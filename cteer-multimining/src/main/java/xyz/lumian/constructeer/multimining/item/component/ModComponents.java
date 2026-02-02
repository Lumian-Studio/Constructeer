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
package xyz.lumian.constructeer.multimining.item.component;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.ModDefine;

import java.util.Map;



//**********************************************************************************************************************
public final class ModComponents
{
    //******************************************************************************************************************
    public static final Map<Identifier, DataComponentType<?>> BY_ID = new Object2ObjectArrayMap<>();
    
    //==================================================================================================================
    public static final DataComponentType<ToolbeltStorage> TOOLBELT_STORAGE = register(
        "toolbelt_storage",
        DataComponentType
            .<ToolbeltStorage>builder()
            .persistent(ToolbeltStorage.CODEC)
            .networkSynchronized(ToolbeltStorage.STREAM_CODEC)
            .cacheEncoding()
            .build());
    
    public static final DataComponentType<PouchContent> POUCH_CONTENT = register(
        "content",
        DataComponentType
            .<PouchContent>builder()
            .persistent(PouchContent.CODEC)
            .networkSynchronized(PouchContent.STREAM_CODEC)
            .cacheEncoding()
            .build());
    
    public static final DataComponentType<MultiMining> MULTI_MINING = register(
        "multi_mining",
        DataComponentType
            .<MultiMining>builder()
            .persistent(MultiMining.CODEC)
            .networkSynchronized(MultiMining.STREAM_CODEC)
            .cacheEncoding()
            .build());
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ComponentTooltipAppenderRegistry.addFirst(ModComponents.TOOLBELT_STORAGE);
        ComponentTooltipAppenderRegistry.addFirst(ModComponents.POUCH_CONTENT);
    }
    
    //==================================================================================================================
    public static <T> DataComponentType<T> register(final String name, final DataComponentType<T> component)
    {
        final ResourceKey<DataComponentType<?>> key = ResourceKey.create(
            Registries.DATA_COMPONENT_TYPE,
            ModDefine.id(name));
        ModComponents.BY_ID.put(key.identifier(), component);
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, component);
    }
    
    //******************************************************************************************************************
    private ModComponents() {}
}
