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
package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public abstract class ModTagProvider
{
    //******************************************************************************************************************
    public static void addToPack(final FabricDataGenerator.Pack pack) { pack.addProvider(Items::new); }
    
    //******************************************************************************************************************
    public static final class Items
        extends FabricTagProvider.ItemTagProvider
    {
        //**************************************************************************************************************
        public Items(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> future)
        {
            super(output, future);
        }
        
        //==============================================================================================================
        @Override
        protected void addTags(final HolderLookup.Provider lookup)
        {
            this.valueLookupBuilder(ModItemTags.POUCHES)
                .add(ModItems.POUCH)
                .addAll(ModItems.POUCH_BY_DYE.values());
            
            this.valueLookupBuilder(ModItemTags.HAMMERS).addAll(ModItems.HAMMER_BY_MATERIAL.values());
            this.valueLookupBuilder(ModItemTags.PLOWS)  .addAll(ModItems.PLOW_BY_MATERIAL  .values());
            this.valueLookupBuilder(ModItemTags.SAWS)   .addAll(ModItems.SAW_BY_MATERIAL   .values());
            
            this.valueLookupBuilder(ModItemTags.MULTI_MINING_TOOLS)
                .addTag(ModItemTags.HAMMERS)
                .addTag(ModItemTags.PLOWS)
                .addTag(ModItemTags.SAWS);
            
            this.valueLookupBuilder(ModItemTags.COMMON_TOOLS)
                .setReplace(false)
                .addTag(ModItemTags.MULTI_MINING_TOOLS);
            
            this.valueLookupBuilder(ItemTags.MINING_ENCHANTABLE)
                .setReplace(false)
                .addTag(ModItemTags.MULTI_MINING_TOOLS);
            
            this.valueLookupBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .setReplace(false)
                .addTag(ModItemTags.MULTI_MINING_TOOLS);
            
            this.valueLookupBuilder(ItemTags.MINING_LOOT_ENCHANTABLE)
                .setReplace(false)
                .addTag(ModItemTags.MULTI_MINING_TOOLS);
        }
    }
}
