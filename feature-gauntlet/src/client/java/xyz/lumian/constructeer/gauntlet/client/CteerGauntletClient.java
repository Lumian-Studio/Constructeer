package xyz.lumian.constructeer.gauntlet.client;

import net.fabricmc.api.ClientModInitializer;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.gauntlet.client.renderer.GauntletRenderer;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.PortableRenderRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public class CteerGauntletClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(PortableRenderRegistry::new)
        .with(IBootstrap.bootstrappable(GauntletRenderer.class, (report -> GauntletRenderer.initialise())));
    
    //******************************************************************************************************************
    @Override
    public void onInitializeClient()
    {
        Constructeer.registerBootstrapper(CteerGauntletClient.LOADER);
    }
}
