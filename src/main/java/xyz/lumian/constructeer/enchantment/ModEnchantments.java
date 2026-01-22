package xyz.lumian.constructeer.enchantment;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModEnchantments
{
    //******************************************************************************************************************
    public static final ResourceKey<Enchantment> PENETRATION = create("penetration");
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    public static ResourceKey<Enchantment> create(final String path)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, ModDefine.id(path));
    }
    
    //******************************************************************************************************************
    private ModEnchantments() {}
}
