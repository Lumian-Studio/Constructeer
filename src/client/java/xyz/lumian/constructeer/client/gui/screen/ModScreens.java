package xyz.lumian.constructeer.client.gui.screen;


import net.minecraft.client.gui.screens.MenuScreens;
import xyz.lumian.constructeer.container.ModMenus;



//**********************************************************************************************************************
public final class ModScreens
{
    //******************************************************************************************************************
    static
    {
        MenuScreens.register(ModMenus.TOOLBELT, ToolbeltScreen::new);
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private ModScreens() {}
}
