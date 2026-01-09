package xyz.lumian.constructeer.client.renderer.layer;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;



//**********************************************************************************************************************
public final class ModRenderLayers
{
    //******************************************************************************************************************
    static
    {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, helper, context) ->
        {
            switch (renderer)
            {
                case HumanoidMobRenderer<?,?,?> rend -> helper.register(ModRenderLayers.generateLayer(rend, context));
                case AvatarRenderer<?>          rend -> helper.register(ModRenderLayers.generateLayer(rend, context));
                default -> {}
            }
        });
    }
    
    //******************************************************************************************************************
    private static <S extends HumanoidRenderState, M extends EntityModel<S>> ToolbeltRenderLayer<S, M> generateLayer(
        final RenderLayerParent<S, M>        renderer,
        final EntityRendererProvider.Context context
    )
    {
        return new ToolbeltRenderLayer<>(renderer, context);
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private ModRenderLayers() {}
}
