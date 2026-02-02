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
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;



//**********************************************************************************************************************
public record AreaProviderType<T extends IAreaProvider>(MapCodec<T> codec, int apiVersion)
{
    //******************************************************************************************************************
    // hardcoded built-ins
    @ApiStatus.Internal public static final AreaProviderType<BuiltInToolProvider>          HAMMER;
    @ApiStatus.Internal public static final AreaProviderType<BuiltInToolProvider>          PLOW;
    @ApiStatus.Internal public static final AreaProviderType<BuiltInTreeDetectionProvider> SAW;
    
    // data driven
    public static final AreaProviderType<ToolProvider>          TOOL;
    public static final AreaProviderType<TreeDetectionProvider> TREE_DETECTION;
    
    //==================================================================================================================
    static
    {
        HAMMER         = register("builtin_hammer", BuiltInToolProvider.HAMMER_CODEC,       1);
        PLOW           = register("builtin_plow",   BuiltInToolProvider.PLOW_CODEC,         1);
        SAW            = register("builtin_saw",    BuiltInTreeDetectionProvider.MAP_CODEC, 1);
        TOOL           = register("tool",           ToolProvider.MAP_CODEC,                 1);
        TREE_DETECTION = register("tree_detection", TreeDetectionProvider.MAP_CODEC,        1);
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private static <T extends IAreaProvider> AreaProviderType<T> register(final String name, final MapCodec<T> codec,
                                                                          final int apiVersion)
    {
        return Registry.register(
            ModRegistries.BuiltIn.AREA_PROVIDER_TYPE,
            ModDefine.id(name),
            new AreaProviderType<>(codec, apiVersion));
    }
}
