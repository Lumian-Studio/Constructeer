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
package xyz.lumian.constructeer.toolbelt.container;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.integration.accessory.AccessorySlot;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.registry.CteerToolbeltTags;
import xyz.lumian.constructeer.toolbelt.item.PouchItem;
import xyz.lumian.constructeer.toolbelt.item.ToolbeltItem;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.PouchContent;
import xyz.lumian.constructeer.toolbelt.item.component.ToolbeltStorage;

import java.util.List;
import java.util.function.UnaryOperator;



//**********************************************************************************************************************
public class ToolbeltMenu
    extends AbstractContainerMenu
{
    //******************************************************************************************************************
    public class PouchSlot
        extends Slot
    {
        //**************************************************************************************************************
        public PouchSlot(final Container container, final int id, final Vector2i pos)
        {
            super(container, id, pos.x(), pos.y());
        }
        
        //==============================================================================================================
        @Override public @Nullable Identifier getNoItemIcon()   { return ToolbeltMenu.POUCH_SLOT_PLACEHOLDER_ICON; }
        @Override public           int        getMaxStackSize() { return 1; }
        
        //==============================================================================================================
        @Override public boolean mayPlace(final ItemStack stack) { return stack.is(CteerToolbeltTags.POUCHES); }
        
        //==============================================================================================================
        @Override
        public void setChanged()
        {
            ToolbeltMenu.this.updateToolbelt(ToolbeltStorage.updateOp(this.getContainerSlot(), this.getItem()));
            super.setChanged();
        }
    }
    
    //******************************************************************************************************************
    /// The [Identifier] to the texture displayed if no pouch is set for a slot.
    public static final Identifier POUCH_SLOT_PLACEHOLDER_ICON = CteerDefine.id("container/slot/pouch");
    
    //==================================================================================================================
    /// The list of positions for each pouch slot.
    public static final Vector2i[] POUCH_SLOTS = {
        // MAIN
        new Vector2i(36, 36),
        
        // POUCHES
        new Vector2i( 8,  8),
        new Vector2i( 0, 36),
        new Vector2i( 8, 64),
        new Vector2i(36, 72),
        new Vector2i(64, 64),
        new Vector2i(72, 36),
        new Vector2i(64,  8),
        new Vector2i(36,  0),
    };
    
    //******************************************************************************************************************
    public static ToolbeltMenu client(final int id, final Inventory inventory)
    {
        return new ToolbeltMenu(id, inventory, AccessorySlot.of(IAccessory.SlotConstants.BELT),
                                new SimpleContainer(ToolbeltItem.COUNT_POUCHES));
    }
    
    public static ToolbeltMenu server(final AccessorySlot slot, final int id, final Inventory inventory,
                                      final SimpleContainer container)
    {
        return new ToolbeltMenu(id, inventory, slot, container);
    }
    
    //******************************************************************************************************************
    private final Player          player;
    private final SimpleContainer container;
    private final AccessorySlot   equipmentSlot;
    
    private boolean initialised;
    
    //******************************************************************************************************************
    private ToolbeltMenu(final int id, final Inventory inventory, final AccessorySlot slot,
                         final SimpleContainer container)
    {
        super(CteerToolbeltMenus.TOOLBELT, id);
        
        this.equipmentSlot = slot;
        this.addDataSlot(this.equipmentSlot);
        
        this.player      = inventory.player;
        this.initialised = !inventory.player.isLocalPlayer();
        this.container   = container;
        this.container.startOpen(inventory.player);
        
        // All other pouch slots
        for (int i = 0; i < ToolbeltItem.COUNT_POUCHES; ++i)
        {
            final Vector2i pos = new Vector2i(ToolbeltMenu.POUCH_SLOTS[i]).add(44, 23);
            this.addSlot(new PouchSlot(this.container, i, pos));
        }
        
        // The player inventory slots
        this.addStandardInventorySlots(inventory, 8, 132);
    }
    
    //==================================================================================================================
    @Override
    public boolean stillValid(final Player player)
    {
        return this.equipmentSlot.getEquipmentFromPlayer(player).is(CteerToolbeltItems.TOOLBELT);
    }
    
    @Override
    public void initializeContents(final int stateId, final List<ItemStack> list, final ItemStack stack)
    {
        super.initializeContents(stateId, list, stack);
        this.initialised = true;
    }
    
    //==================================================================================================================
    @Override
    public ItemStack quickMoveStack(final Player player, final int slotId)
    {
        final Slot slot = this.slots.get(slotId);
        
        if (!slot.hasItem())
        {
            return ItemStack.EMPTY;
        }
        
        final ItemStack slot_stack = slot.getItem();
        
        ItemStack result_stack = slot_stack.copy();
        
        if (slotId < ToolbeltItem.COUNT_POUCHES)
        {
            final ItemStack content = slot_stack
                .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY)
                .content();
            
            if (content.isEmpty())
            {
                if (!this.moveItemStackTo(slot_stack, ToolbeltItem.COUNT_POUCHES, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.moveItemStackTo(content, ToolbeltItem.COUNT_POUCHES, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
                
                slot_stack.remove(CteerToolbeltDataComponents.POUCH_CONTENT);
                result_stack = ItemStack.EMPTY;
            }
        }
        else if (PouchItem.isValidToolItem(slot_stack))
        {
            boolean has_changed = false;
            
            for (int i = 0; i < ToolbeltItem.COUNT_POUCHES; ++i)
            {
                final Slot      pouch_slot = this.getSlot(i);
                final ItemStack pouch      = pouch_slot.getItem();
                
                if (pouch.isEmpty())
                {
                    continue;
                }
                
                final PouchContent content = pouch
                    .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY);
                
                if (!content.isEmpty())
                {
                    continue;
                }
                
                pouch.set(CteerToolbeltDataComponents.POUCH_CONTENT, new PouchContent(slot_stack.split(1)));
                pouch_slot.setChanged();
                
                has_changed = true;
            }
            
            if (!has_changed)
            {
                return ItemStack.EMPTY;
            }
        }
        else if (!this.moveItemStackTo(slot_stack, 0, ToolbeltItem.COUNT_POUCHES, false))
        {
            return ItemStack.EMPTY;
        }
        
        if (slot_stack.isEmpty())
        {
            slot.setByPlayer(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }
        
        return result_stack;
    }
    
    private void updateToolbelt(final UnaryOperator<ToolbeltStorage> updater)
    {
        if (!this.initialised)
        {
            return;
        }
        
        final ItemStack toolbelt = this.equipmentSlot.getEquipmentFromPlayer(this.player);
        
        if (!toolbelt.is(CteerToolbeltItems.TOOLBELT))
        {
            CteerDefine.LOGGER.error("could not save toolbelt, no such item in slot {}", this.equipmentSlot);
            return;
        }
        
        toolbelt.update(CteerToolbeltDataComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY, updater);
    }
    
    //==================================================================================================================
    @Override
    public void removed(final Player player)
    {
        super.removed(player);
        this.container.stopOpen(player);
    }
    
    @Override
    public void clicked(final int slotId, final int button, final ClickType clickType, final Player player)
    {
        final int reverse_id = (this.slots.size() - slotId);
        
        if (reverse_id > 9 || this.equipmentSlot.isAccessorySlot())
        {
            super.clicked(slotId, button, clickType, player);
            return;
        }
        
        final ItemStack toolbelt = this.equipmentSlot.getEquipmentSlot().map(player::getItemBySlot).orElseThrow();
        
        // if our hotbar slot contains the toolbelt stack
        if (this.getSlot(slotId).getItem() != toolbelt)
        {
            super.clicked(slotId, button, clickType, player);
        }
    }
}
