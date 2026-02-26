package xyz.lumian.constructeer.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.Constructeer;

import java.util.Arrays;



//**********************************************************************************************************************
public final class CteerRegistries
{
    //******************************************************************************************************************
    public static <T> ResourceKey<Registry<T>> createKey(final Identifier name)
    {
        return ResourceKey.createRegistryKey(name);
    }
    
    //==================================================================================================================
    public static <T> Registry<T> registerRegistry(final           Identifier        id,
                                                   final @Nullable Identifier        defaultId,
                                                   final           RegistryAttribute ...attributes)
    {
        return CteerRegistries.registerRegistry(CteerRegistries.createKey(id), defaultId, attributes);
    }
    
    public static <T> Registry<T> registerRegistry(final           ResourceKey<Registry<T>> key,
                                                   final @Nullable Identifier               defaultId,
                                                   final           RegistryAttribute        ...attributes)
    {
        final FabricRegistryBuilder<T, ?> builder = (defaultId != null
            ? FabricRegistryBuilder.createDefaulted(key, defaultId)
            : FabricRegistryBuilder.createSimple(key));
        Arrays.stream(attributes).forEach(builder::attribute);
        
        final Registry<T> registry = builder.buildAndRegister();
        Constructeer.sendGlobalBootstrapReport("registered static registry '%s' with attributes %s",
                                               key.identifier(), Arrays.toString(attributes));
        
        return registry;
    }
    
    //==================================================================================================================
    public static <T> ResourceKey<Registry<T>> registerDynamicRegistry(final Identifier id, final Codec<T> codec)
    {
        return CteerRegistries.registerDynamicRegistry(CteerRegistries.createKey(id), codec);
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicRegistry(final ResourceKey<Registry<T>> key,
                                                                       final Codec<T>                 codec)
    {
        DynamicRegistries.register(key, codec);
        Constructeer.sendGlobalBootstrapReport("registered dynamic registry '%s'", key.identifier());
        return key;
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSyncedRegistry(
        final ResourceKey<Registry<T>>     key,
        final Codec<T>                     codec,
        final DynamicRegistries.SyncOption ...options
    )
    {
        return CteerRegistries.registerDynamicSyncedRegistry(key, codec, codec, options);
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSyncedRegistry(
        final Identifier                   id,
        final Codec<T>                     codec,
        final DynamicRegistries.SyncOption ...options
    )
    {
        return CteerRegistries.registerDynamicSyncedRegistry(CteerRegistries.createKey(id), codec, codec, options);
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSyncedRegistry(
        final Identifier                   id,
        final Codec<T>                     codec,
        final Codec<T>                     clientCodec,
        final DynamicRegistries.SyncOption ...options
    )
    {
        return CteerRegistries
            .registerDynamicSyncedRegistry(CteerRegistries.createKey(id), codec, clientCodec, options);
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSyncedRegistry(
        final ResourceKey<Registry<T>>     key,
        final Codec<T>                     codec,
        final Codec<T>                     clientCodec,
        final DynamicRegistries.SyncOption ...options
    )
    {
        DynamicRegistries.registerSynced(key, codec, clientCodec, options);
        Constructeer.sendGlobalBootstrapReport(
            "registered dynamic synced registry '%s' with options %s",
            key.identifier(), Arrays.toString(options));
        return key;
    }
    
    //==================================================================================================================
    public static <V, T extends V> T register(final Registry<V> registry, final Identifier name, final T value)
    {
        return CteerRegistries.register(registry, ResourceKey.create(registry.key(), name), value);
    }

    public static <V, T extends V> T register(final Registry<V> registry, final ResourceKey<V> key, final T value)
    {
        CteerRegistries.registerForHolder(registry, key, value);
        return value;
    }
    
    public static <R, T extends R> Holder.Reference<T> registerForHolder(final Registry<R> registry,
                                                                         final Identifier  name,
                                                                         final T           value)
    {
        return CteerRegistries.registerForHolder(registry, ResourceKey.create(registry.key(), name), value);
    }
    
    public static <R, T extends R> Holder.Reference<T> registerForHolder(final Registry<R>    registry,
                                                                         final ResourceKey<R> key,
                                                                         final T              value)
    {
        final Holder.Reference<T> holder = Registry.registerForHolder(registry, key, value);
        Constructeer.sendGlobalBootstrapReport(
            "registered '%s' for registry '%s'",
            key.identifier(), registry.key().identifier());
        return holder;
    }
    
    //==================================================================================================================
    public static <I, T> T registerMapped(final ExtraCodecs.LateBoundIdMapper<I, T> mapper, final I id, final T value)
    {
        mapper.put(id, value);
        Constructeer.sendGlobalBootstrapReport("registered '%s'", id);
        return value;
    }
}
