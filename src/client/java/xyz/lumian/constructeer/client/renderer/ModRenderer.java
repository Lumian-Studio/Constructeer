package xyz.lumian.constructeer.client.renderer;


import xyz.lumian.constructeer.client.renderer.entity.ModEntityRenderers;
import xyz.lumian.constructeer.client.renderer.item.ModItemModels;
import xyz.lumian.constructeer.client.renderer.item.conditional.ModConditionalItemModelProperties;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicates;
import xyz.lumian.constructeer.client.renderer.layer.ModRenderLayers;
import xyz.lumian.constructeer.client.renderer.state.ModRenderDataKeys;



//**********************************************************************************************************************
public final class ModRenderer
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModRenderDataKeys                  .initialise();
        ModRenderLayers                    .initialise();
        ModItemModels                      .initialise();
        ModConditionalItemModelProperties  .initialise();
        MenuPredicates                     .initialise();
        ModEntityRenderers                 .initialise();
        MultiMiningOutlineRenderer.INSTANCE.initialise();
    }
    
    //******************************************************************************************************************
    private ModRenderer() {}
}
