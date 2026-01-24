package xyz.lumian.constructeer;

import xyz.lumian.constructeer.container.ModMenus;
import xyz.lumian.constructeer.entity.ModEntities;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.MultiMining;
import xyz.lumian.constructeer.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.item.multimining.predicate.MultiMiningPredicateType;
import xyz.lumian.constructeer.item.multimining.timber.TimberMode;
import xyz.lumian.constructeer.item.recipe.ModRecipeSerialisers;
import xyz.lumian.constructeer.network.ModPayloads;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.sound.ModSoundEvents;
import xyz.lumian.constructeer.stat.ModStats;



//**********************************************************************************************************************
public class Bootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModRegistries           .initialise();
        ModComponents           .initialise();
        ModItems                .initialise();
        ModMenus                .initialise();
        ModPayloads             .initialise();
        ModRecipeSerialisers    .initialise();
        Compat                  .initialise();
        MultiMining             .initialise();
        ModStats                .initialise();
        AreaProviderType        .initialise();
        MultiMiningPredicateType.initialise();
        TimberMode              .initialise();
        ModEntities             .initialise();
        ModSoundEvents          .initialise();
    }
}
