package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.item.multimining.predicate.MultiMiningPredicateType;
import xyz.lumian.constructeer.item.multimining.predicate.ToolPredicate;
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
        
        public static final Registry<MultiMiningPredicateType<?>> MULTI_MINING_PREDICATE_TYPE
            = registerSynced(ModRegistries.MULTI_MINING_PREDICATE_TYPE, null);
        
        public static final Registry<IJustinTimbermode> TIMBER_MODE
            = registerSynced(ModRegistries.TIMBER_MODE, null);
        
        //**************************************************************************************************************
        private static <T> Registry<T> registerOptional(final           ResourceKey<Registry<T>> key,
                                                        final @Nullable Identifier               defaultId)
        {
            return BuiltIn.register(key, defaultId, RegistryAttribute.OPTIONAL);
        }
        
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
            // Dynamic registries
            DynamicRegistries.registerSynced(ModRegistries.TOOL_PREDICATE, ToolPredicate.CODEC);
        }
        
        //**************************************************************************************************************
        private BuiltIn() {}
    }
    
    //******************************************************************************************************************
    // Dynamic registry keys
    public static final ResourceKey<Registry<ToolPredicate>> TOOL_PREDICATE
        = createKey("tool_predicate");
    
    // Static registry keys
    public static final ResourceKey<Registry<AreaProviderType<?>>> AREA_PROVIDER_TYPE
        = createKey("area_provider_type");
    
    public static final ResourceKey<Registry<MultiMiningPredicateType<?>>> MULTI_MINING_PREDICATE_TYPE
        = createKey("multi_mining_predicate_type");
    
    public static final ResourceKey<Registry<IJustinTimbermode>> TIMBER_MODE
        = createKey("multi_mining_timber_mode");
    
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
