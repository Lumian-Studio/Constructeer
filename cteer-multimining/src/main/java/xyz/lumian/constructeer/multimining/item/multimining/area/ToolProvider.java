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
package xyz.lumian.constructeer.multimining.item.multimining.area;

import com.mojang.serialization.MapCodec;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.IMultiMiningPredicate;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.MultiMiningPredicateType;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;



//**********************************************************************************************************************
public record ToolProvider(IMultiMiningPredicate predicate)
    implements IToolProvider
{
    //******************************************************************************************************************
    public static final MapCodec<ToolProvider> MAP_CODEC = ModRegistries.BuiltIn.MULTI_MINING_PREDICATE_TYPE
        .byNameCodec()
        .<IMultiMiningPredicate>dispatch(IMultiMiningPredicate::type, MultiMiningPredicateType::codec)
        .xmap(ToolProvider::new, ToolProvider::predicate)
        .fieldOf("predicate");
    
    //******************************************************************************************************************
    @Override public AreaProviderType<? extends IAreaProvider> type() { return AreaProviderType.TOOL; }
}
