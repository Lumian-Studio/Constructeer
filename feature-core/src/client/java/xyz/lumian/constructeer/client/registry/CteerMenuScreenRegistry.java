package xyz.lumian.constructeer.client.registry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerMenuScreenRegistry
{
    //******************************************************************************************************************
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
        final MenuType<? extends M>               type,
        final MenuScreens.ScreenConstructor<M, U> factory
    )
    {
        MenuScreens.register(type, factory);
        Constructeer.sendGlobalBootstrapReport(() ->
            "registered menu screen for type '" + BuiltInRegistries.MENU.getKey(type) + '\'');
    }
}
