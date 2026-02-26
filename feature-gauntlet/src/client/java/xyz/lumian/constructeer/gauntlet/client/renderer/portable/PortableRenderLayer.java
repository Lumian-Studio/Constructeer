package xyz.lumian.constructeer.gauntlet.client.renderer.portable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.gauntlet.client.renderer.state.CteerGauntletRenderDataKeys;
import xyz.lumian.constructeer.gauntlet.item.portable.Portable;



//**********************************************************************************************************************
public class PortableRenderLayer
    extends RenderLayer<AvatarRenderState, PlayerModel>
{
    //******************************************************************************************************************
    public PortableRenderLayer(final RenderLayerParent<AvatarRenderState, PlayerModel> parent) { super(parent); }
    
    //==================================================================================================================
    @Override
    public void submit(final PoseStack pose, final SubmitNodeCollector nodes, final int packedLight,
                       final AvatarRenderState state, final float yRot, final float xRot)
    {
        final Portable<?> portable = state.getData(CteerGauntletRenderDataKeys.PORTABLE);
        this.render(portable, pose, nodes, packedLight, state, yRot, xRot);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private <T> void render(final @Nullable Portable<T> portable, final PoseStack pose, final SubmitNodeCollector nodes,
                            final int light, final AvatarRenderState renderState, final float yRot, final float xRot)
    {
        if (portable == null)
        {
            return;
        }
        
        @SuppressWarnings("unchecked")
        final IPortableRenderer<T> renderer = ((PortableRenderExtension<T>) portable.type()).getRenderer();
        
        if (renderer != null)
        {
            renderer.renderLayer(portable.object(), this, pose, nodes, light, renderState, yRot, xRot);
        }
    }
}
