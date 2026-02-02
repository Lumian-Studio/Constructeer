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
package xyz.lumian.constructeer.multimining.tag;

import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.util.FreezableMap;

import java.util.Map;



//**********************************************************************************************************************
public final class ModItemTags
{
    //******************************************************************************************************************
    public static final Map<Identifier, TagKey<Item>> BY_ID = new FreezableMap<>(new Object2ReferenceArrayMap<>());
    
    //==================================================================================================================
    // MOD TAGS
    public static final TagKey<Item> MULTI_MINING_TOOLS = mod("multi_mining_tools");
    public static final TagKey<Item> POUCHES            = mod("pouches");
    public static final TagKey<Item> HAMMERS            = mod("hammers");
    public static final TagKey<Item> PLOWS              = mod("plows");
    public static final TagKey<Item> SAWS               = mod("saws");
    
    //==================================================================================================================
    // COMMON TAGS (for mod use only)
    public static final TagKey<Item> COMMON_TOOLS = common("tools");
    
    //******************************************************************************************************************
    public static TagKey<Item> mod(final String path)
    {
        final Identifier   id  = ModDefine.id(path);
        final TagKey<Item> key = TagKey.create(Registries.ITEM, id);
        ModItemTags.BY_ID.put(id, key);
        
        return key;
    }
    
    public static TagKey<Item> common(final String path)
    {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    
    //******************************************************************************************************************
    private ModItemTags() {}
}
