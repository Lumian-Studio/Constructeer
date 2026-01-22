package xyz.lumian.constructeer.tag;

import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.util.FreezableMap;

import java.util.Map;



//**********************************************************************************************************************
public final class ModItemTags
{
    //******************************************************************************************************************
    public static final Map<Identifier, TagKey<Item>> BY_ID = new FreezableMap<>(new Object2ReferenceArrayMap<>());
    
    //==================================================================================================================
    // MOD TAGS
    public static final TagKey<Item> MULTI_MINING_TOOLS = mod("multi_mining_tools");
    public static final TagKey<Item> POUCHES            = mod("pouches");
    public static final TagKey<Item> HAMMERS            = mod("hammers");
    public static final TagKey<Item> PLOWS              = mod("plows");
    public static final TagKey<Item> SAWS               = mod("saws");
    
    //==================================================================================================================
    // COMMON TAGS (for mod use only)
    public static final TagKey<Item> COMMON_TOOLS = common("tools");
    
    //******************************************************************************************************************
    public static TagKey<Item> mod(final String path)
    {
        final Identifier   id  = ModDefine.id(path);
        final TagKey<Item> key = TagKey.create(Registries.ITEM, id);
        ModItemTags.BY_ID.put(id, key);
        
        return key;
    }
    
    public static TagKey<Item> common(final String path)
    {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    
    //******************************************************************************************************************
    private ModItemTags() {}
}
