package xyz.lumian.constructeer.toolbelt.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.fml.config.ModConfig;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.toolbelt.ModuleDefine;
import xyz.lumian.constructeer.toolbelt.client.config.CteerToolbeltClientConfig;
import xyz.lumian.constructeer.toolbelt.client.gui.screen.CteerToolbeltMenuScreens;
import xyz.lumian.constructeer.toolbelt.client.model.CteerToolbeltModelLayers;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.CteerToolbeltItemModels;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.CteerToolbeltConditionalItemModelProperties;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.predicate.CteerToolbeltMenuPredicates;
import xyz.lumian.constructeer.toolbelt.client.renderer.layer.CteerToolbeltRenderLayers;
import xyz.lumian.constructeer.toolbelt.item.recipe.CteerToolbeltRecipeSerialisers;



//**********************************************************************************************************************
public class CteerToolbeltClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
    @Override
    public void onInitializeClient()
    {
        CteerConfigManager.INSTANCE.registerConfig(
            CteerDefine.id(ModuleDefine.ID),
            ModConfig.Type.CLIENT,
            CteerToolbeltClientConfig.SPEC);
        
        CteerToolbeltKeybinds                      .initialise();
        CteerToolbeltModelLayers                   .initialise();
        CteerToolbeltRenderLayers                  .initialise();
        CteerToolbeltItemModels                    .initialise();
        CteerToolbeltConditionalItemModelProperties.initialise();
        CteerToolbeltMenuScreens                   .initialise();
        CteerToolbeltMenuPredicates                .initialise();
        CteerToolbeltRecipeSerialisers             .initialise();
    }
}
