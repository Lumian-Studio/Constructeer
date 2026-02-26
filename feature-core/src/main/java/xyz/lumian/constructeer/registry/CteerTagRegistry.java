package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.integration.accessory.IAccessory;



//**********************************************************************************************************************
public final class CteerTagRegistry
{
    //******************************************************************************************************************
    public static final TagKey<Item> COMMON_TOOLS = common("tools");
    
    //******************************************************************************************************************
    public static TagKey<Item> mod(final String path) { return TagKey.create(Registries.ITEM, CteerDefine.id(path)); }
    
    /// Creates a tag key for the convention namespace `c`.
    public static TagKey<Item> common(final String path)
    {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    
    /// Creates a tag used to denote an accessory slot
    public static TagKey<Item> accessory(final IAccessory.SlotReference slotReference)
    {
        return TagKey.create(Registries.ITEM, Identifier
            .fromNamespaceAndPath(CteerDefine.Integrations.ACCESSORY, slotReference.id()));
    }
}
