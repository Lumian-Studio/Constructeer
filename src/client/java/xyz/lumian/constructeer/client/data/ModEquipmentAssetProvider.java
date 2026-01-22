package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.item.ModEquipmentAssets;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;



//**********************************************************************************************************************
public class ModEquipmentAssetProvider
    extends FabricCodecDataProvider<EquipmentClientInfo>
{
    //******************************************************************************************************************
    public ModEquipmentAssetProvider(final FabricDataOutput output,
                                     final CompletableFuture<HolderLookup.Provider> future)
    {
        super(output, future, PackOutput.Target.RESOURCE_PACK, "equipment", EquipmentClientInfo.CODEC);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Equipment Asset Provider"; }
    
    //==================================================================================================================
    @Override
    protected void configure(final BiConsumer<Identifier, EquipmentClientInfo> output,
                             final HolderLookup.Provider                       lookup)
    {
        output.accept(ModEquipmentAssets.TOOLBELT.identifier(), EquipmentClientInfo.builder()
            .addLayers(
                EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                new EquipmentClientInfo.Layer(ModEquipmentAssets.TOOLBELT.identifier(), Optional.empty(), false))
            .build());
    }
}
