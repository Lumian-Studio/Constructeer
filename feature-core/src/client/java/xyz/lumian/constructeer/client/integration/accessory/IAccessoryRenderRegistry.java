package xyz.lumian.constructeer.client.integration.accessory;

import net.minecraft.world.item.Item;



//**********************************************************************************************************************
public interface IAccessoryRenderRegistry
{
    //******************************************************************************************************************
    void register(final Item item, final IAccessoryRenderer renderer);
}
