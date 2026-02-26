package xyz.lumian.constructeer.toolbelt;

import net.fabricmc.api.ModInitializer;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.toolbelt.config.CteerToolbeltServerConfig;
import xyz.lumian.constructeer.toolbelt.container.CteerToolbeltMenus;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.predicate.CteerDataComponentPredicates;
import xyz.lumian.constructeer.toolbelt.item.recipe.CteerToolbeltRecipeSerialisers;
import xyz.lumian.constructeer.toolbelt.network.CteerToolbeltPayloads;



//**********************************************************************************************************************
public class CteerToolbelt
    implements ModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerToolbeltItems::new)
        .with(CteerToolbeltPayloads::new)
        .with(CteerToolbeltRecipeSerialisers::new)
        .with(CteerDataComponentPredicates::new)
        .with(CteerToolbeltDataComponents::new)
        .with(CteerToolbeltMenus::new);
    
    //******************************************************************************************************************
    @Override
    public void onInitialize()
    {
        Constructeer.registerBootstrapper(CteerToolbelt.LOADER);
        CteerConfigManager.INSTANCE.registerReloadableConfig(
            CteerDefine.id(ModuleDefine.ID),
            CteerToolbeltServerConfig.INSTANCE);
    }
}
