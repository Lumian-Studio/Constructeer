package xyz.lumian.constructeer;

import xyz.lumian.constructeer.container.ModMenus;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.recipe.ModRecipeSerialisers;
import xyz.lumian.constructeer.network.ModPayloads;



//**********************************************************************************************************************
public class Bootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModComponents       .initialise();
        ModItems            .initialise();
        ModMenus            .initialise();
        ModPayloads         .initialise();
        ModRecipeSerialisers.initialise();
        Compat              .initialise();
    }
}
