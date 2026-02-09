package xyz.lumian.constructeer.config;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.registry.CteerRegistryEvents;
import xyz.lumian.constructeer.util.BiasedUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;



//**********************************************************************************************************************
public class CteerConfigManager
{
    //******************************************************************************************************************
    public static final CteerConfigManager INSTANCE;
    
    //==================================================================================================================
    static
    {
        INSTANCE = BiasedUtil
            .load("xyz.lumian.constructeer.client.config.CteerClientConfigManager", CteerConfigManager.class);
    }
    
    //******************************************************************************************************************
    public static <T> Pair<T, ModConfigSpec> buildConfig(final Function<ModConfigSpec.Builder, T> generator)
    {
        return (new ModConfigSpec.Builder()).configure(generator);
    }
    
    //******************************************************************************************************************
    private final Map<Identifier, ReloadableConfig<?>> reloadableConfigs = new HashMap<>();
    
    private @Nullable MinecraftServer server = null;
    
    //******************************************************************************************************************
    public CteerConfigManager()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
        {
            if (this.server == null)
            {
                this.server = server;
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            if (this.server == server)
            {
                this.server = null;
            }
        });
        
        /// Loads configs on server start, physical client (in singleplayer) and dedicated server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.initConfigs(server.registryAccess()));
        
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_AFTER.register(() -> this.reloadableConfigs.forEach((id, config) ->
        {
            final String name = (id.getNamespace() + '-' + id.getPath());
            ModConfigEvents.reloading(name).register(mod_cfg ->
            {
                final HolderLookup.Provider lookup = this.getLookup();
                
                if (lookup == null || mod_cfg.getType() != ModConfig.Type.SERVER)
                {
                    return;
                }
                
                this.reloadableConfigs.get(id).invoke(lookup);
            });
        }));
    }
    
    //==================================================================================================================
    public void registerConfig(final Identifier id, final ModConfig.Type type, final ModConfigSpec spec)
    {
        final String file = "%s/%s-%s.toml".formatted(id.getNamespace(), id.getPath(), type.extension());
        final String name = (id.getNamespace() + '-' + id.getPath());
        ConfigRegistry.INSTANCE.register(name, type, spec, file);
    }
    
    public <T extends ReloadableConfig<T>> void registerReloadableConfig(final Identifier id, final T config)
    {
        this.registerConfig(id, ModConfig.Type.SERVER, config.spec());
        
        if (this.reloadableConfigs.put(id, config) != null)
        {
            throw new IllegalStateException("Reloadable config '" + id + "' already registered");
        }
    }

    @Nullable
    public HolderLookup.Provider getLookup()
    {
        return (this.server != null ? this.server.registryAccess() : null);
    }
    
    //==================================================================================================================
    private void initConfigs(final HolderLookup.Provider lookup)
    {
        this.reloadableConfigs.values().forEach(config -> config.invoke(lookup));
    }
}
