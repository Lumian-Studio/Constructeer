package xyz.lumian.constructeer.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.Constructeer;

import java.util.function.Consumer;
import java.util.function.Supplier;



//**********************************************************************************************************************
/// Defines a class a bootstrap class which can be registered with [Constructeer#registerBootstrapper(Loader)].
/// Classes inheriting this interface should only ever be stateless utility classes, not classes that get
/// ordinarily instantiated.
///
/// The bootstrapping methods [#bootstrap(BootstrapReport)] and [#freeze(BootstrapReport)] should never be called
/// manually but instead only ever overridden. Since bootstrappers might be created multiple times, it is best to not
/// do anything in the constructor and to have no member data, they should be purely algorithmic.
public interface IBootstrap
{
    //******************************************************************************************************************
    @FunctionalInterface
    interface Loader
    {
        //**************************************************************************************************************
        Loader BEGIN = (hook -> {});
        
        //**************************************************************************************************************
        void apply(Consumer<Supplier<IBootstrap>> hook);
        
        //==============================================================================================================
        /// Appends a bootstrapper class to the loader.
        /// @param bootstrapper The [Supplier] generating the bootstrapper
        default Loader with(final Supplier<IBootstrap> bootstrapper)
        {
            return (hook ->
            {
                this.apply(hook);
                hook.accept(bootstrapper);
            });
        }
        
        default Loader withBiased(final EnvType side, final Supplier<IBootstrap> bootstrapper)
        {
            return (side == FabricLoader.getInstance().getEnvironmentType() ? this.with(bootstrapper) : this);
        }
        
        /// Concatenates two loaders.
        /// @param attached The [Loader] to attach
        default Loader concat(final Loader attached)
        {
            return (hook ->
            {
                this.apply(hook);
                attached.apply(hook);
            });
        }
    }
    
    //******************************************************************************************************************
    static Supplier<IBootstrap> bootstrappable(final Supplier<String> id, final Consumer<BootstrapReport> bootstrapCallback)
    {
        return (() -> new IBootstrap()
        {
            @Override public void   bootstrap(final BootstrapReport report) { bootstrapCallback.accept(report); }
            @Override public String id()                                    { return id.get(); }
        });
    }
    
    static Supplier<IBootstrap> bootstrappable(final Class<?> id, final Consumer<BootstrapReport> bootstrapCallback)
    {
        return IBootstrap.bootstrappable(id::getName, bootstrapCallback);
    }
    
    static Supplier<IBootstrap> freezable(final Supplier<String> id, final Consumer<BootstrapReport> freezeCallback)
    {
        return (() -> new IBootstrap()
        {
            @Override public void   freeze(final BootstrapReport report) { freezeCallback.accept(report); }
            @Override public String id()                                 { return id.get(); }
        });
    }
    
    static Supplier<IBootstrap> freezable(final Class<?> id, final Consumer<BootstrapReport> freezeCallback)
    {
        return IBootstrap.freezable(id::getName, freezeCallback);
    }
    
    //******************************************************************************************************************
    /// See [CteerRegistryEvents#MC_REGISTRIES_FROZEN_BEFORE].
    @ApiStatus.OverrideOnly
    default void bootstrap(BootstrapReport report) {}
    
    /// See [CteerRegistryEvents#MC_REGISTRIES_FROZEN_AFTER].
    @ApiStatus.OverrideOnly
    default void freeze(BootstrapReport report) {}
    
    //==================================================================================================================
    /// Gets the id class of the bootstrapper, by default this will be the name of the bootstrapping class.
    default String id() { return this.getClass().getName(); }
}
