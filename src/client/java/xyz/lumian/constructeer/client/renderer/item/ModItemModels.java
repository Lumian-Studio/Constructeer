package xyz.lumian.constructeer.client.renderer.item;

import net.minecraft.client.renderer.item.ItemModels;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModItemModels
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ItemModels.ID_MAPPER.put(
            ModDefine.id("pouch/contained_item"),
            PouchContainedItemSpecialRenderer.Unbaked.MAP_CODEC);
    }
    
    //******************************************************************************************************************
    private ModItemModels() {}
}
