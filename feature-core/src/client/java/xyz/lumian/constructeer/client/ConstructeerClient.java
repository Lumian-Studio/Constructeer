package xyz.lumian.constructeer.client;

import net.fabricmc.api.ClientModInitializer;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.client.network.CteerNetworkClient;
import xyz.lumian.constructeer.client.registry.CteerRenderRegistry;
import xyz.lumian.constructeer.client.registry.CteerConditionalItemModelPropertyRegistry;
import xyz.lumian.constructeer.client.registry.CteerMenuPredicateRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public class ConstructeerClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerNetworkClient::new)
        .with(CteerConditionalItemModelPropertyRegistry::new)
        .with(CteerMenuPredicateRegistry::new)
        .with(CteerRenderRegistry::new);
    
    //******************************************************************************************************************
    @Override
    public void onInitializeClient()
    {
        Constructeer.registerBootstrapper(ConstructeerClient.LOADER);
    }
}
