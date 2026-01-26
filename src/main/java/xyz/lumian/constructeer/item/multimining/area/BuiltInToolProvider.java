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
package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.item.multimining.predicate.BuiltInToolPredicate;
import xyz.lumian.constructeer.item.multimining.predicate.IMultiMiningPredicate;

import java.util.function.Supplier;



//**********************************************************************************************************************
@ApiStatus.Internal
public record BuiltInToolProvider(
    Supplier<IMultiMiningPredicate>       predicateGetter,
    AreaProviderType<BuiltInToolProvider> type
) implements IToolProvider
{
    //******************************************************************************************************************
    public static final BuiltInToolProvider HAMMER
        = new BuiltInToolProvider(BuiltInToolPredicate.HammerType.INSTANCE::getPlain, AreaProviderType.HAMMER);
    public static final BuiltInToolProvider PLOW
        = new BuiltInToolProvider(BuiltInToolPredicate.PlowType  .INSTANCE::getPlain, AreaProviderType.PLOW);
    
    public static final MapCodec<BuiltInToolProvider> HAMMER_CODEC = MapCodec.unit(() -> HAMMER);
    public static final MapCodec<BuiltInToolProvider> PLOW_CODEC   = MapCodec.unit(() -> PLOW);
    
    //******************************************************************************************************************
    @Override public IMultiMiningPredicate predicate() { return this.predicateGetter.get(); }
}
