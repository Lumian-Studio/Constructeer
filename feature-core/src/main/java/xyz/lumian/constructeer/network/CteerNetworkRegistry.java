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
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.network.payload.ConfigS2CFeatureSync;
import xyz.lumian.constructeer.network.payload.ConfigureC2SFeatureSyncAck;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public final class CteerNetworkRegistry
    implements IBootstrap
{
    //******************************************************************************************************************
    public static final CustomPacketPayload.Type<ConfigS2CFeatureSync>       CONFIG_FEATURE_SYNC;
    public static final CustomPacketPayload.Type<ConfigureC2SFeatureSyncAck> CONFIG_FEATURE_SYNC_ACK;
    
    //==================================================================================================================
    static
    {
        CONFIG_FEATURE_SYNC
            = registerConfigS2C(ConfigS2CFeatureSync.ID, ConfigS2CFeatureSync.STREAM_CODEC);
        CONFIG_FEATURE_SYNC_ACK
            = registerConfigC2S(ConfigureC2SFeatureSyncAck.ID, ConfigureC2SFeatureSyncAck.STREAM_CODEC);
    }
    
    //******************************************************************************************************************
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerConfigS2C(
        final Identifier                      id,
        final StreamCodec<FriendlyByteBuf, T> codec
    )
    {
        final CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(id);
        PayloadTypeRegistry.configurationS2C().register(type, codec);
        Constructeer.sendGlobalBootstrapReport("registered client-bound CONFIG packet '%s'", id);
        return type;
    }
    
    public static <
        T extends IHandleableServerPayload<ServerConfigurationNetworking.Context>
    > CustomPacketPayload.Type<T> registerConfigC2S(
        final Identifier                      id,
        final StreamCodec<FriendlyByteBuf, T> codec
    )
    {
        final CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(id);
        PayloadTypeRegistry.configurationC2S().register(type, codec);
        ServerConfigurationNetworking.registerGlobalReceiver(type, T::handle);
        Constructeer.sendGlobalBootstrapReport("registered server-bound CONFIG packet '%s'", id);
        return type;
    }
    
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T>
    registerPlayS2C(
        final Identifier                              id,
        final StreamCodec<RegistryFriendlyByteBuf, T> codec
    )
    {
        final CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(id);
        PayloadTypeRegistry.playS2C().register(type, codec);
        Constructeer.sendGlobalBootstrapReport("registered client-bound PLAY packet '%s'", id);
        return type;
    }
    
    public static <T extends IHandleableServerPayload<ServerPlayNetworking.Context>> CustomPacketPayload.Type<T>
    registerPlayC2S(
        final Identifier                              id,
        final StreamCodec<RegistryFriendlyByteBuf, T> codec
    )
    {
        final CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(id);
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, T::handle);
        Constructeer.sendGlobalBootstrapReport("registered server-bound PLAY packet '%s'", id);
        return type;
    }
    
    //******************************************************************************************************************
    @Override
    public void bootstrap(final BootstrapReport report)
    {
        ServerConfigurationConnectionEvents.CONFIGURE.register(((handler, server) ->
        {
            // obviously in single-player, the feature set should be pretty much the same
            if (server.isSingleplayer())
            {
                return;
            }
            
            if (ServerConfigurationNetworking.canSend(handler, CteerNetworkRegistry.CONFIG_FEATURE_SYNC))
            {
                handler.addTask(new ConfigS2CFeatureSync.Task());
            }
            else
            {
                handler.disconnect(Component.literal(CteerDefine.MOD_NAME + " mod not supported on client"));
            }
        }));
    }
}
