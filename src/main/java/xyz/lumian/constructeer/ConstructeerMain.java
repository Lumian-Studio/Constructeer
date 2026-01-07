package xyz.lumian.constructeer;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.item.ItemAttorney;

import java.util.*;



//**********************************************************************************************************************
public class ConstructeerMain
    implements ModInitializer
{
    //******************************************************************************************************************
    private @Nullable MinecraftServer server;
    
    //******************************************************************************************************************
	@Override
	public void onInitialize()
    {
		ModDefine.LOGGER.info("Initialising mod");
		Bootstrap.initialise();
  
		ModDefine.LOGGER.info("Loading configurations...");
        
        ConfigRegistry.INSTANCE.register(ModDefine.MOD_ID, ModConfig.Type.SERVER, ModServerConfig.SPEC);
        ModConfigEvents.reloading(ModDefine.MOD_ID).register(this::reloadConfig);
        
        // Keeping an instance of the server when we reload the config
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerLifecycleEvents.SERVER_STOPPED .register(server -> this.server = null);
        
        // Reload whatever can be reloaded once any of our configs reloaded
        ModConfigEvents.reloading(ModDefine.MOD_ID).register(this::reloadConfig);
        
        // We have to build the valid toolset once the server started from the initial configuration
        // This only runs on either the physical integrated client server, or on the physical dedicated server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ItemAttorney.updateValidTools(server.registryAccess()));
        
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
        {
            ServerLifecycleEvents.SERVER_STARTED.register(server ->
            {
                if (ItemAttorney.getValidPouchTools().get().size() == 0)
                {
                    ModDefine.LOGGER.warn("Valid item tool set was empty");
                }
            });
        }
        
        ModDefine.LOGGER.info("Constructeer initialized");
	}
    
    //==================================================================================================================
    private void reloadConfig(final ModConfig config)
    {
        if (config.getType() == ModConfig.Type.SERVER)
        {
            // running either on an integrated server on the physical client, or we are on a dedicated server
            if (this.server != null)
            {
                ItemAttorney.updateValidTools(this.server.registryAccess());
            }
        }
    }
}
