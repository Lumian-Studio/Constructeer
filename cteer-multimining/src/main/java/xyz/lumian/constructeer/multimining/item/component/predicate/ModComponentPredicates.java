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
package xyz.lumian.constructeer.multimining.item.component.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModComponentPredicates
{
    //******************************************************************************************************************
    public static final DataComponentPredicate.Type<PouchPredicate> POUCH_CONTENT = register(
        "pouch_content",
        PouchPredicate.CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static <T extends DataComponentPredicate> DataComponentPredicate.Type<T> register(final String   path,
                                                                                              final Codec<T> codec)
    {
		return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE,
            ResourceKey.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, ModDefine.id(path)),
            new DataComponentPredicate.ConcreteType<>(codec));
	}
    
    //******************************************************************************************************************
    private ModComponentPredicates() {}
}
