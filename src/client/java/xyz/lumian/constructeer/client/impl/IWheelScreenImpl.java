package xyz.lumian.constructeer.client.impl;


import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.client.gui.screen.ToolbeltWheelScreen;



//**********************************************************************************************************************
public interface IWheelScreenImpl
{
    //******************************************************************************************************************
    @Nullable ToolbeltWheelScreen constructeer$getWheelScreen();
    void constructeer$setWheelScreen(@Nullable ToolbeltWheelScreen screen);
}
