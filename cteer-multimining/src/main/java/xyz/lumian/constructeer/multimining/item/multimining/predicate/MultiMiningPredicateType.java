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
package xyz.lumian.constructeer.multimining.item.multimining.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;



//**********************************************************************************************************************
@FunctionalInterface
public interface MultiMiningPredicateType<T extends IMultiMiningPredicate>
{
    //******************************************************************************************************************
    MultiMiningPredicateType<ToolPredicate>        TOOL
        = register(ModDefine.id("tool"),           ToolPredicate.MAP_CODEC);
    MultiMiningPredicateType<DynamicToolPredicate> DATA_TOOL
        = register(ModDefine.id("data_tool"),      DynamicToolPredicate.MAP_CODEC);
    MultiMiningPredicateType<IToolPredicate>       HAMMER
        = register(ModDefine.id("builtin_hammer"), BuiltInToolPredicate.HammerType.MAP_CODEC);
    MultiMiningPredicateType<IToolPredicate>       PLOW
        = register(ModDefine.id("builtin_plow"),   BuiltInToolPredicate.PlowType.MAP_CODEC);
    
    //******************************************************************************************************************
    static void initialise() {}
    
    //******************************************************************************************************************
    static <T extends IMultiMiningPredicate> MultiMiningPredicateType<T> register(final Identifier  id,
                                                                                  final MapCodec<T> codec)
    {
        return Registry.register(ModRegistries.BuiltIn.MULTI_MINING_PREDICATE_TYPE, id, (() -> codec));
    }
    
    //******************************************************************************************************************
    MapCodec<T> codec();
}
