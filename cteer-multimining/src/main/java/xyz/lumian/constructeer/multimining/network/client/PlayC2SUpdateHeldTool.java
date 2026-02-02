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
package xyz.lumian.constructeer.multimining.network.client;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.container.slot.ExtendedEquipmentSlot;
import xyz.lumian.constructeer.multimining.item.PouchItem;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.PouchContent;
import xyz.lumian.constructeer.multimining.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.multimining.network.IHandleablePayload;
import xyz.lumian.constructeer.multimining.network.ModPayloads;
import xyz.lumian.constructeer.multimining.tag.ModItemTags;

import java.util.Optional;



//**********************************************************************************************************************
public record PlayC2SUpdateHeldTool(
    ItemStack             toolStack,
    ExtendedEquipmentSlot toolbeltSlot,
    ItemStack             toolbeltStack,
    short                 swapSlotId
)
    implements IHandleablePayload<ServerPlayNetworking.Context>
{
    //******************************************************************************************************************
    public static final Identifier ID = ModDefine.id("toolbelt_update_held_tool");
    
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayC2SUpdateHeldTool> CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,    PlayC2SUpdateHeldTool::toolStack,
        ExtendedEquipmentSlot.STREAM_CODEC, PlayC2SUpdateHeldTool::toolbeltSlot,
        ItemStack.STREAM_CODEC,             PlayC2SUpdateHeldTool::toolbeltStack,
        ByteBufCodecs.SHORT,                PlayC2SUpdateHeldTool::swapSlotId,
        PlayC2SUpdateHeldTool::new);
    
    //******************************************************************************************************************
    @Override public Type<PlayC2SUpdateHeldTool> type() { return ModPayloads.UPDATE_HELD_TOOL; }
    
    //******************************************************************************************************************
    public void handle(final ServerPlayNetworking.Context ctx)
    {
        boolean dirty = true;
        
        final ItemStack in_hand = ctx.player().getMainHandItem();
        
        if (!ItemStack.isSameItemSameComponents(this.toolStack, in_hand))
        {
            ModDefine.LOGGER.error("problem swapping tools, the item in hand differs from the client");
            dirty = false;
        }
        
        final ItemStack in_slot = this.toolbeltSlot.getEquipmentFromPlayer(ctx.player());
        
        if (!ItemStack.isSameItem(this.toolbeltStack, in_slot))
        {
            ModDefine.LOGGER.error("problem swapping tools, the item in the toolbelt slot differs from the client");
            dirty = false;
        }
        
        if (dirty && this.handleSwap(ctx.player(), in_hand, in_slot))
        {
            ctx.player().inventoryMenu.broadcastChanges();
            return;
        }
        
        ctx.player().inventoryMenu.broadcastFullState();
    }
    
    public boolean handleSwap(final Player player, final ItemStack inHand, final ItemStack toolbelt)
    {
        if (!PouchItem.isValidToolItem(inHand) && !inHand.isEmpty())
        {
            ModDefine.LOGGER.error("tool in hand is not a valid pouch tool");
            return false;
        }
        
        final ToolbeltStorage storage = toolbelt.get(ModComponents.TOOLBELT_STORAGE);
        
        if (storage == null)
        {
            return false;
        }
        
        final Optional<ItemStack> pouch_optional = storage.getPouch(this.swapSlotId)
            .filter(stack -> stack.is(ModItemTags.POUCHES));
        
        if (pouch_optional.isEmpty())
        {
            ModDefine.LOGGER.error("selected pouch slot {} was empty", this.swapSlotId);
            return false;
        }
        
        final ItemStack pouch   = pouch_optional.orElseThrow().copy();
        final ItemStack content = pouch.getOrDefault(ModComponents.POUCH_CONTENT, PouchContent.EMPTY).content();
        
        if (!inHand.isEmpty())
        {
            final int count = inHand.getCount();
            
            // AHA would you look at that, the perfect opportunity to swap slot contents
            if (count == 1 || content.isEmpty())
            {
                pouch.set(ModComponents.POUCH_CONTENT, new PouchContent(inHand.split(1)));
                
                if (!content.isEmpty())
                {
                    player.setItemInHand(InteractionHand.MAIN_HAND, content);
                }
                
                PouchItem.playInsertSound(player);
            }
            
            // arg, we can't swap if there is more than one in hand and pouches can only take one, so instead, if the
            // hand stack allows it, we put the pouch content onto the hand instead
            else
            {
                if ((count + 1) > inHand.getMaxStackSize() || !ItemStack.isSameItemSameComponents(inHand, content))
                {
                    // oof we lost the bet, what a fail, quick everybody... the ark is coming
                    PouchItem.playInsertFailSound(player);
                    
                    if (player.isLocalPlayer())
                    {
                        player.displayClientMessage(
                            ModLang.TOOLBELT_WHEEL_SCREEN_ACTIONBAR_HAND_IS_FULL.copy()
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                            true);
                    }
                    
                    return false;
                }
                
                // HA WE DID IT what a day
                pouch.remove(ModComponents.POUCH_CONTENT);
                inHand.grow(1);
                
                PouchItem.playRemoveSound(player);
            }
        }
        else if (!content.isEmpty())
        {
            pouch.remove(ModComponents.POUCH_CONTENT);
            player.setItemInHand(InteractionHand.MAIN_HAND, content);
            
            PouchItem.playRemoveSound(player);
        }
        else
        {
            // arrrg, no way to swap unswappable swappables
            return false;
        }
        
        toolbelt.update(
            ModComponents.TOOLBELT_STORAGE,
            ToolbeltStorage.EMPTY,
            ToolbeltStorage.updateOp(this.swapSlotId, pouch));
        
        return true;
    }
}
