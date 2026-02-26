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

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;



//**********************************************************************************************************************
public final class CteerDataComponentRegistry
{
    //******************************************************************************************************************
    public static <T> DataComponentType<T> register(
        final Identifier                                                           id,
        final Function<DataComponentType.Builder<T>, DataComponentType.Builder<T>> builder
    )
    {
        return CteerDataComponentRegistry.register(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, id), builder);
    }
    
    public static <T> DataComponentType<T> register(
        final ResourceKey<DataComponentType<?>>                                    key,
        final Function<DataComponentType.Builder<T>, DataComponentType.Builder<T>> builder
    )
    {
        return CteerRegistries.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            key,
            builder.apply(DataComponentType.builder()).build());
    }
    
    public static <T extends DataComponentPredicate> DataComponentPredicate.Type<T> registerPredicate(
        final Identifier id,
        final Codec<T>   codec
    )
    {
        return CteerDataComponentRegistry.registerPredicate(
            ResourceKey.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, id),
            codec);
    }
    
    public static <T extends DataComponentPredicate> DataComponentPredicate.Type<T> registerPredicate(
        final ResourceKey<DataComponentPredicate.Type<?>> key,
        final Codec<T>                                    codec
    )
    {
        return CteerRegistries.register(
            BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE,
            key,
            new DataComponentPredicate.ConcreteType<>(codec));
    }
}
