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
package xyz.lumian.constructeer.toolbelt.network.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.integration.accessory.AccessorySlot;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.network.IHandleableServerPayload;
import xyz.lumian.constructeer.toolbelt.item.ToolbeltItem;
import xyz.lumian.constructeer.toolbelt.network.CteerToolbeltPayloads;



//**********************************************************************************************************************
public record PlayC2SOpenToolbeltConfig(AccessorySlot slot)
    implements IHandleableServerPayload<ServerPlayNetworking.Context>
{
    //******************************************************************************************************************
    public static final Identifier ID = CteerDefine.id("toolbelt_config");
    
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayC2SOpenToolbeltConfig> CODEC = StreamCodec.composite(
        AccessorySlot.streamCodec(IAccessory.SlotConstants.BELT), PlayC2SOpenToolbeltConfig::slot,
        PlayC2SOpenToolbeltConfig::new);
    
    //******************************************************************************************************************
    @Override public Type<? extends CustomPacketPayload> type() { return CteerToolbeltPayloads.OPEN_TOOLBELT_SCREEN; }
    
    //******************************************************************************************************************
    public void handle(final ServerPlayNetworking.Context ctx)
    {
        final ItemStack stack = this.slot.getEquipmentFromPlayer(ctx.player());
        ctx.player().openMenu(new ToolbeltItem.MenuProvider(this.slot, stack));
    }
}
