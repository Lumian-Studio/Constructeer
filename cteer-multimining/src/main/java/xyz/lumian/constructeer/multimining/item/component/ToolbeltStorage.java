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
package xyz.lumian.constructeer.multimining.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.item.ModItems;
import xyz.lumian.constructeer.multimining.item.ToolbeltItem;
import xyz.lumian.constructeer.multimining.tag.ModItemTags;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;



//**********************************************************************************************************************
/// Represents the pouch storage of a [ToolbeltItem].
/// This is an immutable data structure, modifications need to be done by creating new instances such as with
/// [#withPouch(int, ItemStack)] or [#withEmptyPouch(int)].
public class ToolbeltStorage
    implements
        TooltipProvider,
        Iterable<ItemStack>
{
    //******************************************************************************************************************
    /// @param id    The slot id of the pouch
    /// @param pouch The pouch [ItemStack]
    public record Entry(int id, ItemStack pouch)
    {
        //**************************************************************************************************************
        /// The [Codec] for toolbelt entries.
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                Codec.intRange(0, (ToolbeltItem.COUNT_POUCHES - 1))
                    .fieldOf("slot")
                    .forGetter(Entry::id),
                ItemStack.OPTIONAL_CODEC
                    .fieldOf("pouch")
                    .forGetter(Entry::pouch))
            .apply(instance, Entry::new));
    }
    
    //******************************************************************************************************************
    /// An empty toolbelt storage instance used to initialise items with.
    public static final ToolbeltStorage EMPTY = new ToolbeltStorage();
    
    /// The [Codec] for the toolbelt storage class.
    public static final Codec<ToolbeltStorage> CODEC;
    
    /// The network [StreamCodec] for the toolbelt storage class.
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolbeltStorage> STREAM_CODEC;
    
    //==================================================================================================================
    static
    {
        CODEC        = Entry.CODEC.sizeLimitedListOf(ToolbeltItem.COUNT_POUCHES)
            .xmap(ToolbeltStorage::fromEntries, ToolbeltStorage::asEntries);
        STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .apply(ByteBufCodecs.list(ToolbeltItem.COUNT_POUCHES))
            .map(ToolbeltStorage::fromItems, ToolbeltStorage::asItems);
    }
    
    //******************************************************************************************************************
    /// Creates a new [ToolbeltStorage] from the given list of [Entry] objects.
    /// @param entries The entries to construct the storage instance from
    /// @return The new [ToolbeltStorage]
    /// @throws IllegalArgumentException If the entry's [Entry#id()] is out of bounds or any of the entry's item stacks
    ///                                  was not representing a [ModItems#POUCH]
    public static ToolbeltStorage fromEntries(final Collection<Entry> entries)
    {
        final int         id_cap  = (ToolbeltItem.COUNT_POUCHES - 1);
        final ItemStack[] pouches = new ItemStack[ToolbeltItem.COUNT_POUCHES];
        
        for (final var entry : entries)
        {
            if (entry.id > id_cap || entry.id < 0)
            {
                throw new IllegalArgumentException("invalid pouch id " + entry.id);
            }
            
            pouches[entry.id] = entry.pouch;
        }
        
        return new ToolbeltStorage(pouches);
    }
    
    /// Creates a new [ToolbeltStorage] from the given list of [ItemStack] objects. The IDs of the pouches depend on
    /// the order of the stacks in the input list.
    /// @param stacks The [ItemStack] objects to construct the storage instance from
    /// @return The new [ToolbeltStorage]
    /// @throws IllegalArgumentException If any of the item stacks was not representing a [ModItems#POUCH]
    public static ToolbeltStorage fromItems(final Collection<ItemStack> stacks)
    {
        return new ToolbeltStorage(stacks.toArray(ItemStack[]::new));
    }
    
    //==================================================================================================================
    public static UnaryOperator<ToolbeltStorage> updateOp(final int id, final ItemStack stack)
    {
        return (storage -> storage.withPouch(id, stack.copy()));
    }
    
    public static UnaryOperator<ToolbeltStorage> emptyOp(final int id)
    {
        return (storage -> storage.withEmptyPouch(id));
    }
    
    //******************************************************************************************************************
    private final ItemStack[] pouches = new ItemStack[ToolbeltItem.COUNT_POUCHES];
    
    //******************************************************************************************************************
    /// Verifies that the given [ItemStack] is a valid [ModItems#POUCH] and converts it to fit into the toolbelt
    /// storage.
    /// @param stack The [ItemStack] to verify
    /// @return If `stack` was ok then `stack`, otherwise if [ItemStack#isEmpty()] evaluated to `true` then
    ///         [ItemStack#EMPTY]
    /// @throws IllegalArgumentException If the item stack was not representing a [ModItems#POUCH]
    public static ItemStack verify(final ItemStack stack)
    {
        if (!stack.isEmpty() && !stack.is(ModItemTags.POUCHES))
        {
            throw new IllegalArgumentException("item is not a pouch (got: " + stack + ")");
        }
        
        return stack;
    }
    
    //******************************************************************************************************************
    /// Constructs a new [ToolbeltStorage] instance.
    /// @param initPouches The [ItemStack] objects to fill the storage with, any stack at an index greater than
    ///                    "[ToolbeltItem#COUNT_POUCHES] - 1" will be dropped. If any of the stacks'
    ///                    [ItemStack#isEmpty()] evaluates to `true`, this stack will be considered as "no pouch"
    /// @throws IllegalArgumentException If any of the item stacks was not representing a [ModItems#POUCH]
    public ToolbeltStorage(final ItemStack ...initPouches)
    {
        final int maxlen = Math.min(this.pouches.length, initPouches.length);
        
        for (int i = 0; i < maxlen; ++i)
        {
            this.pouches[i] = ToolbeltStorage.verify(initPouches[i]);
        }
        
        if (maxlen < this.pouches.length)
        {
            Arrays.fill(this.pouches, maxlen, this.pouches.length, ItemStack.EMPTY);
        }
    }
    
    //==================================================================================================================
    /// Gets the pouch with the given ID.
    /// @param id The id of the pouch
    /// @return An [Optional] containing the pouch [ItemStack] or an empty optional if no pouch was given for the slot.
    public Optional<ItemStack> getPouch(final int id)
    {
        if (id < 0 || id >= this.pouches.length)
        {
            throw new IndexOutOfBoundsException("pouch id " + id + " is out of bounds (of max. "
                                                + (ToolbeltItem.COUNT_POUCHES - 1) + ")");
        }
        
        final ItemStack pouch = this.pouches[id];
        return (!pouch.isEmpty() ? Optional.of(pouch) : Optional.empty());
    }
    
    /// Gets the pouch with the given ID unchecked.
    /// @param id The id of the pouch
    /// @return The [ItemStack] at the given ID, or throws an exception if out of bounds
    public ItemStack getPouchUnsafe(final int id) { return this.pouches[id]; }
    
    /// A [Stream] with every slot of this storage being represented by an [Entry]. Other than [#stream()], empty slots
    /// will not be omitted but instead represented by setting an entry's [Entry#pouch()] to [ItemStack#EMPTY].
    /// @return The [Stream].
    public Stream<Entry> streamIntrusive()
    {
        return IntStream
            .range(0, this.pouches.length)
            .mapToObj(i -> new Entry(i, this.pouches[i]));
    }
    
    /// A [Stream] with every slot of this storage, that contains a pouch, being represented by an [Entry]. Empty slots
    /// will be omitted, if this is not the desired behaviour use [#streamIntrusive()] instead.
    /// @return The [Stream].
    public Stream<Entry> stream()
    {
        return IntStream
            .range(0, this.pouches.length)
            .filter(i -> !this.pouches[i].isEmpty())
            .mapToObj(i -> new Entry(i, this.pouches[i]));
    }
    
    /// Converts this storage instance to a list of [Entry] objects representing the contents of every slot.
    /// @return The list of pouch entries.
    public List<Entry> asEntries() { return this.streamIntrusive().toList(); }
    
    /// Converts this storage instance to a list of [ItemStack] objects representing the contents of every slot.
    /// @return The list of pouch [ItemStack] objects.
    public List<ItemStack> asItems() { return ImmutableList.copyOf(this.pouches); }
    
    /// {@return the number of upgrades in this storage instance}
    public int numUpgrades()
    {
        int c = 0;
        
        for (final var pouch : this.pouches)
        {
            if (!pouch.isEmpty())
            {
                ++c;
            }
        }
        
        return c;
    }
    
    //==================================================================================================================
    /// Determines whether this storage instance has an [ItemStack] which does not satisfy [ItemStack#isEmpty()].
    /// @param id The id of the pouch
    /// @return `true` if the pouch is non-empty
    public boolean hasPouch(final int id)
    {
        if (id < 0 || id >= this.pouches.length)
        {
            return false;
        }
        
        return !this.pouches[id].isEmpty();
    }
    
    /// {@return whether this storage instance has at least one upgrade}
    public boolean hasUpgrades()
    {
        for (final var pouch : this.pouches)
        {
            if (!pouch.isEmpty())
            {
                return true;
            }
        }
        
        return false;
    }
    
    //==================================================================================================================
    /// Creates a new [ToolbeltStorage] instance with the given slot at `id` set to the contents of `pouch`.
    /// @param id    The slot ID of the pouch
    /// @param pouch The pouch [ItemStack], if [ItemStack#isEmpty()] evaluated to true, will empty the slot
    /// @return The new [ToolbeltStorage] instance.
    /// @throws IllegalArgumentException If `pouch` was not representing a [ModItems#POUCH]
    public ToolbeltStorage withPouch(final int id, final ItemStack pouch)
    {
        if (id < 0 || id >= this.pouches.length)
        {
            throw new IndexOutOfBoundsException("pouch id " + id + " is out of bounds (of max. "
                                                + (ToolbeltItem.COUNT_POUCHES - 1) + ")");
        }
        
        if (this.pouches[id] == pouch)
        {
            return this;
        }
        
        final ItemStack[] new_pouches = this.pouches.clone();
        new_pouches[id] = ToolbeltStorage.verify(pouch);
        return new ToolbeltStorage(new_pouches);
    }
    
    /// Creates a new [ToolbeltStorage] instance with the given slot at `id` emptied.
    /// @param id The slot ID of the pouch to empty
    /// @return The new [ToolbeltStorage] instance.
    public ToolbeltStorage withEmptyPouch(final int id) { return this.withPouch(id, ItemStack.EMPTY); }
    
    //==================================================================================================================
    @Override
    public void addToTooltip(final Item.TooltipContext tooltipContext, final Consumer<Component> consumer,
                             final TooltipFlag tooltipFlag, final DataComponentGetter dataComponentGetter)
    {
        final MutableComponent component = Component.empty();
        int upgrade_count = 0;
        
        for (var it = this.stream().iterator(); it.hasNext();)
        {
            final var entry = it.next();
            ++upgrade_count;
            
            final PouchContent tool = entry.pouch().get(ModComponents.POUCH_CONTENT);
            
            if (tool != null && !tool.content().isEmpty())
            {
                component.append(tool.toComponent());
            }
        }
        
        if (upgrade_count == 0)
        {
            return;
        }
        
        consumer.accept(ModLang.TOOLBELT_TOOLTIP_POUCH_COUNT.withArgs(upgrade_count).withStyle(ChatFormatting.GRAY));
        
        if (!component.getSiblings().isEmpty())
        {
            consumer.accept(ModLang.TOOLBELT_TOOLTIP_TOOLS.withArgs(component).withStyle(ChatFormatting.GRAY));
        }
    }
    
    //==================================================================================================================
    @Override public Iterator<ItemStack> iterator() { return Arrays.stream(this.pouches).iterator(); }
    
    //==================================================================================================================
    public boolean equals(final Object object)
    {
        if (this == object)                             return true;
        if (!(object instanceof ToolbeltStorage other)) return false;
        
        for (int i = 0; i < this.pouches.length; ++i)
        {
            if (!ItemStack.matches(this.pouches[i], other.pouches[i]))
            {
                return false;
            }
        }
        
        return true;
    }

    @SuppressWarnings("deprecation")
    public int hashCode() { return ItemStack.hashStackList(Arrays.asList(this.pouches)); }
}
