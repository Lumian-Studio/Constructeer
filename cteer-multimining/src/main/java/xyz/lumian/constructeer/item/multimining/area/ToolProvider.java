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

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import xyz.lumian.constructeer.item.multimining.predicate.ToolPredicate;



//**********************************************************************************************************************
public record ToolProvider(ToolPredicate predicate)
    implements IToolProvider
{
    //******************************************************************************************************************
    public static final Codec<ToolProvider> CODEC = ToolPredicate.CODEC
        .xmap(ToolProvider::new, ToolProvider::predicate);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolProvider> STREAM_CODEC = ToolPredicate.STREAM_CODEC
        .map(ToolProvider::new, ToolProvider::predicate);
    
    //******************************************************************************************************************
    @Override public AreaProviderType<? extends IAreaProvider> type() { return AreaProviderType.TOOL; }
}
