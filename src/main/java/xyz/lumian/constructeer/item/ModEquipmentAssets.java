package xyz.lumian.constructeer.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModEquipmentAssets
{
    //******************************************************************************************************************
    public static final ResourceKey<EquipmentAsset> TOOLBELT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        ModDefine.id("toolbelt"));
    
    //******************************************************************************************************************
    private ModEquipmentAssets() {}
}
