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
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.multimining.registry.CteerMultiMiningRegistries;



//**********************************************************************************************************************
public record AreaProviderType<T extends IAreaProvider>(
    MapCodec<T>                             codec,
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
)
{
    //******************************************************************************************************************
    // data driven
    public static final AreaProviderType<ToolProvider> TOOL
        = register("tool", ToolProvider.CODEC.fieldOf("predicate"), ToolProvider.STREAM_CODEC);
    public static final AreaProviderType<TreeDetectionProvider> TREE_DETECTION
        = register("tree_detection", TreeDetectionProvider.MAP_CODEC, TreeDetectionProvider.STREAM_CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private static <T extends IAreaProvider> AreaProviderType<T> register(
        final String                                  name,
        final MapCodec<T>                             codec,
        final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    )
    {
        final AreaProviderType<T> type = new AreaProviderType<>(codec, streamCodec);
        return Registry.register(CteerMultiMiningRegistries.AREA_PROVIDER_TYPE, CteerDefine.id(name), type);
    }
}
