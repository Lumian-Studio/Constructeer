package xyz.lumian.constructeer.client.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.registry.CteerRegistries;



//**********************************************************************************************************************
public class CteerItemModelRegistry
{
    //******************************************************************************************************************
    public static void register(final Identifier id, final MapCodec<? extends ItemModel.Unbaked> model)
    {
        CteerRegistries.registerMapped(ItemModels.ID_MAPPER, id, model);
    }
}
