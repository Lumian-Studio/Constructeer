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
package xyz.lumian.constructeer.integration.trinkets;

import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;



//**********************************************************************************************************************
public interface ITrinkets
{
    //******************************************************************************************************************
    interface SlotReference
    {
        //**************************************************************************************************************
        static SlotReference parse(final String slot)
        {
            final String[] split = slot.split("/");
            
            if (split.length != 2)
            {
                throw new IllegalArgumentException("Invalid slot format '" + slot + "' (expected 'group/type')");
            }
            
            return SlotReference.of(split[0], split[1]);
        }
        
        static SlotReference of(final String group, final String type)
        {
            if (group.isBlank())
            {
                throw new IllegalArgumentException("Slot group may not be blank");
            }
            
            if (type.isBlank())
            {
                throw new IllegalArgumentException("Slot type may not be blank");
            }
            
            final String trimmed_group = group.trim();
            final String trimmed_type  = type.trim();
            
            return new SlotReference() {
                @Override public String group() { return trimmed_group; }
                @Override public String type()  { return trimmed_type;  }
            };
        }
        
        //**************************************************************************************************************
        String group();
        String type();
        
        //==============================================================================================================
        default String toPath() { return this.group() + "/" + this.type(); }
    }
    
    enum DefaultSlot
        implements SlotReference
    {
        HEAD_HAT      ("head",    "hat"),
        HEAD_FACE     ("head",    "face"),
        CHEST_NECKLACE("chest",   "necklace"),
        CHEST_CAPE    ("chest",   "cape"),
        CHEST_BACK    ("chest",   "back"),
        LEGS_BELT     ("legs",    "belt"),
        FEET_SHOES    ("feet",    "shoes"),
        FEET_AGLET    ("feet",    "aglet"),
        HAND_GLOVE    ("hand",    "glove"),
        HAND_RING     ("hand",    "ring"),
        OFFHAND_GLOVE ("offhand", "glove"),
        OFFHAND_RING  ("offhand", "ring"),
        ;
        
        //**************************************************************************************************************
        public final String group;
        public final String type;
        
        //**************************************************************************************************************
        DefaultSlot(final String group, final String type)
        {
            this.group  = group;
            this.type   = type;
        }
        
        //==============================================================================================================
        @Override public String group() { return this.group; }
        @Override public String type()  { return this.type;  }
        
        //==============================================================================================================
        @Override public String toString() { return ("Trinket@" + this.toPath()); }
    }
    
    //******************************************************************************************************************
    Optional<Container> getContainer(Player player, SlotReference slot);
    
    default Optional<ItemStack> getSlotItem(final Player player, final SlotReference slot, final int slotId)
    {
        return this.getContainer(player, slot).map(container -> container.getItem(slotId));
    }
    
    default Optional<Stream<ItemStackWithSlot>> getItems(final Player player, final SlotReference slot)
    {
        return this.getContainer(player, slot).map(container -> IntStream
            .range(0, container.getContainerSize())
            .mapToObj(i -> new ItemStackWithSlot(i, container.getItem(i))));
    }
    
    default Optional<ItemStack> getFirstMatchingItem(final Player player, final SlotReference slot, final Item item)
    {
        return this.getItems(player, slot).flatMap(stream -> stream
            .map(ItemStackWithSlot::stack)
            .filter(stack -> stack.is(item))
            .findFirst());
    }
    
    default Optional<ItemStack> getFirstMatchingPredicate(final Player player, final SlotReference slot,
                                                          final Predicate<ItemStack> predicate)
    {
        return this.getItems(player, slot).flatMap(stream -> stream
            .map(ItemStackWithSlot::stack)
            .filter(predicate)
            .findFirst());
    }
}
