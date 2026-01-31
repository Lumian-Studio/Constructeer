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
package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.MultiMining;
import xyz.lumian.constructeer.item.multimining.MultiMiningType;
import xyz.lumian.constructeer.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.item.multimining.timber.IJustinTimbermode;

import java.util.Arrays;



//**********************************************************************************************************************
public final class ModRegistries
{
    //******************************************************************************************************************
    public static final class BuiltIn
    {
        //**************************************************************************************************************
        // Static registries
        
        // We make it optional, as we have our own synchronisation going on
        public static final Registry<AreaProviderType<?>> AREA_PROVIDER_TYPE
            = registerOptional(ModRegistries.AREA_PROVIDER_TYPE, null);
        
        public static final Registry<IJustinTimbermode> MULTI_MINING_TIMBER_MODE
            = registerSynced(ModRegistries.MULTI_MINING_TIMBER_MODE, null);
        
        public static final Registry<MultiMiningType<?>> MULTI_MINING_TYPE
            = registerSynced(ModRegistries.MULTI_MINING_TYPE, null);
        
        //**************************************************************************************************************
        @SuppressWarnings("SameParameterValue")
        private static <T> Registry<T> registerOptional(final           ResourceKey<Registry<T>> key,
                                                        final @Nullable Identifier               defaultId)
        {
            return BuiltIn.register(key, defaultId, RegistryAttribute.OPTIONAL);
        }
        
        @SuppressWarnings("SameParameterValue")
        private static <T> Registry<T> registerSynced(final           ResourceKey<Registry<T>> key,
                                                      final @Nullable Identifier               defaultId)
        {
            return BuiltIn.register(key, defaultId, RegistryAttribute.SYNCED);
        }
        
        private static <T> Registry<T> registerOptionalSynced(final           ResourceKey<Registry<T>> key,
                                                              final @Nullable Identifier               defaultId)
        {
            return BuiltIn.register(key, defaultId, RegistryAttribute.OPTIONAL, RegistryAttribute.SYNCED);
        }
        
        private static <T> Registry<T> register(final           ResourceKey<Registry<T>> key,
                                                final @Nullable Identifier               defaultId,
                                                final           RegistryAttribute     ...attributes)
        {
            final FabricRegistryBuilder<T, ?> builder = (defaultId != null
                ? FabricRegistryBuilder.createDefaulted(key, defaultId)
                : FabricRegistryBuilder.createSimple(key));
            Arrays.stream(attributes).forEach(builder::attribute);
            return builder.buildAndRegister();
        }
        
        //**************************************************************************************************************
        public static void initialise()
        {
            DynamicRegistries.registerSynced(ModRegistries.MULTI_MINING_PROVIDER, MultiMining.MAP_CODEC.codec());
        }
        
        //**************************************************************************************************************
        private BuiltIn() {}
    }
    
    //******************************************************************************************************************
    // Dynamic registry keys
    public static final ResourceKey<Registry<MultiMining>> MULTI_MINING_PROVIDER
        = createKey("multi_mining_provider");
    
    // Static registry keys
    public static final ResourceKey<Registry<AreaProviderType<?>>> AREA_PROVIDER_TYPE
        = createKey("area_provider_type");
    
    public static final ResourceKey<Registry<IJustinTimbermode>> MULTI_MINING_TIMBER_MODE
        = createKey("multi_mining_timber_mode");
    
    public static final ResourceKey<Registry<MultiMiningType<?>>> MULTI_MINING_TYPE
        = createKey("multi_mining_type");
    
    //******************************************************************************************************************
    private static <T> ResourceKey<Registry<T>> createKey(final String name)
    {
        return ResourceKey.createRegistryKey(ModDefine.id(name));
    }
    
    //******************************************************************************************************************
    public static void initialise() { BuiltIn.initialise(); }
    
    //******************************************************************************************************************
    private ModRegistries() {}
}
