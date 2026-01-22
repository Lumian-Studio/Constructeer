package xyz.lumian.constructeer.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.entity.FallingObjectEntity;

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
        
        for (final var part : renderState.subRenderState.parts)
        {
            final BlockState state = part.state();
            
            if (state.getRenderShape() == RenderShape.MODEL)
            {
                final Vec3i offset = part.offset();
                
                pose.pushPose();
                pose.translate(offset.getX(), offset.getY(), offset.getZ());
                nodes.submitBlock(pose, part.state(), light, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
                pose.popPose();
            }
        }
    }
}
