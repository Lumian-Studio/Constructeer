package xyz.lumian.constructeer.integration.impl;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
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
    enum DefaultSlot
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
        
        //--------------------------------------------------------------------------------------------------------------
        private final Supplier<Either<DefaultSlot, String>> either;
        
        //**************************************************************************************************************
        DefaultSlot(final String group, final String type)
        {
            this.group  = group;
            this.type   = type;
            this.either = Suppliers.memoize(() -> Either.left(this));
        }
        
        //==============================================================================================================
        public Either<DefaultSlot, String> asEither() { return this.either.get(); }
        
        //==============================================================================================================
        @Override public String toString() { return ("Trinket@" + this.group + '/' + this.type); }
    }
    
    //******************************************************************************************************************
    Optional<Container> getContainer(Player player, Either<DefaultSlot, String> slot);
    
    default Optional<ItemStack> getSlotItem(final Player player, final Either<DefaultSlot, String> slot,
                                            final int slotId)
    {
        return this.getContainer(player, slot).map(container -> container.getItem(slotId));
    }
    
    default Optional<Stream<ItemStackWithSlot>> getItems(final Player player, final Either<DefaultSlot, String> slot)
    {
        return this.getContainer(player, slot).map(container -> IntStream
            .range(0, container.getContainerSize())
            .mapToObj(i -> new ItemStackWithSlot(i, container.getItem(i))));
    }
    
    default Optional<ItemStack> getFirstMatchingItem(final Player player, final Either<DefaultSlot, String> slot,
                                                     final Item item)
    {
        return this.getItems(player, slot).flatMap(stream -> stream
            .map(ItemStackWithSlot::stack)
            .filter(stack -> stack.is(item))
            .findFirst());
    }
    
    default Optional<ItemStack> getFirstMatchingPredicate(final Player player, final Either<DefaultSlot, String> slot,
                                                          final Predicate<ItemStack> predicate)
    {
        return this.getItems(player, slot).flatMap(stream -> stream
            .map(ItemStackWithSlot::stack)
            .filter(predicate)
            .findFirst());
    }
}
