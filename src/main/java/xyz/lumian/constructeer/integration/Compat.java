package xyz.lumian.constructeer.integration;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.integration.impl.ITrinkets;

import java.util.Optional;
import java.util.function.*;



//**********************************************************************************************************************
public final class Compat
{
    //******************************************************************************************************************
    private static @Nullable ITrinkets TRINKETS;
    
    //******************************************************************************************************************
    public static void initialise()
    {
        // trinkets integration
        TRINKETS = Compat
            .loadIntegrationClass("trinkets", "Trinkets", ITrinkets.class, Compat.class)
            .orElse(null);
    }
    
    //==================================================================================================================
    @SuppressWarnings("NullableProblems")
    public static Optional<ITrinkets> getTrinkets() { return Optional.ofNullable(Compat.TRINKETS); }
    
    //==================================================================================================================
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> loadIntegrationClass(final String   requiredModId,
                                                       final String   className,
                                                       final Class<T> integrationClass,
                                                       final Class<?> lookup)
    {
        if (!FabricLoader.getInstance().isModLoaded(requiredModId))
        {
            ModDefine.LOGGER.info("mod '{}' not found, disabling integration", requiredModId);
            return Optional.empty();
        }
        
        try
        {
            final ClassLoader   loader      = lookup.getClassLoader();
            final Class<?>      integration = Class.forName((lookup.getPackageName() + "." + className), true, loader);
            
            if (!integrationClass.isAssignableFrom(integration))
            {
                throw new RuntimeException(
                    "invalid integration, is not inheriting from " + integrationClass.getSimpleName());
            }
            
            final T instance = ((Class<T>) integration).getConstructor().newInstance();
            ModDefine.LOGGER.info("successfully loaded integration for mod '{}'", requiredModId);
            
            return Optional.of(instance);
        }
        catch (final Exception ex)
        {
            ModDefine.LOGGER.error("integration for mod '{}' could not be loaded", requiredModId, ex);
        }
        
        return Optional.empty();
    }
    
    //******************************************************************************************************************
    private Compat() {}
}
