package xyz.lumian.constructeer;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.config.ModServerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;



//**********************************************************************************************************************
public class ConstructeerMain
    implements ModInitializer
{
    //******************************************************************************************************************
    private static final List<Consumer<ModServerConfig>> SERVER_CONFIG_RELOAD_LISTENERS = new ArrayList<>();
    
    //******************************************************************************************************************
    public static void addServerReloadListener(final Consumer<ModServerConfig> listener)
    {
        ConstructeerMain.SERVER_CONFIG_RELOAD_LISTENERS.add(listener);
    }
    
    //==================================================================================================================
    @ApiStatus.Internal
    public static void reloadServerConfig()
    {
        ConstructeerMain.SERVER_CONFIG_RELOAD_LISTENERS.forEach(listener -> listener.accept(ModServerConfig.INSTANCE));
    }
    
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
        
        // We have to build the valid toolset once the server started from the initial configuration
        // This only runs on either the physical integrated client server, or on the physical dedicated server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ConstructeerMain.reloadServerConfig());
        
        ModDefine.LOGGER.info("Mod is ready!");
	}
    
    //==================================================================================================================
    private void reloadConfig(final ModConfig config)
    {
        if (config.getType() == ModConfig.Type.SERVER)
        {
            // running either on an integrated server on the physical client, or we are on a dedicated server
            if (this.server != null)
            {
                ConstructeerMain.reloadServerConfig();
            }
        }
    }
}
