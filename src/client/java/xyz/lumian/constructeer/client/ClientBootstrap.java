package xyz.lumian.constructeer.client;

import xyz.lumian.constructeer.client.gui.screen.ModScreens;
import xyz.lumian.constructeer.client.renderer.item.ModItemModels;
import xyz.lumian.constructeer.client.renderer.item.conditional.ModConditionalItemModelProperties;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicates;



//**********************************************************************************************************************
public final class ClientBootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModItemModels                    .initialise();
        ModScreens                       .initialise();
        ModKeybinds                      .initialise();
        ModConditionalItemModelProperties.initialise();
        MenuPredicates                   .initialise();
    }
    
    //******************************************************************************************************************
    private ClientBootstrap() {}
}
