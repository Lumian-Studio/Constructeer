/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.toolbelt.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.toolbelt.CteerToolbeltDictionary;
import xyz.lumian.constructeer.toolbelt.client.player.CteerClientPlayerAttachments;
import xyz.lumian.constructeer.toolbelt.client.gui.screen.ToolbeltWheelScreen;
import xyz.lumian.constructeer.toolbelt.container.ToolbeltEquipmentSlot;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.item.PouchItem;
import xyz.lumian.constructeer.toolbelt.network.serverbound.PlayC2SOpenToolbeltConfig;



//**********************************************************************************************************************
public final class CteerToolbeltKeybinds
{
    //******************************************************************************************************************
    public static final KeyMapping OPEN_TOOLBELT_CONFIG;
    public static final KeyMapping SHOW_TOOLBELT_WHEEL;
    
    public static final KeyMapping.Category CONSTRUCTEER_CATEGORY;
    
    //==================================================================================================================
    static
    {
        CONSTRUCTEER_CATEGORY = KeyMapping.Category.register(CteerDefine.id("keybinds"));
        
        OPEN_TOOLBELT_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            CteerToolbeltDictionary.KEYBIND_OPEN_TOOLBELT_CONFIG.getKey(),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            CteerToolbeltKeybinds.CONSTRUCTEER_CATEGORY));
        SHOW_TOOLBELT_WHEEL = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            CteerToolbeltDictionary.KEYBIND_SHOW_TOOLBELT_WHEEL.getKey(),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            CteerToolbeltKeybinds.CONSTRUCTEER_CATEGORY));
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            final LocalPlayer player = client.player;
            
            if (player == null)
            {
                return;
            }
            
            while (CteerToolbeltKeybinds.OPEN_TOOLBELT_CONFIG.consumeClick())
            {
                if (player.hasContainerOpen())
                {
                    return;
                }
                
                final ToolbeltEquipmentSlot slot = ToolbeltEquipmentSlot.findToolbelt(player);
                
                if (!slot.getEquipmentFromPlayer(client.player).is(CteerToolbeltItems.TOOLBELT))
                {
                    break;
                }
                
                ClientPlayNetworking.send(new PlayC2SOpenToolbeltConfig(slot));
            }
            
            //noinspection UnstableApiUsage
            if (
                CteerToolbeltKeybinds.SHOW_TOOLBELT_WHEEL.isDown()
                && !player.hasAttached(CteerClientPlayerAttachments.WHEEL_SCREEN)
            )
            {
                if (!player.hasContainerOpen())
                {
                    final ToolbeltEquipmentSlot slot     = ToolbeltEquipmentSlot.findToolbelt(player);
                    final ItemStack             toolbelt = slot.getEquipmentFromPlayer(client.player);
                    
                    if (toolbelt.is(CteerToolbeltItems.TOOLBELT))
                    {
                        final ItemStack in_hand = player.getItemInHand(InteractionHand.MAIN_HAND);
                    
                        if (!PouchItem.isValidToolItem(in_hand) && !in_hand.isEmpty())
                        {
                            player.displayClientMessage(
                                CteerToolbeltDictionary.TOOLBELT_WHEEL_SCREEN_NOT_A_VALID_TOOL
                                    .copy()
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                                true);
                        }
                        else
                        {
                            client.setScreen(new ToolbeltWheelScreen(slot, toolbelt));
                            CteerToolbeltKeybinds.SHOW_TOOLBELT_WHEEL.setDown(true);
                        }
                    }
                    else
                    {
                        player.displayClientMessage(
                            CteerToolbeltDictionary.TOOLBELT_WHEEL_SCREEN_NO_TOOLBELT_FOUND
                                .copy()
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                            true);
                    }
                }
            }
        });
    }
    
    //******************************************************************************************************************
    private CteerToolbeltKeybinds() {}
}
