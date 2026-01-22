package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.lumian.constructeer.client.data.lang.ModEnUsProvider;



//**********************************************************************************************************************
public class ConstructeerData
	implements DataGeneratorEntrypoint
{
    //******************************************************************************************************************
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator)
    {
        final FabricDataGenerator.Pack pack = generator.createPack();
        ModTagProvider.addToPack(pack);

        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModEquipmentAssetProvider::new);
        pack.addProvider(ModEnchantmentProvider::new);
        
        // Langs
        pack.addProvider(ModEnUsProvider::new);
        
	}
}
