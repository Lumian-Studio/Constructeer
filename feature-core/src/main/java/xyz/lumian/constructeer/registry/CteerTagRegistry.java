package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.CteerDefine;



//**********************************************************************************************************************
public final class CteerTagRegistry
{
    //******************************************************************************************************************
    public static final TagKey<Item> COMMON_TOOLS = common("tools");
    
    //******************************************************************************************************************
    public static TagKey<Item> mod(final String path) { return TagKey.create(Registries.ITEM, CteerDefine.id(path)); }
    
    public static TagKey<Item> common(final String path)
    {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    
    //******************************************************************************************************************
    private CteerTagRegistry() {}
}
