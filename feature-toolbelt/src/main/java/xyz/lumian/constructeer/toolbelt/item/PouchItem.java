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
package xyz.lumian.constructeer.toolbelt.item;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.registry.RegistryId;
import xyz.lumian.constructeer.toolbelt.config.CteerToolbeltServerConfig;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.PouchContent;
import xyz.lumian.constructeer.toolbelt.registry.CteerToolbeltTags;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;



//**********************************************************************************************************************
public class PouchItem
    extends Item
{
    //******************************************************************************************************************
    private static final AtomicReference<HolderSet<Item>> VALID_TOOLS = new AtomicReference<>(HolderSet.empty());
    
    //==================================================================================================================
    static
    {
        CteerToolbeltServerConfig.INSTANCE.addListener(config -> PouchItem.VALID_TOOLS.set(HolderSet.direct(config
            .allowedPouchTools
            .get()
            .stream()
            .flatMap(str ->
            {
                final RegistryId<Item> id = RegistryId.parse(Registries.ITEM, str);
                final Stream<Holder<Item>> holders = id
                    .resolveOptional(BuiltInRegistries.ITEM)
                    .map(HolderSet::stream)
                    .orElse(null);
                
                if (holders == null)
                {
                    CteerDefine.LOGGER.warn("unknown registry holder {}", id);
                    return Stream.empty();
                }
                
                return holders;
            })
           .distinct()
           .toList())));
    }
    
    //******************************************************************************************************************
    public static boolean isValidToolItem(final ItemStack stack) { return stack.is(PouchItem.VALID_TOOLS.get()); }
    
    //==================================================================================================================
    @SuppressWarnings("resource")
    public static void playInsertSound(final Player player)
    {
		player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, (0.8F + player.level().getRandom().nextFloat() * 0.4F));
	}

	public static void playInsertFailSound(final Player player)
    {
		player.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}
    
    @SuppressWarnings("resource")
    public static void playRemoveSound(final Player player)
    {
		player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, (0.8F + player.level().getRandom().nextFloat() * 0.4F));
	}
    
    //==================================================================================================================
    private static void playerInventoryChanged(final Player player)
    {
        player.containerMenu.slotsChanged(player.getInventory());
	}
    
    //******************************************************************************************************************
    public PouchItem(final Properties properties) { super(properties); }
    
    //==================================================================================================================
    @Override
	public boolean overrideStackedOnOther(final ItemStack me, final Slot slot, final ClickAction action,
                                          final Player player)
    {
        final ItemStack current = me
            .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY)
            .content();
        final ItemStack other   = slot.getItem();
     
        if (other.is(CteerToolbeltTags.POUCHES))
        {
            return false;
        }
        
        if (action == ClickAction.PRIMARY && !other.isEmpty())
        {
            if (
                PouchItem.isValidToolItem(other)
                && slot.allowModification(player)
                && (current.isEmpty() || other.getCount() < 2)
            )
            {
                final ItemStack next = slot.safeTake(1, 1, player);
                me.set(CteerToolbeltDataComponents.POUCH_CONTENT, new PouchContent(next));
                
                if (!current.isEmpty())
                {
                    slot.safeInsert(current);
                }
                
                PouchItem.playerInventoryChanged(player);
                PouchItem.playInsertSound(player);
            }
            else
            {
                PouchItem.playInsertFailSound(player);
            }
            
            return true;
        }
        else if (action == ClickAction.SECONDARY && other.isEmpty())
        {
            if (!current.isEmpty() && slot.allowModification(player))
            {
                slot.safeInsert(current);
                
                me.remove(CteerToolbeltDataComponents.POUCH_CONTENT);
                PouchItem.playerInventoryChanged(player);
                
                PouchItem.playRemoveSound(player);
            }
            
            return true;
        }
        
        return false;
	}
    
    @Override
	public boolean overrideOtherStackedOnMe(final ItemStack me, final ItemStack other, final Slot slot,
                                            final ClickAction action, final Player player, final SlotAccess slotAccess)
    {
        if (other.is(CteerToolbeltTags.POUCHES))
        {
            return false;
        }
        
        final ItemStack current = me
            .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY)
            .content();
        
        if (action == ClickAction.PRIMARY && !other.isEmpty())
        {
            if (
                PouchItem.isValidToolItem(other)
                && slot.allowModification(player)
                && (current.isEmpty() || other.getCount() < 2)
            )
            {
                final ItemStack next = other.split(1);
                me.set(CteerToolbeltDataComponents.POUCH_CONTENT, new PouchContent(next));
                
                if (!current.isEmpty())
                {
                    slotAccess.set(current);
                }
                
                PouchItem.playerInventoryChanged(player);
                PouchItem.playInsertSound(player);
            }
            else
            {
                PouchItem.playInsertFailSound(player);
            }
            
            return true;
        }
        else if (action == ClickAction.SECONDARY && other.isEmpty())
        {
            if (slot.allowModification(player) && !current.isEmpty())
            {
                slotAccess.set(current);
                
                me.remove(CteerToolbeltDataComponents.POUCH_CONTENT);
                PouchItem.playerInventoryChanged(player);
                
                PouchItem.playRemoveSound(player);
            }
            
            return true;
        }
        
        return false;
	}
}
