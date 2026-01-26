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
/// SOFTWARE.package xyz.lumian.constructeer.client.data;
package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import xyz.lumian.constructeer.enchantment.ModEnchantments;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class ModEnchantmentProvider
    extends FabricDynamicRegistryProvider
{
    //******************************************************************************************************************
    public ModEnchantmentProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> future)
    {
        super(output, future);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Enchantment Provider"; }
    
    //==================================================================================================================
    @Override
    protected void configure(final HolderLookup.Provider lookup, final Entries entries)
    {
        entries.add(
            ModEnchantments.PENETRATION,
            Enchantment
                .enchantment(Enchantment.definition(
                    lookup.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.MULTI_MINING_TOOLS),
                    10, 2,
                    Enchantment.dynamicCost(20, 30),
                    Enchantment.dynamicCost(20, 50),
                    5,
                    EquipmentSlotGroup.MAINHAND))
                .build(ModEnchantments.PENETRATION.identifier()));
    }
}
