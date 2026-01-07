package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.lumian.constructeer.client.gui.screen.ToolbeltWheelScreen;
import xyz.lumian.constructeer.client.impl.IWheelScreenImpl;



//**********************************************************************************************************************
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
    implements IWheelScreenImpl
{
    //******************************************************************************************************************
    @Unique
    @Nullable
    private ToolbeltWheelScreen wheelScreen = null;
    
    //******************************************************************************************************************
    @Override public @Nullable ToolbeltWheelScreen constructeer$getWheelScreen() { return this.wheelScreen; }
    
    //==================================================================================================================
    @Override
    public void constructeer$setWheelScreen(final @Nullable ToolbeltWheelScreen screen)
    {
        this.wheelScreen = screen;
    }
}
