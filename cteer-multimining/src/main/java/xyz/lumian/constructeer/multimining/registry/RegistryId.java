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
package xyz.lumian.constructeer.multimining.registry;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;



//**********************************************************************************************************************
public record RegistryId<T>(ResourceKey<? extends Registry<T>> registryKey, Either<Identifier, TagKey<T>> id)
{
    //******************************************************************************************************************
    public static <T> RegistryId<T> forIdentifier(final ResourceKey<Registry<T>> registry, final Identifier id)
    {
        return new RegistryId<>(registry, Either.left(id));
    }
    
    public static <T> RegistryId<T> forTag(final TagKey<T> tag)
    {
        return new RegistryId<>(tag.registry(), Either.right(tag));
    }
    
    public static <T> RegistryId<T> parse(final ResourceKey<Registry<T>> registry, final String value)
    {
        return new RegistryId<>(registry, (value.startsWith("#")
            ? Either.right(TagKey.create(registry, Identifier.parse(value.substring(1))))
            : Either.left(Identifier.parse(value))));
    }
    
    public static <T> @Nullable RegistryId<T> tryParse(final ResourceKey<Registry<T>> registry, String value)
    {
        final boolean is_tag;
        
        if (value.startsWith("#"))
        {
            is_tag = true;
            value  = value.substring(1);
        }
        else is_tag = false;
        
        final Identifier id = Identifier.tryParse(value);
        
        if (id == null)
        {
            return null;
        }
        
        return new RegistryId<>(registry, (is_tag ? Either.right(TagKey.create(registry, id)) : Either.left(id)));
    }
    
    public static <T>  RegistryId<T> untrustedId(final ResourceKey<Registry<T>> registry,
                                                 final String                   namespace,
                                                 final String                   path)
    {
        return RegistryId.forIdentifier(registry, Identifier.fromNamespaceAndPath(namespace, path));
    }
    
    public static <T> RegistryId<T> vanillaId(final ResourceKey<Registry<T>> registry, final String path)
    {
        return RegistryId.forIdentifier(registry, Identifier.withDefaultNamespace(path));
    }
    
    public static <T>  RegistryId<T> untrustedTag(final ResourceKey<Registry<T>> registry,
                                                  final String                   namespace,
                                                  final String                   path)
    {
        return new RegistryId<>(
            registry,
            Either.right(TagKey.create(registry, Identifier.fromNamespaceAndPath(namespace, path))));
    }
    
    public static <T> RegistryId<T> vanillaTag(final ResourceKey<Registry<T>> registry, final String path)
    {
        return new RegistryId<>(
            registry,
            Either.right(TagKey.create(registry, Identifier.withDefaultNamespace(path))));
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
        return this.id
            .map((id -> registry.get(id).map(HolderSet::direct)), registry::get)
            .map(Function.identity());
    }
    
    public Optional<Holder<T>> resolveSingleOptional(final Registry<T> registry)
    {
        return this.id
            .<Optional<? extends Holder<T>>>map(registry::get, (tag -> Optional.empty()))
            .map(Function.identity());
    }
    
    public HolderSet<T> resolve(final Registry<T> lookup) { return this.getOrThrow(this.resolveOptional(lookup)); }
    
    public Holder<T> resolveSingle(final Registry<T> lookup)
    {
        return this.getOrThrowSingle(this.resolveSingleOptional(lookup));
    }
    
    public Optional<HolderSet<T>> resolveOptional(final HolderLookup.Provider lookup)
    {
        return lookup.get(this.registryKey).flatMap(ref -> this.resolveOptional(ref.value()));
    }
    
    public Optional<Holder<T>> resolveSingleOptional(final HolderLookup.Provider lookup)
    {
        return lookup.get(this.registryKey).flatMap(ref -> this.resolveSingleOptional(ref.value()));
    }
    
    public HolderSet<T> resolve(final HolderLookup.Provider lookup)
    {
        return this.getOrThrow(this.resolveOptional(lookup));
    }
    
    public Holder<T> resolveSingle(final HolderLookup.Provider lookup)
    {
        return this.getOrThrowSingle(this.resolveSingleOptional(lookup));
    }
    
    @SuppressWarnings("unchecked")
    public Optional<HolderSet<T>> resolveOptional()
    {
        return BuiltInRegistries.REGISTRY
            .get(this.registryKey.identifier())
            .flatMap(reg -> this.resolveOptional((Registry<T>) reg));
    }
    
    @SuppressWarnings("unchecked")
    public Optional<Holder<T>> resolveSingleOptional()
    {
        return BuiltInRegistries.REGISTRY
            .get(this.registryKey.identifier())
            .flatMap(reg -> this.resolveSingleOptional((Registry<T>) reg));
    }
    
    public HolderSet<T> resolve() { return this.getOrThrow(this.resolveOptional()); }
    public Holder<T> resolveSingle() { return this.getOrThrowSingle(this.resolveSingleOptional()); }
    
    //==================================================================================================================
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private HolderSet<T> getOrThrow(final Optional<? extends HolderSet<T>> optional)
    {
        return optional.orElseThrow(() -> new IllegalStateException("registry id " + this + " could not be looked up"));
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Holder<T> getOrThrowSingle(final Optional<? extends Holder<T>> optional)
    {
        return optional.orElseThrow(() -> new IllegalStateException("registry id " + this + " could not be looked up"));
    }
    
    public String asIdString() { return this.id.map(Identifier::toString, (tag -> ("#" + tag.location()))); }
    
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
