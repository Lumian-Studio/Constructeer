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
package xyz.lumian.constructeer.integration.accessory;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.util.CodecUtil;
import xyz.lumian.constructeer.integration.accessory.IAccessory.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;



//**********************************************************************************************************************
/// Represents a slot that can optionally work with accessory mods to represent either an accessory slot or a Minecraft
/// equipment slot. Furthermore, this class can (but doesn't have to) be used as a [DataSlot] to function in conjunction
/// with Minecraft's [Container] system.
///
/// Internally, this class holds a [Short] that represents either the slot index of a specific accessory slot container,
/// or the ordinal number of the given [EquipmentSlot] constant, which is determined by the sign bit. Since Minecraft's
/// data slots can only send data in a 16-bit width integer over the network, accessory slot indices are limited
/// to [Short#MAX_VALUE]. For this reason, using [#set(int)] or [#get()] with anything bigger than the
/// aforementioned limits will result in an integer overflow, thus it is best to use [#getSlotId()] or
/// [#setAccessorySlot(short)] and [#setEquipmentSlot(EquipmentSlot)] respectively.
///
/// The third state a slot can be in is if it is invalid, this can only happen if the slot is default constructed, or
/// if [#set(int)] is used, which is when the stored integer is bigger than the last ordinal of the [EquipmentSlot] enum
/// but the sign bit is not set. With [#isValidSlot()], this can be useful to represent a slot that has not yet been
/// initialised, or that a slot is not needed.
public abstract class AccessorySlot
    extends DataSlot
{
    //******************************************************************************************************************
    /// @param stack The [ItemStack] of the found slot
    /// @param slot  The [AccessorySlot] pointing to the slot the stack was found it
    public record SlotWithItemStack(ItemStack stack, AccessorySlot slot) {}
    
    //------------------------------------------------------------------------------------------------------------------
    private enum Type
    {
        ACCESSORY,
        VANILLA,
        INVALID;
        
        //**************************************************************************************************************
        public static Type of(final AccessorySlot slot)
        {
            if (slot.isAccessorySlot()) return ACCESSORY;
            if (slot.isEquipmentSlot()) return VANILLA;
            return                             INVALID;
        }
    }
    
    //******************************************************************************************************************
    /// The flag bit used to determine whether it is an accessory slot.
    public static final short ACCESSORY_FLAG = (short) (1 << 15);
    
    //******************************************************************************************************************
    /// Tries to find the given `item` in either the given accessory slot (as given by `ref`), or if no accessory slots
    /// are used OR it could not be found in any accessory slot, will search the vanilla equipment slot as given by
    /// [SlotReference#vanillaPendant()].
    /// @param ref    The [SlotReference] defining the slots to search
    /// @param player The [Player] to search the inventory from
    /// @param item   The [Item] to search for
    /// @return If the item was found in either the accessory or equipment slot, an optional containing the slot and
    ///         [ItemStack], otherwise [Optional#empty()]
    public static Optional<SlotWithItemStack> findEquipment(final SlotReference ref,
                                                            final Player        player,
                                                            final Item          item)
    {
        return AccessorySlot.findEquipment(ref, player, (stack -> stack.is(item)));
    }
    
    /// Tries to find an item according to the given `predicate` in either the given accessory slot (as given by `ref`),
    /// or if no accessory slots are used OR it could not be found in any accessory slot, will search the vanilla
    /// equipment slot as given by [SlotReference#vanillaPendant()].
    /// @param ref       The [SlotReference] defining the slots to search
    /// @param player    The [Player] to search the inventory from
    /// @param predicate The [ItemStack] predicate to test the slots against
    /// @return If the item was found in either the accessory or equipment slot, an optional containing the slot and
    ///         [ItemStack], otherwise [Optional#empty()]
    public static Optional<SlotWithItemStack> findEquipment(final SlotReference        ref,
                                                            final Player               player,
                                                            final Predicate<ItemStack> predicate)
    {
        final IAccessory compat = Compat.getAccessory().orElse(null);
        
        if (compat != null)
        {
            final ItemStackWithSlot stack = compat
                .getItems(player, ref)
                .filter(slot_stack -> predicate.test(slot_stack.stack()))
                .findFirst()
                .orElse(null);
            
            if (stack != null)
            {
                return Optional.of(new SlotWithItemStack(
                    stack.stack(),
                    AccessorySlot.ofAccessory(ref, (short) stack.slot())));
            }
        }
        
        final EquipmentSlot equipment_slot = ref.vanillaPendant();
        final ItemStack     stack          = player.getItemBySlot(equipment_slot);
        
        if (predicate.test(stack))
        {
            return Optional.of(new SlotWithItemStack(stack, AccessorySlot.ofVanilla(ref, equipment_slot)));
        }
        
        return Optional.empty();
    }
    
    /// Tries to find the given `item` in either the given accessory slot (as given by `ref`), or if no accessory slots
    /// are used OR it could not be found in any accessory slot, will search the vanilla equipment slot as given by
    /// [SlotReference#vanillaPendant()].
    /// @param ref    The [SlotReference] defining the slots to search
    /// @param player The [Player] to search the inventory from
    /// @param item   The [Item] to search for
    /// @return If the item was found in either the accessory or equipment slot, an optional containing the [ItemStack],
    ///         otherwise [Optional#empty()]
    public static Optional<ItemStack> findEquipmentStack(final SlotReference ref, final Player player, final Item item)
    {
        return AccessorySlot.findEquipmentStack(ref, player, (stack -> stack.is(item)));
    }
    
    /// Tries to find an item according to the given `predicate` in either the given accessory slot (as given by `ref`),
    /// or if no accessory slots are used OR it could not be found in any accessory slot, will search the vanilla
    /// equipment slot as given by [SlotReference#vanillaPendant()].
    /// @param ref       The [SlotReference] defining the slots to search
    /// @param player    The [Player] to search the inventory from
    /// @param predicate The [ItemStack] predicate to test the slots against
    /// @return If the item was found in either the accessory or equipment slot, an optional containing the [ItemStack],
    ///         otherwise [Optional#empty()]
    public static Optional<ItemStack> findEquipmentStack(final SlotReference        ref,
                                                         final Player               player,
                                                         final Predicate<ItemStack> predicate)
    {
        final IAccessory compat = Compat.getAccessory().orElse(null);
        
        if (compat != null)
        {
            return compat.getFirstMatchingItem(player, ref, predicate, true);
        }
        
        final ItemStack stack = player.getItemBySlot(ref.vanillaPendant());
        return (predicate.test(stack) ? Optional.of(stack) : Optional.empty());
    }
    
    /// Similar to [#findEquipmentStack(SlotReference, Player, Item)], but searches the vanilla equipment slots first.
    public static Optional<ItemStack> findVanillaEquipmentStack(final SlotReference ref,
                                                                final Player        player,
                                                                final Item          item)
    {
        return AccessorySlot.findVanillaEquipmentStack(ref, player, (stack -> stack.is(item)));
    }
    
    /// Similar to [#findEquipmentStack(SlotReference, Player, Predicate)], but searches the vanilla equipment slots
    /// first.
    public static Optional<ItemStack> findVanillaEquipmentStack(final SlotReference        ref,
                                                                final Player               player,
                                                                final Predicate<ItemStack> predicate)
    {
        final ItemStack stack = player.getItemBySlot(ref.vanillaPendant());
        
        if (predicate.test(stack))
        {
            return Optional.of(stack);
        }
        
        final IAccessory compat = Compat.getAccessory().orElse(null);
        
        if (compat != null)
        {
            return compat.getFirstMatchingItem(player, ref, predicate, false);
        }
        
        return Optional.empty();
    }
    
    //==================================================================================================================
    /// Creates a new invalid [AccessorySlot] with the given slot definition.
    /// @param ref The [SlotReference] pointing at the target slot
    /// @return The new invalid [AccessorySlot]
    public static AccessorySlot of(final SlotReference ref)
    {
        return new AccessorySlot() { @Override public SlotReference getSlotReference() { return ref; } };
    }
    
    /// Creates a new accessory targeted [AccessorySlot] with the given slot definition.
    /// @param ref                The [SlotReference] pointing at the target slot
    /// @param accessorySlotIndex The slot index inside the accessory slot's container
    /// @return The new accessory [AccessorySlot]
    public static AccessorySlot ofAccessory(final SlotReference ref, final short accessorySlotIndex)
    {
        return Util.make(AccessorySlot.of(ref), (slot -> slot.setAccessorySlot(accessorySlotIndex)));
    }
    
    /// Creates a new equipment slot targeted [AccessorySlot] with the given slot definition's
    /// [SlotReference#vanillaPendant()] slot.
    /// @param ref The [SlotReference] pointing at the target slot
    /// @return The new equipment slot [AccessorySlot]
    public static AccessorySlot ofVanillaPendant(final SlotReference ref)
    {
        return AccessorySlot.ofVanilla(ref, ref.vanillaPendant());
    }
    
    /// Creates a new equipment slot targeted [AccessorySlot] with the given slot definition and equipment slot.
    /// @param ref           The [SlotReference] pointing at the target slot
    /// @param equipmentSlot The initially targeted equipment slot
    /// @return The new equipment slot [AccessorySlot]
    public static AccessorySlot ofVanilla(final SlotReference ref, final EquipmentSlot equipmentSlot)
    {
        return Util.make(AccessorySlot.of(ref), (slot -> slot.setEquipmentSlot(equipmentSlot)));
    }
    
    //==================================================================================================================
    /// Creates a new strict [Codec] used to encode/decode [AccessorySlot] objects,
    /// other than [#optionalCodec(SlotReference)] this codec will complain if a slot is in an invalid state.
    /// @param ref The [SlotReference] pointing at the target slot
    public static Codec<AccessorySlot> codec(final SlotReference ref)
    {
        return AccessorySlot.optionalCodec(ref).validate(slot -> slot.isValidSlot()
            ? DataResult.success(slot)
            : DataResult.error(() -> "slot was not valid"));
    }
    
    /// Creates a new optional [Codec] used to encode/decode [AccessorySlot] objects,
    /// other than [#codec(SlotReference)] this codec will not complain if a slot is in an invalid state.
    /// @param ref The [SlotReference] pointing at the target slot
    public static Codec<AccessorySlot> optionalCodec(final SlotReference ref)
    {
        return ExtraCodecs.optionalEmptyMap(Codec.either(EquipmentSlot.CODEC, Codec.intRange(0, Short.MAX_VALUE)))
            .flatXmap(
                (opt -> DataResult.success(opt
                    .map(either -> either.map(
                        (slot  -> AccessorySlot.ofVanilla(ref, slot)),
                        (index -> AccessorySlot.ofAccessory(ref, index.shortValue()))))
                    .orElseGet(() -> AccessorySlot.of(ref)))),
                (slot ->
                {
                    if (!slot.isValidSlot())
                    {
                        return DataResult.success(Optional.empty());
                    }
                    
                    if (!slot.getSlotReference().equals(ref))
                    {
                        return DataResult.error(() -> "slot type '%s' does not match codec's slot type '%s'"
                            .formatted(slot.getSlotReference().toDebugString(), ref.toDebugString()));
                    }
                    
                    return DataResult.success(Optional.of(slot.getEquipmentSlot()
                        .<Either<EquipmentSlot, Integer>>map(Either::left)
                        .orElseGet(() -> Either.right((int) slot.getSlotId()))));
                }));
    }
    
    /// Creates a new optional [StreamCodec] used to encode/decode [AccessorySlot] objects for network transmission.
    ///
    /// This codec might throw an [IllegalStateException] if, upon encoding the slot to bytes, the slot's
    /// [SlotReference] does not satisfy the codec's given [SlotReference].
    /// @param ref The [SlotReference] pointing at the target slot
    public static StreamCodec<ByteBuf, AccessorySlot> streamCodec(final SlotReference ref)
    {
        return CodecUtil.smallEnum(Type.values()).dispatch(Type::of, (type -> switch (type)
        {
            case ACCESSORY -> ByteBufCodecs.SHORT.map((val -> AccessorySlot.ofAccessory(ref, val)), (slot ->
            {
                if (!slot.getSlotReference().equals(ref))
                {
                    throw new IllegalStateException(
                        "slot type '%s' does not match stream codec's slot type '%s'"
                            .formatted(slot.getSlotReference().toDebugString(), ref.toDebugString()));
                }
                
                return slot.getSlotId();
            }));
            
            case VANILLA -> CodecUtil.smallEnum(EquipmentSlot.values())
                .map((val -> AccessorySlot.ofVanilla(ref, val)), (slot ->
                {
                    if (!slot.getSlotReference().equals(ref))
                    {
                        throw new IllegalStateException(
                            "slot type '%s' does not match stream codec's slot type '%s'"
                                .formatted(slot.getSlotReference().toDebugString(), ref.toDebugString()));
                    }
                    
                    return EquipmentSlot.values()[slot.getSlotId()];
                }));
            
            case INVALID -> new StreamCodec<>()
            {
                //******************************************************************************************************
                @Override
                public void encode(final ByteBuf buf, final AccessorySlot slot)
                {
                    if (!slot.getSlotReference().equals(ref))
                    {
                        throw new IllegalStateException(
                            "slot type '%s' does not match stream codec's slot type '%s'"
                                .formatted(slot.getSlotReference().toDebugString(), ref.toDebugString()));
                    }
                }
                
                @Override public AccessorySlot decode(final ByteBuf buf) { return AccessorySlot.of(ref); }
            };
        }));
    }
    
    //******************************************************************************************************************
    private short value;
    
    //******************************************************************************************************************
    /// Constructs a new [AccessorySlot] with the given accessory slot index.
    /// @param accessorySlotId The index of the accessory slot
    public AccessorySlot(final short accessorySlotId) { this.setAccessorySlot(accessorySlotId); }
    
    /// Constructs a new [AccessorySlot] with the given equipment slot.
    /// @param equipmentSlot The [EquipmentSlot]
    public AccessorySlot(final EquipmentSlot equipmentSlot) { this.setEquipmentSlot(equipmentSlot);  }
    
    /// Constructs a new invalid [AccessorySlot].
    public AccessorySlot() { this.value = (short) EquipmentSlot.values().length; }
    
    //==================================================================================================================
    @ApiStatus.Internal
    @Override public int get() { return this.value; }
    
    /// {@return the [SlotReference] that this slot object represents}
    public abstract SlotReference getSlotReference();
    
    /// Gets the [EquipmentSlot] this slot object is currently representing.
    /// @return An [Optional] containing the given [EquipmentSlot], or [Optional#empty()] if it is not an equipment slot
    public Optional<EquipmentSlot> getEquipmentSlot()
    {
        return (this.isEquipmentSlot() ? Optional.of(EquipmentSlot.values()[this.getSlotId()]) : Optional.empty());
    }
    
    /// Gets the accessory slot index this slot object is currently representing.
    /// @return An [Optional] containing the given slot index, or [Optional#empty()] if it is not an accessory slot
    public Optional<Short> getAccessorySlot()
    {
        return (this.isAccessorySlot() ? Optional.of(this.getSlotId()) : Optional.empty());
    }
    
    /// Gets the internal slot id of the current slot. If the current slot is an accessory slot then the index of the
    /// slot, if it is an equipment slot, than the ordinal of the given [EquipmentSlot] and if the slot is invalid
    /// than this will contain garbage data.
    /// @return The slot id
    public short getSlotId() { return (short) (this.value & ~AccessorySlot.ACCESSORY_FLAG); }
    
    /// Gets the [ItemStack] of the slot currently represented by this slot object.
    ///
    /// To distinguish between whether the returned [ItemStack] was empty because the slot was empty (or didn't exist),
    /// or this slot object was invalid, use [#isValidSlot()].
    /// @param player The player to search the inventory from
    /// @return The [ItemStack] in the given slot, or [ItemStack#EMPTY] if the slot was empty or this slot object was
    ///         pointing to an invalid slot
    public ItemStack getEquipmentFromPlayer(final Player player)
    {
        if (!this.isValidSlot())
        {
            return ItemStack.EMPTY;
        }
        
        return this.getEquipmentSlot()
            .map(player::getItemBySlot)
            .or(() -> Compat.getAccessory()
                .flatMap(ext -> ext.getSlotItem(player, this.getSlotReference(), this.getSlotId())))
            .orElse(ItemStack.EMPTY);
    }
    
    //==================================================================================================================
    /// {@return `true` if this slot object currently represents an accessory slot}
    public boolean isAccessorySlot() { return ((this.value & AccessorySlot.ACCESSORY_FLAG) != 0); }
    
    /// {@return `true` if this slot object currently represents an equipment slot}
    public boolean isEquipmentSlot() { return (this.value >= 0 && this.value < EquipmentSlot.values().length); }
    
    /// {@return `true` if this slot object currently is neither an accessory nor an equipment slot}
    public boolean isValidSlot() { return (this.isAccessorySlot() || this.isEquipmentSlot()); }
    
    //==================================================================================================================
    @ApiStatus.Internal
    @Override public void set(final int i) { this.value = (short) i; }
    
    /// Sets this slot to be an accessory slot pointing to the given container slot index.
    /// @param accessorySlotIndex The slot index
    /// @throws IllegalArgumentException If the index was negative
    public void setAccessorySlot(final short accessorySlotIndex)
    {
        if (accessorySlotIndex < 0)
        {
            throw new IllegalArgumentException(
                "an extended equipment slot index may not be negative or greater than 32,767");
        }
        
        this.set(accessorySlotIndex | AccessorySlot.ACCESSORY_FLAG);
    }
    
    /// Sets this slot be an equipment slot.
    /// @param equipmentSlot The [EquipmentSlot]
    public void setEquipmentSlot(final EquipmentSlot equipmentSlot) { this.set(equipmentSlot.ordinal()); }
    
    /// Sets this slot to this slot's [#getSlotReference()] equipment slot (see [SlotReference#vanillaPendant()]).
    public void setDefaultEquipmentSlot() { this.setEquipmentSlot(this.getSlotReference().vanillaPendant()); }
    
    //==================================================================================================================
    @Override
    public String toString()
    {
        return this.getEquipmentSlot()
            .map      (slot -> "Equipment@" + slot.getName())
            .orElseGet(()   -> "Accessory@" + this.getSlotReference().id() + '[' + this.getSlotId() + ']');
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)                           return true;
        if (!(obj instanceof AccessorySlot other)) return false;
        return (this.value == other.value && this.getSlotReference().isSame(other.getSlotReference()));
    }
    
    @Override public int hashCode() { return Objects.hash(this.value, this.getSlotReference().id()); }
}
