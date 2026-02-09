package xyz.lumian.constructeer.toolbelt;

import net.fabricmc.api.ModInitializer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.toolbelt.config.CteerToolbeltServerConfig;
import xyz.lumian.constructeer.toolbelt.container.CteerToolbeltMenus;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.item.component.predicate.CteerDataComponentPredicates;
import xyz.lumian.constructeer.toolbelt.item.recipe.CteerToolbeltRecipeSerialisers;
import xyz.lumian.constructeer.toolbelt.network.CteerToolbeltPayloads;



//**********************************************************************************************************************
public class CteerToolbelt
    implements ModInitializer
{
    //******************************************************************************************************************
    @Override
    public void onInitialize()
    {
        CteerConfigManager.INSTANCE.registerReloadableConfig(
            CteerDefine.id(ModuleDefine.ID),
            CteerToolbeltServerConfig.INSTANCE);
        CteerToolbeltItems            .initialise();
        CteerToolbeltPayloads         .initialise();
        CteerToolbeltRecipeSerialisers.initialise();
        CteerDataComponentPredicates  .initialise();
        CteerToolbeltMenus            .initialise();
    }
}
