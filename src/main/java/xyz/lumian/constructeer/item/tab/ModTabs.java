package xyz.lumian.constructeer.item.tab;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.item.ModItems;



//**********************************************************************************************************************
public final class ModTabs
{
    //******************************************************************************************************************
    public static final ResourceKey<CreativeModeTab> TOOLS_KEY;
    
    //==================================================================================================================
    public static final CreativeModeTab TOOLS_TAB;
    
    //==================================================================================================================
    static
    {
        TOOLS_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ModDefine.id("tools"));
        TOOLS_TAB = registerTab(ModTabs.TOOLS_KEY, FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.DIAMOND_HAMMER))
            .title(ModLang.CREATIVE_TAB_TOOLS)
            .build());
    }
    
    //******************************************************************************************************************
    private static CreativeModeTab registerTab(final ResourceKey<CreativeModeTab> tabKey, final CreativeModeTab tab)
    {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);
    }
    
    //******************************************************************************************************************
    private ModTabs() {}
}
