package xyz.lumian.constructeer.integration.accessory;

import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;



//**********************************************************************************************************************
public interface IAccessory
{
    //******************************************************************************************************************
    /// Represents a pointer to an accessory slot containing the string id of the slot and an [EquipmentSlot] specifying
    /// the vanilla pendant of this slot.
    interface SlotReference
    {
        //**************************************************************************************************************
        /// Creates a new [SlotReference] object.
        static SlotReference of(final String id, final EquipmentSlot vanillaPendant)
        {
            Objects.requireNonNull(vanillaPendant);
            
            if (id.isBlank())
            {
                throw new IllegalArgumentException("Slot id may not be blank");
            }
            
            return new SlotReference()
            {
                //******************************************************************************************************
                @Override public String        id()             { return id; }
                @Override public EquipmentSlot vanillaPendant() { return vanillaPendant; }
                
                //======================================================================================================
                @Override public String toString() { return this.toDebugString(); }
            };
        }
        
        //**************************************************************************************************************
        /// {@return the ID string of the targeted slot}
        String id();
        
        /// {@return the vanilla [EquipmentSlot] that acts as a pendant to this slot}
        EquipmentSlot vanillaPendant();
        
        /// Determines whether this slot reference and another slot reference point to the same accessory slot.
        default boolean isSame(final SlotReference other) { return this.id().equals(other.id()); }
        
        //==============================================================================================================
        /// Gets a string representing this slot reference as a string.
        default String toDebugString()
        {
            return ("SlotReference@[id=" + this.id() + ";vanilla=" + this.vanillaPendant().getName() + "]");
        }
    }
    
    /// Represents a set of pre-defined [SlotReference] objects that will be available with multiple different accessory
    /// mods.
    enum SlotConstants
        implements SlotReference
    {
    #if FABRIC
        // Trinkets
        HEAD    ("head/hat",       EquipmentSlot.HEAD),
        BACK    ("chest/back",     EquipmentSlot.BODY),
        BODY    ("chest/cape",     EquipmentSlot.BODY),
        NECKLACE("chest/necklace", EquipmentSlot.BODY),
        BELT    ("legs/belt",      EquipmentSlot.LEGS),
        HAND    ("hand/glove",     EquipmentSlot.MAINHAND),
        RING    ("hand/ring",      EquipmentSlot.MAINHAND),
    #elif NEOFORGE || FORGE
        // Curios API
        HEAD    ("head",     EquipmentSlot.HEAD),
        BACK    ("back",     EquipmentSlot.BODY),
        BODY    ("body",     EquipmentSlot.BODY),
        NECKLACE("necklace", EquipmentSlot.BODY),
        BELT    ("belt",     EquipmentSlot.LEGS),
        HAND    ("hands",    EquipmentSlot.MAINHAND),
        RING    ("ring",     EquipmentSlot.MAINHAND),
    #endif
        ;
        
        //**************************************************************************************************************
        private final String        name;
        private final EquipmentSlot vanilla;
        
        //**************************************************************************************************************
        SlotConstants(final String name, final EquipmentSlot vanilla)
        {
            this.name    = name;
            this.vanilla = vanilla;
        }
        
        //==============================================================================================================
        @Override public String        id()             { return this.name; }
        @Override public EquipmentSlot vanillaPendant() { return this.vanilla; }
    }
    
    //******************************************************************************************************************
    /// Gets the container for the given accessory slot.
    /// @param player The player to get the inventory for
    /// @param ref    The [SlotReference] pointing at the searched slot
    /// @return An [Optional] with the found [Container], otherwise [Optional#empty()] if no slot was found for that
    ///         slot reference
    Optional<Container> getContainer(Player player, SlotReference ref);
    
    /// Gets the [ItemStack] for the given slot reference and index.
    /// @param player The player to get the inventory for
    /// @param ref    The [SlotReference] pointing at the searched slot
    /// @param index  The index of the slot inside the container
    /// @return An [Optional] with the found [ItemStack], otherwise [Optional#empty()] if no slot was found for that
    ///         slot reference
    default Optional<ItemStack> getSlotItem(final Player player, final SlotReference ref, final int index)
    {
        return this.getContainer(player, ref).map(container -> container.getItem(index));
    }
    
    /// Gets all [ItemStack]s inside the container for the given slot reference.
    /// @param player The player to get the inventory for
    /// @param ref    The [SlotReference] pointing at the searched slot
    /// @return An [Stream] with all found [ItemStack]s in the given container, or an empty stream if the container
    ///         could not be found
    default Stream<ItemStackWithSlot> getItems(final Player player, final SlotReference ref)
    {
        final Container container = this.getContainer(player, ref).orElse(null);
        
        if (container == null)
        {
            return Stream.empty();
        }
        
        final Stream.Builder<ItemStackWithSlot> builder = Stream.builder();
        
        for (int i = 0; i < container.getContainerSize(); ++i)
        {
            builder.add(new ItemStackWithSlot(0, container.getItem(i)));
        }
        
        return builder.build();
    }
    
