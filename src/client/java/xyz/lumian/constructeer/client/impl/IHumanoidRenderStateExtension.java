package xyz.lumian.constructeer.client.impl;

import net.minecraft.world.item.ItemStack;



//**********************************************************************************************************************
public interface IHumanoidRenderStateExtension
{
    //******************************************************************************************************************
    double constructeer$getFallDistance();
    void constructeer$setFallDistance(double value);
    
    ItemStack constructeer$getToolbeltEquipment();
    void constructeer$setToolbeltEquipment(ItemStack toolbelt);
}
