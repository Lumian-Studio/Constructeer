package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
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
                .addTag(ModItemTags.PLOWS);
            
            this.valueLookupBuilder(ModItemTags.COMMON_TOOLS)
                .setReplace(false)
                .addTag(ModItemTags.HAMMERS)
                .addTag(ModItemTags.PLOWS)
                .addTag(ModItemTags.SAWS);
        }
    }
}
