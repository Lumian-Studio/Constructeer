package xyz.lumian.constructeer.util;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Function;



//**********************************************************************************************************************
public record RegistryId<T>(ResourceKey<? extends Registry<T>> registryKey, Either<Identifier, TagKey<T>> id)
{
    //******************************************************************************************************************
    public static <T> RegistryId<T> forRegistry(final ResourceKey<Registry<T>> registry, final Identifier id)
    {
        return new RegistryId<>(registry, Either.left(id));
    }
    
    public static <T> RegistryId<T> forRegistry(final ResourceKey<Registry<T>> registry, final TagKey<T> tag)
    {
        return new RegistryId<>(registry, Either.right(tag));
    }
    
    public static <T> RegistryId<T> parse(final ResourceKey<Registry<T>> registry, String value)
    {
        return new RegistryId<>(registry, (value.startsWith("#")
            ? Either.right(TagKey.create(registry, Identifier.parse(value.substring(1))))
            : Either.left(Identifier.parse(value))));
    }
    
    public static <T>  RegistryId<T> untrustedId(final ResourceKey<Registry<T>> registry,
                                                 final String                   namespace,
                                                 final String                   path)
    {
        return RegistryId.forRegistry(registry, Identifier.fromNamespaceAndPath(namespace, path));
    }
    
    public static <T> RegistryId<T> vanillaId(final ResourceKey<Registry<T>> registry, final String path)
    {
        return RegistryId.forRegistry(registry, Identifier.withDefaultNamespace(path));
    }
    
    public static <T>  RegistryId<T> untrustedTag(final ResourceKey<Registry<T>> registry,
                                                  final String                   namespace,
                                                  final String                   path)
    {
        return RegistryId.forRegistry(
            registry,
            TagKey.create(registry, Identifier.fromNamespaceAndPath(namespace, path)));
    }
    
    public static <T> RegistryId<T> vanillaTag(final ResourceKey<Registry<T>> registry, final String path)
    {
        return RegistryId.forRegistry(registry, TagKey.create(registry, Identifier.withDefaultNamespace(path)));
    }
    
    public static boolean isValidRegistryId(String id)
    {
        if (id.startsWith("#"))
        {
            id = id.substring(1);
        }
        
        final int    separator = id.indexOf(':');
        final String namespace;
        final String path;
        
        if (separator > -1)
        {
            namespace = id.substring(0, separator);
            path      = id.substring(separator + 1);
        }
        else
        {
            namespace = "minecraft";
            path      = id;
        }
        
        return (Identifier.isValidNamespace(namespace) && Identifier.isValidPath(path) && !path.isEmpty());
    }
    
    //******************************************************************************************************************
    public boolean isId()  { return this.id.left() .isPresent(); }
    public boolean isTag() { return this.id.right().isPresent(); }
    
    //==================================================================================================================
    public Optional<HolderSet<T>> resolveOptional(final Registry<T> registry)
    {
        return this.id.map(
            (id  -> registry.get(id) .map(HolderSet::direct)),
            (tag -> registry.get(tag).map(Function.identity())));
    }
    
    public HolderSet<T> resolve(final Registry<T> lookup) { return this.getOrThrow(this.resolveOptional(lookup)); }
    
    public Optional<HolderSet<T>> resolveOptional(final HolderLookup.Provider lookup)
    {
        return lookup.get(this.registryKey).flatMap(ref -> this.resolveOptional(ref.value()));
    }
    
    public HolderSet<T> resolve(final HolderLookup.Provider lookup)
    {
        return this.getOrThrow(this.resolveOptional(lookup));
    }
    
    @SuppressWarnings("unchecked")
    public Optional<HolderSet<T>> resolveOptional()
    {
        return BuiltInRegistries.REGISTRY
            .getOptional(this.registryKey.identifier())
            .flatMap(reg -> this.resolveOptional((Registry<T>) reg));
    }
    
    public HolderSet<T> resolve() { return this.getOrThrow(this.resolveOptional()); }
    
    //==================================================================================================================
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private HolderSet<T> getOrThrow(final Optional<HolderSet<T>> optional)
    {
        return optional.orElseThrow(() -> new IllegalStateException("registry id " + this + " could not be looked up"));
    }
    
    //==================================================================================================================
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper("RegistryId")
            .add("registry", this.registryKey.identifier())
            .add("id",       this.id.map(Identifier::toString, (key -> ("#" + key.location()))))
            .toString();
    }
}
