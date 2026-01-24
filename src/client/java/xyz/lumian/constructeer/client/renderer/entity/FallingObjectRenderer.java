package xyz.lumian.constructeer.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.lumian.constructeer.entity.FallingObjectEntity;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;

import java.util.Objects;



//**********************************************************************************************************************
public class FallingObjectRenderer
    extends DisplayRenderer<FallingObjectEntity, FallingObjectEntity.RenderState, FallingObjectRenderer.RenderState>
{
    //******************************************************************************************************************
    public static class RenderState
        extends DisplayEntityRenderState
    {
        //**************************************************************************************************************
        @Nullable
        public FallingObjectEntity.RenderState subRenderState;
        
        //**************************************************************************************************************
        @Override public boolean hasSubState() { return (this.subRenderState != null); }
    }
    
    //******************************************************************************************************************
    protected FallingObjectRenderer(final EntityRendererProvider.Context context) { super(context); }
    
    //==================================================================================================================
    @Override public RenderState createRenderState() { return new RenderState(); }
    
    @Override
    public void extractRenderState(final FallingObjectEntity display, final RenderState state, final float f)
    {
        super.extractRenderState(display, state, f);
        state.subRenderState = display.getRenderState();
    }
    
    //==================================================================================================================
    @Override
    protected void submitInner(final RenderState renderState, final PoseStack pose, final SubmitNodeCollector nodes,
                               final int light, final float partialTick)
    {
        Objects.requireNonNull(renderState.subRenderState);
        
        final MultiMiningBox box = renderState.subRenderState.box;
        {
            final Vec3i       normal = renderState.subRenderState.direction.getUnitVec3i();
            final Vector3f    motion = renderState.subRenderState.motion;
            final Quaternionf quat   = new Quaternionf(new AxisAngle4f(
                renderState.subRenderState.rotation,
                normal.getZ(), 0, (normal.getX() * -1)));
            pose.rotateAround(quat, motion.x(), motion.y(), motion.z());
        }
        
        for (final var part : box.parts())
        {
            final BlockState state = part.state();
            
            if (state.getRenderShape() == RenderShape.MODEL)
            {
                pose.pushPose();
                {
                    final Vec3i offset = part.offset();
                    final Vec3  base   = box.base();
                    pose.translate((offset.getX() - base.x()), offset.getY(), (offset.getZ() - base.z()));
                    nodes.submitBlock(pose, state, light, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
                }
                pose.popPose();
            }
        }
    }
}
