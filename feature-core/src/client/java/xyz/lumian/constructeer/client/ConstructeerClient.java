package xyz.lumian.constructeer.client;

import net.fabricmc.api.ClientModInitializer;
import xyz.lumian.constructeer.client.renderer.item.conditional.CteerConditionalItemModelProperties;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicates;



//**********************************************************************************************************************
public class ConstructeerClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
    @Override
    public void onInitializeClient()
    {
        CteerConditionalItemModelProperties.initialise();
        MenuPredicates                     .initialise();
    }
}
