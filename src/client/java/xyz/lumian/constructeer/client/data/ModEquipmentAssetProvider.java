package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import xyz.lumian.constructeer.client.model.ModModelLayers;
import xyz.lumian.constructeer.item.ModEquipmentAssets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;



//**********************************************************************************************************************
public class ModEquipmentAssetProvider
    implements DataProvider
{
    //******************************************************************************************************************
    private final PackOutput.PathProvider pathProvider;
    
    //******************************************************************************************************************
    public ModEquipmentAssetProvider(final FabricDataOutput output)
    {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Equipment Asset Provider"; }
    
    //==================================================================================================================
    public CompletableFuture<?> run(final CachedOutput output)
    {
		final Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> map = new HashMap<>();
		this.generate((resourceKey, equipmentClientInfo) ->
        {
			if (map.putIfAbsent(resourceKey, equipmentClientInfo) != null)
            {
				throw new IllegalStateException("Tried to register equipment asset twice for id: " + resourceKey);
			}
		});
  
		return DataProvider.saveAll(output, EquipmentClientInfo.CODEC, this.pathProvider::json, map);
	}
    
    //==================================================================================================================
    private void generate(final BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output)
    {
        output.accept(
            ModEquipmentAssets.TOOLBELT,
            EquipmentClientInfo.builder()
                .addLayers(
                    EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                    new EquipmentClientInfo.Layer(ModEquipmentAssets.TOOLBELT.identifier(), Optional.empty(), false))
                .build()
        );
    }
}