    /// Gets the first [ItemStack] that matched `item` in the given slot reference.
    /// @param player     The player to get the inventory for
    /// @param ref        The [SlotReference] pointing at the searched slot
    /// @param item       The item to search for
    /// @param tryVanilla Should [SlotReference#vanillaPendant()] be searched if no item or container could be found
    /// @return An [Optional] containing the found [ItemStack], or [Optional#empty()] if the stack could not be found
    default Optional<ItemStack> getFirstMatchingItem(final Player player, final SlotReference ref, final Item item,
                                                     final boolean tryVanilla)
    {
        return this.getFirstMatchingItem(player, ref, (stack -> stack.is(item)), tryVanilla);
    }
    
    /// Gets the first [ItemStack] that matched `predicate` in the given slot reference.
    /// @param player     The player to get the inventory for
    /// @param ref        The [SlotReference] pointing at the searched slot
    /// @param predicate  The [ItemStack] predicate to search for
    /// @param tryVanilla Should [SlotReference#vanillaPendant()] be searched if no item or container could be found
    /// @return An [Optional] containing the found [ItemStack], or [Optional#empty()] if the stack could not be found
    default Optional<ItemStack> getFirstMatchingItem(final Player player, final SlotReference ref,
                                                     final Predicate<ItemStack> predicate, final boolean tryVanilla)
    {
        final Container container = this.getContainer(player, ref).orElse(null);
        
        if (container != null)
        {
            for (int i = 0; i < container.getContainerSize(); ++i)
            {
                final ItemStack stack = container.getItem(i);
                
                if (predicate.test(stack))
                {
                    return Optional.of(stack);
                }
            }
        }
        
        if (tryVanilla)
        {
            final ItemStack stack = player.getItemBySlot(ref.vanillaPendant());
            
            if (predicate.test(stack))
            {
                return Optional.of(stack);
            }
        }
        
        return Optional.empty();
    }
    
    //==================================================================================================================
    /// Gets whether the accessory inventory has a container for the given slot reference.
    /// @param player The player to get the inventory for
    /// @param ref    The [SlotReference] pointing at the searched slot
    /// @return `true` if a container could be found
    default boolean hasSlot(final Player player, final SlotReference ref)
    {
        return this.getContainer(player, ref).isPresent();
    }
    
    //==================================================================================================================
    /// Checks whether the player has a given item equipped in the given accessory slot.
    /// @param ref        The [SlotReference] pointing at the searched slot
    /// @param player     The player to get the inventory for
    /// @param predicate  The [ItemStack] predicate to search for
    /// @param tryVanilla Whether [SlotReference#vanillaPendant()] should be searched if no accessory was found
    /// @return `true` if the item was found
    boolean isEquipped(SlotReference ref, Player player, Predicate<ItemStack> predicate, boolean tryVanilla);
    
    /// Checks whether the player has a given item equipped in the given accessory slot.
    /// @param ref        The [SlotReference] pointing at the searched slot
    /// @param player     The player to get the inventory for
    /// @param item       The [Item] to search for
    /// @param tryVanilla Whether [SlotReference#vanillaPendant()] should be searched if no accessory was found
    /// @return `true` if the item was found
    default boolean isEquipped(final SlotReference ref, final Player player, final Item item, final boolean tryVanilla)
    {
        return this.isEquipped(ref, player, (stack -> stack.is(item)), tryVanilla);
    }
    
    /// Checks whether the player has a given item equipped in any of the accessory slots.
    /// @param player      The player to get the inventory for
    /// @param predicate   The [ItemStack] predicate to search for
    /// @param vanillaSlot If not `null`, will search the given equipment slot if no accessory could be found
    /// @return `true` if the item was found
    boolean isEquipped(Player player, Predicate<ItemStack> predicate, @Nullable EquipmentSlot vanillaSlot);
    
    /// Checks whether the player has a given item equipped in any of the accessory slots.
    /// @param player      The player to get the inventory for
    /// @param item        The [Item] to search for
    /// @param vanillaSlot If not `null`, will search the given equipment slot if no accessory could be found
    /// @return `true` if the item was found
    default boolean isEquipped(final Player player, final Item item, final @Nullable EquipmentSlot vanillaSlot)
    {
        return this.isEquipped(player, (stack -> stack.is(item)), vanillaSlot);
    }
}
