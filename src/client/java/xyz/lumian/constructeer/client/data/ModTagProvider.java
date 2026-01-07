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
        }
    }
}
