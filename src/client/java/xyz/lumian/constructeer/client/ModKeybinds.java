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
package xyz.lumian.constructeer.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.client.gui.screen.ToolbeltWheelScreen;
import xyz.lumian.constructeer.client.impl.IWheelScreenImpl;
import xyz.lumian.constructeer.container.slot.ExtendedEquipmentSlot;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.impl.ITrinkets;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.item.PouchItem;
import xyz.lumian.constructeer.network.client.PlayC2SOpenToolbeltConfig;

import java.util.stream.Stream;



//**********************************************************************************************************************
public final class ModKeybinds
{
    //******************************************************************************************************************
    public static final KeyMapping OPEN_TOOLBELT_CONFIG;
    public static final KeyMapping SHOW_TOOLBELT_WHEEL;
    
    public static final KeyMapping.Category CONSTRUCTEER_CATEGORY;
    
    //==================================================================================================================
    static
    {
        CONSTRUCTEER_CATEGORY = KeyMapping.Category.register(ModDefine.id("keybinds"));
        
        OPEN_TOOLBELT_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            ModLang.KEYBIND_OPEN_TOOLBELT_CONFIG.getKey(),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            ModKeybinds.CONSTRUCTEER_CATEGORY));
        SHOW_TOOLBELT_WHEEL = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            ModLang.KEYBIND_SHOW_TOOLBELT_WHEEL.getKey(),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            ModKeybinds.CONSTRUCTEER_CATEGORY));
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
            
            while (ModKeybinds.OPEN_TOOLBELT_CONFIG.consumeClick())
            {
                if (player.hasContainerOpen())
                {
                    return;
                }
                
                final ExtendedEquipmentSlot slot = Compat.getTrinkets()
                    .flatMap(compat -> compat
                        .getItems(client.player, ITrinkets.DefaultSlot.LEGS_BELT.asEither())
                        .orElse(Stream.empty())
                        .filter(stack -> stack.stack().is(ModItems.TOOLBELT))
                    .findFirst()
                    .map(stack -> new ExtendedEquipmentSlot(stack.slot())))
                    .orElseGet(() -> new ExtendedEquipmentSlot(EquipmentSlot.LEGS));
                
                if (!slot.getEquipmentFromPlayer(client.player).is(ModItems.TOOLBELT))
                {
                    break;
                }
                
                ClientPlayNetworking.send(new PlayC2SOpenToolbeltConfig(slot));
            }
            
            final ToolbeltWheelScreen current_screen = ((IWheelScreenImpl) player).constructeer$getWheelScreen();
            
            if (ModKeybinds.SHOW_TOOLBELT_WHEEL.isDown() && current_screen == null)
            {
                if (!player.hasContainerOpen())
                {
                    final ExtendedEquipmentSlot slot = Compat.getTrinkets()
                        .flatMap(compat -> compat
                            .getItems(client.player, ITrinkets.DefaultSlot.LEGS_BELT.asEither())
                            .orElse(Stream.empty())
                            .filter(stack -> stack.stack().is(ModItems.TOOLBELT))
                        .findFirst()
                        .map(stack -> new ExtendedEquipmentSlot(stack.slot())))
                        .orElseGet(() -> new ExtendedEquipmentSlot(EquipmentSlot.LEGS));
                    final ItemStack toolbelt = slot.getEquipmentFromPlayer(client.player);
                    
                    if (toolbelt.is(ModItems.TOOLBELT))
                    {
                        final ItemStack in_hand = player.getItemInHand(InteractionHand.MAIN_HAND);
                    
                        if (!PouchItem.isValidToolItem(in_hand) && !in_hand.isEmpty())
                        {
                            player.displayClientMessage(
                                ModLang.TOOLBELT_WHEEL_SCREEN_NOT_A_VALID_TOOL.copy()
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                                true);
                        }
                        else
                        {
                            client.setScreen(new ToolbeltWheelScreen(slot, toolbelt));
                            ModKeybinds.SHOW_TOOLBELT_WHEEL.setDown(true);
                        }
                    }
                    else
                    {
                        player.displayClientMessage(
                            ModLang.TOOLBELT_WHEEL_SCREEN_NO_TOOLBELT_FOUND.copy()
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                            true);
                    }
                }
            }
        });
    }
    
    //******************************************************************************************************************
    private ModKeybinds() {}
}
