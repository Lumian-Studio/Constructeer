package xyz.lumian.constructeer.container;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModMenus
{
    //******************************************************************************************************************
    public static final MenuType<ToolbeltMenu> TOOLBELT = ModMenus.register("toolbelt", ToolbeltMenu::client);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    public static <T extends AbstractContainerMenu> MenuType<T> register(final String                   name,
                                                                         final MenuType.MenuSupplier<T> supplier)
    {
        final ResourceKey<MenuType<?>> key = ResourceKey.create(Registries.MENU, ModDefine.id(name));
        return Registry.register(BuiltInRegistries.MENU, key, new MenuType<>(supplier, FeatureFlagSet.of()));
    }
    
    //******************************************************************************************************************
    private ModMenus() {}
}
