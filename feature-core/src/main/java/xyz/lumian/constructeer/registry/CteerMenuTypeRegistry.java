package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;



//**********************************************************************************************************************
public class CteerMenuTypeRegistry
{
    //******************************************************************************************************************
    public static <T extends AbstractContainerMenu> MenuType<T> register(final Identifier               id,
                                                                         final MenuType.MenuSupplier<T> supplier)
    {
        return CteerMenuTypeRegistry.register(ResourceKey.create(Registries.MENU, id), supplier);
    }
    
    public static <T extends AbstractContainerMenu> MenuType<T> register(final ResourceKey<MenuType<?>> key,
                                                                         final MenuType.MenuSupplier<T> supplier)
    {
        return CteerRegistries.register(BuiltInRegistries.MENU, key, new MenuType<>(supplier, FeatureFlagSet.of()));
    }
}
