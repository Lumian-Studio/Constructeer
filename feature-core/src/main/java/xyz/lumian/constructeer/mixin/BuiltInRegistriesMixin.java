package xyz.lumian.constructeer.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.impl.BootstrapRegistryExtension;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.CteerRegistryEvents;
import xyz.lumian.constructeer.registry.IBootstrap;

import java.util.*;



//**********************************************************************************************************************
@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin
    implements BootstrapRegistryExtension
{
    //******************************************************************************************************************
    @Unique
    private static final List<IBootstrap.Loader> LOADERS = new ArrayList<>();
    
    @Unique
    private static final Map<String, IBootstrap> BOOTSTRAPPERS = new Object2ObjectArrayMap<>();
    
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger(CteerDefine.formatId("%s-bootstrap"));
    
    @Unique
    private static BootstrapReport REPORT = (FabricLoader.getInstance().isDevelopmentEnvironment()
        ? new BootstrapReport.DebugReport(BuiltInRegistriesMixin.LOGGER)
        : BootstrapReport.EmptyReport.INSTANCE);
    
    //******************************************************************************************************************
    @Unique
    @Override
    public void constructeer$registerBootstrapper(final IBootstrap.Loader loader)
    {
        BuiltInRegistriesMixin.LOADERS.add(loader);
    }
    
    @Override public @Nullable BootstrapReport constructeer$getCurrentReport() { return BuiltInRegistriesMixin.REPORT; }
    
    //==================================================================================================================
    @Inject(
        method = "bootStrap",
        at     = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/core/registries/BuiltInRegistries;createContents()V",
            shift  = At.Shift.AFTER
        )
    )
    private static void bootStrap_before(final CallbackInfo ci)
    {
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_BEFORE.invoker().run();
        
        BuiltInRegistriesMixin.LOGGER.debug("task [<STATIC INITIALISATION>]");
        BuiltInRegistriesMixin.LOADERS.forEach(loader -> loader.apply(getter ->
        {
            final IBootstrap bootstrapper = getter.get();
            final String     id           = bootstrapper.id();
            
            if (BuiltInRegistriesMixin.BOOTSTRAPPERS.put(id, bootstrapper) != null)
            {
                throw new RuntimeException("bootstrapper %s was loaded at least twice".formatted(id));
            }
        }));
        
        BuiltInRegistriesMixin.LOGGER.debug("task [#bootstrap()]");
        BuiltInRegistriesMixin.BOOTSTRAPPERS.forEach((id, value) ->
        {
            BuiltInRegistriesMixin.LOGGER.debug("- bootstrapping '{}'", id);
            value.bootstrap(BuiltInRegistriesMixin.REPORT);
        });
        BuiltInRegistriesMixin.LOADERS.clear();
    }
    
    @Inject(
        method = "bootStrap",
        at     = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V",
            shift  = At.Shift.AFTER
        )
    )
    private static void bootStrap_after(final CallbackInfo ci)
    {
        final MutableInt count = new MutableInt();

        BuiltInRegistriesMixin.LOGGER.debug("task [#freezing()]");
        BuiltInRegistriesMixin.BOOTSTRAPPERS.forEach((id, value) ->
        {
            BuiltInRegistriesMixin.LOGGER.debug("- freezing '{}'", id);
            value.freeze(BuiltInRegistriesMixin.REPORT);
            count.increment();
        });
        BuiltInRegistriesMixin.BOOTSTRAPPERS.clear();
        BuiltInRegistriesMixin.REPORT = BootstrapReport.EmptyReport.INSTANCE;
        BuiltInRegistriesMixin.LOGGER.debug("successfully bootstrapped {} loaders", count);
        
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_AFTER.invoker().run();
    }
}
