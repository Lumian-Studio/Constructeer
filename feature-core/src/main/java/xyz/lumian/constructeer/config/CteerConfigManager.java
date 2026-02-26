package xyz.lumian.constructeer.config;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.util.BiasedUtil;
import xyz.lumian.constructeer.util.FreezableMap;

import java.util.function.Function;



//**********************************************************************************************************************
@ApiStatus.NonExtendable
public class CteerConfigManager
{
    //******************************************************************************************************************
    public static final CteerConfigManager INSTANCE = BiasedUtil
        .loadProtected("xyz.lumian.constructeer.client.config.CteerClientConfigManager", CteerConfigManager.class);
    
    //******************************************************************************************************************
    @ApiStatus.Internal
    public static void freeze(final BootstrapReport report) { CteerConfigManager.INSTANCE.setupEvents(report); }
    
    //==================================================================================================================
    public static <T> Pair<T, ModConfigSpec> buildConfig(final Function<ModConfigSpec.Builder, T> generator)
    {
        return (new ModConfigSpec.Builder()).configure(generator);
    }
    
    //******************************************************************************************************************
    private final FreezableMap<Identifier, ReloadableConfig<?>> reloadableConfigs
        = new FreezableMap<>(new Object2ObjectOpenHashMap<>());
    
    private @Nullable MinecraftServer server = null;
    
    //******************************************************************************************************************
    protected CteerConfigManager()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerLifecycleEvents.SERVER_STOPPED .register(server -> this.server = null);
        
        // Loads configs on server start, physical client (in singleplayer) and dedicated server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.reloadAll(server.registryAccess()));
    }
    
    //==================================================================================================================
    @Nullable
    public HolderLookup.Provider getLookup()
    {
        return (this.server != null ? this.server.registryAccess() : null);
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
    
    //==================================================================================================================
    protected void reloadAll(final HolderLookup.Provider lookup)
    {
        this.reloadableConfigs.values().forEach(config -> config.invoke(lookup));
    }
    
    //==================================================================================================================
    private void setupEvents(final BootstrapReport report)
    {
        this.reloadableConfigs.freeze();
        
        if (!this.reloadableConfigs.isEmpty())
        {
            report.report("hooking into reloadable server configs");
            this.reloadableConfigs.forEach((id, config) ->
            {
                ModConfigEvents.reloading(id.getNamespace() + '-' + id.getPath()).register(mod_cfg ->
                {
                    final HolderLookup.Provider lookup = this.getLookup();
                    
                    if (lookup == null || mod_cfg.getType() != ModConfig.Type.SERVER)
                    {
                        return;
                    }
                    
                    config.invoke(lookup);
                });
                report.report("* config '%s'", id);
            });
        }
    }
}
