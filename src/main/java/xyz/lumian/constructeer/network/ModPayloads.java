package xyz.lumian.constructeer.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.lumian.constructeer.network.client.PlayC2SOpenToolbeltConfig;
import xyz.lumian.constructeer.network.client.PlayC2SUpdateHeldTool;



//**********************************************************************************************************************
public final class ModPayloads
{
    //******************************************************************************************************************
    public static final CustomPacketPayload.Type<PlayC2SOpenToolbeltConfig> OPEN_TOOLBELT_SCREEN = registerPlayC2S(
        new CustomPacketPayload.Type<>(PlayC2SOpenToolbeltConfig.ID),
        PlayC2SOpenToolbeltConfig.CODEC);
    public static final CustomPacketPayload.Type<PlayC2SUpdateHeldTool> UPDATE_HELD_TOOL = registerPlayC2S(
        new CustomPacketPayload.Type<>(PlayC2SUpdateHeldTool.ID),
        PlayC2SUpdateHeldTool.CODEC);
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ServerPlayNetworking.registerGlobalReceiver(
            ModPayloads.OPEN_TOOLBELT_SCREEN,
            PlayC2SOpenToolbeltConfig::handle);
        ServerPlayNetworking.registerGlobalReceiver(
            ModPayloads.UPDATE_HELD_TOOL,
            PlayC2SUpdateHeldTool::handle);
    }
    
    //==================================================================================================================
    public static
    <T extends IHandleablePayload<ServerPlayNetworking.Context>> CustomPacketPayload.Type<T> registerPlayC2S(
        final CustomPacketPayload.Type<T>             type,
        final StreamCodec<RegistryFriendlyByteBuf, T> codec
    )
    {
        return PayloadTypeRegistry.playC2S().register(type, codec).type();
    }
    
    //******************************************************************************************************************
    private ModPayloads() {}
}
