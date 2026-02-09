package xyz.lumian.constructeer.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;

import java.util.Arrays;



//**********************************************************************************************************************
public final class CteerRegistries
{
    //******************************************************************************************************************
    public static <T> ResourceKey<Registry<T>> createKey(final String name)
    {
        return ResourceKey.createRegistryKey(CteerDefine.id(name));
    }
    
    //==================================================================================================================
    public static <T> Registry<T> registerOptional(final String name, final @Nullable Identifier defaultId)
    {
        return CteerRegistries.register(name, defaultId, RegistryAttribute.OPTIONAL);
    }
    
    public static <T> Registry<T> registerSynced(final String name, final @Nullable Identifier defaultId)
    {
        return CteerRegistries.register(name, defaultId, RegistryAttribute.SYNCED);
    }
    
    public static <T> Registry<T> registerOptionalSynced(final String name, final @Nullable Identifier defaultId)
    {
        return CteerRegistries.register(name, defaultId, RegistryAttribute.OPTIONAL, RegistryAttribute.SYNCED);
    }
    
    public static <T> Registry<T> register(final String name, final @Nullable Identifier defaultId,
                                           final RegistryAttribute ...attributes)
    {
        final ResourceKey<Registry<T>> key = CteerRegistries.createKey(name);
        final FabricRegistryBuilder<T, ?> builder = (defaultId != null
            ? FabricRegistryBuilder.createDefaulted(key, defaultId)
            : FabricRegistryBuilder.createSimple(key));
        Arrays.stream(attributes).forEach(builder::attribute);
        return builder.buildAndRegister();
    }
    
    //==================================================================================================================
    public static <T> ResourceKey<Registry<T>> registerDynamicSynced(final String name, final Codec<T> codec)
    {
        final ResourceKey<Registry<T>> key = CteerRegistries.createKey(name);
        DynamicRegistries.register(key, codec);
        return key;
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSynced(final String name, final Codec<T> codec,
                                                                     final DynamicRegistries.SyncOption ...options)
    {
        final ResourceKey<Registry<T>> key = CteerRegistries.createKey(name);
        DynamicRegistries.registerSynced(key, codec, options);
        return key;
    }
    
    public static <T> ResourceKey<Registry<T>> registerDynamicSynced(final String name, final Codec<T> codec,
                                                                     final Codec<T> clientCodec,
                                                                     final DynamicRegistries.SyncOption ...options)
    {
        final ResourceKey<Registry<T>> key = CteerRegistries.createKey(name);
        DynamicRegistries.registerSynced(key, codec, clientCodec, options);
        return key;
    }
    
    //******************************************************************************************************************
    private CteerRegistries() {}
}
