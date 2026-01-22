package xyz.lumian.constructeer.client;

import xyz.lumian.constructeer.client.gui.screen.ModScreens;
import xyz.lumian.constructeer.client.integration.ClientCompat;
import xyz.lumian.constructeer.client.model.ModModelLayers;
import xyz.lumian.constructeer.client.network.ModClientPayloadReceiver;
import xyz.lumian.constructeer.client.renderer.ModRenderer;
import xyz.lumian.constructeer.client.renderer.item.conditional.ModConditionalItemModelProperties;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicates;
import xyz.lumian.constructeer.item.component.MultiMining;



//**********************************************************************************************************************
public final class ClientBootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModScreens              .initialise();
        ModKeybinds             .initialise();
        ModModelLayers          .initialise();
        ClientCompat            .initialise();
        ModClientPayloadReceiver.initialise();
        ModRenderer             .initialise();
    }
    
    //******************************************************************************************************************
    private ClientBootstrap() {}
}
