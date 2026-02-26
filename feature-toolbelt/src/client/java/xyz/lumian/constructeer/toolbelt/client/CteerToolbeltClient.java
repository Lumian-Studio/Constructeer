package xyz.lumian.constructeer.toolbelt.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.fml.config.ModConfig;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.toolbelt.ModuleDefine;
import xyz.lumian.constructeer.toolbelt.client.config.CteerToolbeltClientConfig;
import xyz.lumian.constructeer.toolbelt.client.gui.screen.CteerToolbeltMenuScreens;
import xyz.lumian.constructeer.toolbelt.client.renderer.ToolbeltRenderer;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.CteerToolbeltItemModels;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.CteerToolbeltConditionalItemModelProperties;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.predicate.CteerToolbeltMenuPredicates;



//**********************************************************************************************************************
public class CteerToolbeltClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerToolbeltKeybinds::new)
        .with(CteerToolbeltItemModels::new)
        .with(CteerToolbeltConditionalItemModelProperties::new)
        .with(CteerToolbeltMenuScreens::new)
        .with(CteerToolbeltMenuPredicates::new)
        .with(IBootstrap.bootstrappable(ToolbeltRenderer.class, (report -> ToolbeltRenderer.initialise())));
    
    //******************************************************************************************************************
    @Override
    public void onInitializeClient()
    {
        Constructeer.registerBootstrapper(CteerToolbeltClient.LOADER);
        CteerConfigManager.INSTANCE.registerConfig(
            CteerDefine.id(ModuleDefine.ID),
            ModConfig.Type.CLIENT,
            CteerToolbeltClientConfig.SPEC);
    }
}
