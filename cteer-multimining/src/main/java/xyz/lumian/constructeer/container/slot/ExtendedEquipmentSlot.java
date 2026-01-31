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
package xyz.lumian.constructeer.container.slot;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.impl.ITrinkets;



//**********************************************************************************************************************
public class ExtendedEquipmentSlot
    extends DataSlot
{
    //******************************************************************************************************************
    public static StreamCodec<ByteBuf, ExtendedEquipmentSlot> STREAM_CODEC = ByteBufCodecs.SHORT.map(
        ExtendedEquipmentSlot::new,
        (slot -> slot.value));
    
    //------------------------------------------------------------------------------------------------------------------
    private static final short                 TRINKET_FLAG = (1 << 8);
    private static final ITrinkets.DefaultSlot TRINKET_SLOT = ITrinkets.DefaultSlot.LEGS_BELT;
    
    //******************************************************************************************************************
    public static ItemStack getItemFromTrinket(final Player player, final int slotId)
    {
        return Compat.getTrinkets()
            .flatMap(compat -> compat.getSlotItem(player, ITrinkets.DefaultSlot.LEGS_BELT.asEither(), slotId))
            .orElse(ItemStack.EMPTY);
    }
    
    //******************************************************************************************************************
    private short value;
    
    //******************************************************************************************************************
    public ExtendedEquipmentSlot(final Either<EquipmentSlot, Integer> slot)
    {
        this.value = slot.map(EquipmentSlot::ordinal, (i -> (ExtendedEquipmentSlot.TRINKET_FLAG | i))).shortValue();
    }
    
    public ExtendedEquipmentSlot(final EquipmentSlot mcEquipmentSlot)
    {
        this.value = (short) mcEquipmentSlot.ordinal();
    }
    
    public ExtendedEquipmentSlot(final InteractionHand hand) { this(hand.asEquipmentSlot()); }
    
    public ExtendedEquipmentSlot(final int trinketSlotId)
    {
        this.value = (short) (trinketSlotId | ExtendedEquipmentSlot.TRINKET_FLAG);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private ExtendedEquipmentSlot(final short slotId) { this.value = slotId; }
    
    //==================================================================================================================
    @Override public void set(final int i) { this.value = (short) i; }
    @Override public int  get()            { return this.value; }
    
    public int getSlotId() { return (this.value & ~ExtendedEquipmentSlot.TRINKET_FLAG); }
    
    public ItemStack getEquipmentFromPlayer(final Player player)
    {
        return (this.isTrinketSlot()
            ? ExtendedEquipmentSlot.getItemFromTrinket(player, this.getSlotId())
            : player.getItemBySlot(this.asMcEquipmentSlot()));
    }
    
    //==================================================================================================================
    public EquipmentSlot asMcEquipmentSlot()
    {
        if (this.isTrinketSlot())
        {
            throw new IllegalStateException("was a trinket slot");
        }
        
        return EquipmentSlot.values()[this.getSlotId()];
    }
    
    //==================================================================================================================
    public boolean isTrinketSlot()
    {
        return ((this.value & ExtendedEquipmentSlot.TRINKET_FLAG) == ExtendedEquipmentSlot.TRINKET_FLAG);
    }
    
    //==================================================================================================================
    @Override
    public String toString()
    {
        return (!this.isTrinketSlot()
            ? "EquipmentSlot@"+ this.asMcEquipmentSlot().getName()
            : ExtendedEquipmentSlot.TRINKET_SLOT.toString() + '[' + this.getSlotId() + "]");
    }
}
