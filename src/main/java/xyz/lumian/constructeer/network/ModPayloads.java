package xyz.lumian.constructeer.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.network.client.ConfigureC2SFeatureSyncAck;
import xyz.lumian.constructeer.network.client.PlayC2SOpenToolbeltConfig;
import xyz.lumian.constructeer.network.client.PlayC2SUpdateHeldTool;
import xyz.lumian.constructeer.network.server.ConfigS2CFeatureSync;



//**********************************************************************************************************************
public final class ModPayloads
{
    //******************************************************************************************************************
    // Configuration client -> server
    public static final CustomPacketPayload.Type<ConfigureC2SFeatureSyncAck> CONFIGURE_FEATURE_SYNC_ACK
        = registerConfigC2S(
            new CustomPacketPayload.Type<>(ConfigureC2SFeatureSyncAck.ID),
            ConfigureC2SFeatureSyncAck.STREAM_CODEC);
    
    // Configuration server -> client
    public static final CustomPacketPayload.Type<ConfigS2CFeatureSync> CONFIGURE_FEATURE_SYNC = registerConfigS2C(
        new CustomPacketPayload.Type<>(ConfigS2CFeatureSync.ID),
        ConfigS2CFeatureSync.STREAM_CODEC);
    
    // Play client -> server
    public static final CustomPacketPayload.Type<PlayC2SOpenToolbeltConfig> OPEN_TOOLBELT_SCREEN = registerPlayC2S(
        new CustomPacketPayload.Type<>(PlayC2SOpenToolbeltConfig.ID),
        PlayC2SOpenToolbeltConfig.CODEC);
    public static final CustomPacketPayload.Type<PlayC2SUpdateHeldTool> UPDATE_HELD_TOOL = registerPlayC2S(
        new CustomPacketPayload.Type<>(PlayC2SUpdateHeldTool.ID),
        PlayC2SUpdateHeldTool.CODEC);
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ServerConfigurationConnectionEvents.CONFIGURE.register(((handler, server) ->
        {
            // obviously in single-player, the feature set should be pretty much the same
            if (server.isSingleplayer())
            {
                return;
            }
            
            if (ServerConfigurationNetworking.canSend(handler, ModPayloads.CONFIGURE_FEATURE_SYNC))
            {
                handler.addTask(new ConfigS2CFeatureSync.Task());
            }
            else
            {
                handler.disconnect(Component.literal(ModDefine.MOD_NAME + " mod not supported on client"));
            }
        }));
    }
    
    //==================================================================================================================
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerConfigS2C(
        final CustomPacketPayload.Type<T>     type,
        final StreamCodec<FriendlyByteBuf, T> codec
    )
    {
        PayloadTypeRegistry.configurationS2C().register(type, codec);
        return type;
    }
    
    public static
        <T extends IHandleablePayload<ServerConfigurationNetworking.Context>> CustomPacketPayload.Type<T>
        registerConfigC2S(
            final CustomPacketPayload.Type<T>     type,
            final StreamCodec<FriendlyByteBuf, T> codec
        )
    {
        PayloadTypeRegistry.configurationC2S().register(type, codec);
        ServerConfigurationNetworking.registerGlobalReceiver(type, T::handle);
        return type;
    }
    
    public static
        <T extends IHandleablePayload<ServerPlayNetworking.Context>> CustomPacketPayload.Type<T>
        registerPlayC2S(
            final CustomPacketPayload.Type<T>             type,
            final StreamCodec<RegistryFriendlyByteBuf, T> codec
        )
    {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, T::handle);
        return type;
    }
    
    //******************************************************************************************************************
    private ModPayloads() {}
}
