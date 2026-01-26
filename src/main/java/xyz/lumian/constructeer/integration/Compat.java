/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.integration;

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
