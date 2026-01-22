package xyz.lumian.constructeer.client;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ModConfig;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.client.config.ModClientConfig;



//**********************************************************************************************************************
public class ConstructeerClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
	@Override
	public void onInitializeClient()
    {
		ClientBootstrap.initialise();
        ConfigRegistry.INSTANCE.register(ModDefine.MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        
        // Reload whatever can be reloaded once any of our configs reloaded
        ModConfigEvents.reloading(ModDefine.MOD_ID).register(this::reloadConfig);
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> ConstructeerMain.reloadServerConfig()));
	}
    
    //==================================================================================================================
    private void reloadConfig(final ModConfig config)
    {
        if (config.getType() == ModConfig.Type.SERVER)
        {
            this.reloadValidTools(Minecraft.getInstance());
        }
    }
    
    private void reloadValidTools(final Minecraft mc)
    {
        // we are on the physical client and on a dedicated server
        if (mc.level != null && mc.getSingleplayerServer() == null)
        {
            ConstructeerMain.reloadServerConfig();
        }
    }
}
