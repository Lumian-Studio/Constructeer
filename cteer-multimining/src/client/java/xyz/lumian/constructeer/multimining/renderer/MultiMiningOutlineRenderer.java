/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.multimining.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.multimining.config.ModClientConfig;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.MultiMining;
import xyz.lumian.constructeer.multimining.tag.ModItemTags;
import xyz.lumian.constructeer.multimining.util.BlockContext;

import java.util.*;



//**********************************************************************************************************************
enum MultiMiningOutlineRenderer
{
    INSTANCE;
    
    //******************************************************************************************************************
    public static class MutableShape
    {
        //**************************************************************************************************************
        private static VoxelShape makeBlock(final double x, final double y, final double z)
        {
            return Shapes.box(x, y, z, (x + 1), (y + 1), (z + 1));
        }
        
        //**************************************************************************************************************
        private @Nullable VoxelShape   shape;
        private @Nullable BlockContext block;
        
        //**************************************************************************************************************
        public boolean isDirty(final BlockContext newBlock, final Direction face)
        {
            return (!newBlock.equals(this.block) || !this.block.is(newBlock.state()));
        }
        
        //==============================================================================================================
        public void setBlock(final BlockContext block)
        {
            this.block = Objects.requireNonNull(block);
            this.shape = Shapes.block();
        }
        
        public void addBlock(final BlockContext toRender)
        {
            if (this.shape == null)
            {
                return;
            }
            
            assert (this.block != null);
            
            final int x = (toRender.pos().getX() - this.block.pos().getX());
            final int y = (toRender.pos().getY() - this.block.pos().getY());
            final int z = (toRender.pos().getZ() - this.block.pos().getZ());
            
            this.shape = Shapes.joinUnoptimized(this.shape, MutableShape.makeBlock(x, y, z), BooleanOp.OR);
        }
        
        public void reset()
        {
            this.shape = null;
            this.block = null;
        }
        
        public void doNotRender() { this.shape = null; }
        
        //==============================================================================================================
        public void render(final WorldRenderContext ctx, final int rgb)
        {
            if (this.shape != null)
            {
                assert (this.block != null);
                
                final Vec3           cam_pos  = ctx.worldState().cameraRenderState.pos;
                final double         draw_x   = (this.block.pos().getX() - cam_pos.x);
                final double         draw_y   = (this.block.pos().getY() - cam_pos.y);
                final double         draw_z   = (this.block.pos().getZ() - cam_pos.z);
                final VertexConsumer vertices = ctx.consumers().getBuffer(RenderTypes.lines());
                final int            colour   = ((rgb & 0xFFFFFF) | 0x55000000);
                ShapeRenderer.renderShape(ctx.matrices(), vertices, this.shape, draw_x, draw_y, draw_z, colour, 3f);
            }
        }
    }
    
    //******************************************************************************************************************
    private final MutableShape shape = new MutableShape();
    
    private           int       ticks        = 0;
    private           boolean   initialised  = false;
    private           boolean   prevSneaking = false;
    private @Nullable Direction prevFace     = null;
    
    //******************************************************************************************************************
    public void initialise()
    {
        if (this.initialised)
        {
            return;
        }
        
        ClientTickEvents .START_CLIENT_TICK   .register(this::tick);
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, state) ->
        {
            this.renderOutline(ctx, state);
            return true;
        });
        
        this.initialised = true;
    }
    
    //==================================================================================================================
    public void tick(final Minecraft client)
    {
        if (client.level != null)
        {
            ++this.ticks;
            
            if ((this.ticks % 10) == 0)
            {
                this.shape.reset();
            }
        }
    }
    
    public void renderOutline(final WorldRenderContext ctx, final BlockOutlineRenderState state)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        final Player    player    = minecraft.player;
        
        if (player == null || player.isCreative() || !(minecraft.hitResult instanceof BlockHitResult hit_result))
        {
            return;
        }
        
        final ItemStack   stack = player.getMainHandItem();
        final MultiMining mm    = stack.get(ModComponents.MULTI_MINING);
        
        if (mm == null)
        {
            return;
        }
        
        final int rgb;
        
        if (stack.is(ModItemTags.HAMMERS))
        {
            if (!ModClientConfig.INSTANCE.shouldRenderHammerOutline().getAsBoolean())
            {
                return;
            }
            
            rgb = ModClientConfig.INSTANCE.hammerOutlineColour().getAsInt();
        }
        else if (stack.is(ModItemTags.PLOWS))
        {
            if (!ModClientConfig.INSTANCE.shouldRenderPlowOutline().getAsBoolean())
            {
                return;
            }
            
            rgb = ModClientConfig.INSTANCE.plowOutlineColour().getAsInt();
        }
        else if (mm.outlineRenderColour() != 0)
        {
            rgb = (0xFFFFFF & mm.outlineRenderColour());
        }
        else return;
        
        final BlockContext block = BlockContext.forLevel(player.level(), state.pos());
        final Direction    face  = hit_result.getDirection();
        final boolean      sneak = player.isCrouching();
        
        if (this.shape.isDirty(block, face) || this.prevFace != face || this.prevSneaking != sneak)
        {
            final MultiMining.Action action = mm.execute(face, player, stack, block);
            
            this.shape.setBlock(block);
            this.prevFace     = face;
            this.prevSneaking = sneak;
            
            if (action.result() != MultiMining.Result.SUCCESS)
            {
                this.shape.doNotRender();
                return;
            }
            
            for (final var to_render : action.blocks())
            {
                if (!to_render.equals(block))
                {
                    this.shape.addBlock(to_render);
                }
            }
        }
        
        this.shape.render(ctx, rgb);
    }
}
