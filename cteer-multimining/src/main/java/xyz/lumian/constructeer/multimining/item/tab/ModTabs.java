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
package xyz.lumian.constructeer.multimining.item.tab;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.item.ModItems;



//**********************************************************************************************************************
public final class ModTabs
{
    //******************************************************************************************************************
    public static final ResourceKey<CreativeModeTab> TOOLS_KEY;
    
    //==================================================================================================================
    public static final CreativeModeTab TOOLS_TAB;
    
    //==================================================================================================================
    static
    {
        TOOLS_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ModDefine.id("tools"));
        TOOLS_TAB = registerTab(ModTabs.TOOLS_KEY, FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.DIAMOND_HAMMER))
            .title(ModLang.CREATIVE_TAB_TOOLS)
            .build());
    }
    
    //******************************************************************************************************************
    private static CreativeModeTab registerTab(final ResourceKey<CreativeModeTab> tabKey, final CreativeModeTab tab)
    {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);
    }
    
    //******************************************************************************************************************
    private ModTabs() {}
}
