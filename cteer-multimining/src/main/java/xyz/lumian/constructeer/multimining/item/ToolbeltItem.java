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
package xyz.lumian.constructeer.multimining.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.container.ToolbeltMenu;
import xyz.lumian.constructeer.multimining.container.slot.ExtendedEquipmentSlot;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.ToolbeltStorage;



//**********************************************************************************************************************
public class ToolbeltItem
    extends Item
{
    //******************************************************************************************************************
    public record MenuProvider(ExtendedEquipmentSlot slot, ItemStack stack)
        implements net.minecraft.world.MenuProvider
    {
        //**************************************************************************************************************
        @Override public Component getDisplayName() { return this.stack.getHoverName(); }
        
        @Override
        public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inv,
                                                          final Player player)
        {
            if (!this.stack.is(ModItems.TOOLBELT))
            {
                ModDefine.LOGGER.error(
                    "could not open toolbelt configuration menu as the item in the given slot was not a toolbelt: {}",
                    this.stack);
                return null;
            }
            
            return ToolbeltMenu.server(this.slot, containerId, inv, new SimpleContainer(this.stack
                .getOrDefault(ModComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY)
                .asItems()
                .stream()
                .map(ItemStack::copy)
                .toArray(ItemStack[]::new)));
        }
    }
    
    //******************************************************************************************************************
    public static final int COUNT_POUCHES = 9;
    
    //******************************************************************************************************************
    public ToolbeltItem(final Properties properties) { super(properties); }
    
    //==================================================================================================================
    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand)
    {
        if (hand != InteractionHand.MAIN_HAND)
        {
            return InteractionResult.PASS;
        }
        
        if (player.isCrouching())
        {
            return super.use(level, player, hand);
        }
        
        if (!level.isClientSide())
        {
            player.openMenu(new MenuProvider(new ExtendedEquipmentSlot(hand), player.getItemInHand(hand)));
        }
        
        return InteractionResult.SUCCESS;
    }
}
