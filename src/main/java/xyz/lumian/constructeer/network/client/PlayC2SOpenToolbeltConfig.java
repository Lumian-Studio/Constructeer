package xyz.lumian.constructeer.network.client;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.container.slot.ExtendedEquipmentSlot;
import xyz.lumian.constructeer.item.ToolbeltItem;
import xyz.lumian.constructeer.network.IHandleablePayload;
import xyz.lumian.constructeer.network.ModPayloads;



//**********************************************************************************************************************
public record PlayC2SOpenToolbeltConfig(ExtendedEquipmentSlot slot)
    implements IHandleablePayload<ServerPlayNetworking.Context>
{
    //******************************************************************************************************************
    public static final Identifier ID = ModDefine.id("toolbelt_config");
    
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayC2SOpenToolbeltConfig> CODEC = StreamCodec
        .composite(
            ExtendedEquipmentSlot.STREAM_CODEC, PlayC2SOpenToolbeltConfig::slot,
            PlayC2SOpenToolbeltConfig::new);
    
    //******************************************************************************************************************
    @Override public Type<? extends CustomPacketPayload> type() { return ModPayloads.OPEN_TOOLBELT_SCREEN; }
    
    //******************************************************************************************************************
    public void handle(final ServerPlayNetworking.Context ctx)
    {
        final ItemStack stack = this.slot.getEquipmentFromPlayer(ctx.player());
        ctx.player().openMenu(new ToolbeltItem.MenuProvider(this.slot, stack));
    }
}
