package xyz.lumian.constructeer.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.network.payload.ConfigS2CFeatureSync;
import xyz.lumian.constructeer.network.payload.ConfigureC2SFeatureSyncAck;



//**********************************************************************************************************************
public final class CteerNetwork
{
    //******************************************************************************************************************
    public static final CustomPacketPayload.Type<ConfigS2CFeatureSync> CONFIG_FEATURE_SYNC
        = registerConfigS2C(ConfigS2CFeatureSync.ID, ConfigS2CFeatureSync.STREAM_CODEC);
    
    public static final CustomPacketPayload.Type<ConfigureC2SFeatureSyncAck> CONFIG_FEATURE_SYNC_ACK
        = registerConfigC2S(ConfigureC2SFeatureSyncAck.ID, ConfigureC2SFeatureSyncAck.STREAM_CODEC);
    
    //******************************************************************************************************************
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerConfigS2C(
        final Identifier                      id,
        final StreamCodec<FriendlyByteBuf, T> codec
    )
    {
        final CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(id);
        PayloadTypeRegistry.configurationS2C().register(type, codec);
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
        return type;
    }
    
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
            
            if (ServerConfigurationNetworking.canSend(handler, CteerNetwork.CONFIG_FEATURE_SYNC))
            {
                handler.addTask(new ConfigS2CFeatureSync.Task());
            }
            else
            {
                handler.disconnect(Component.literal(CteerDefine.MOD_NAME + " mod not supported on client"));
            }
        }));
        
        ClientConfigurationNetworking.registerGlobalReceiver(CteerNetwork.CONFIG_FEATURE_SYNC, ((payload, context) ->
        {
            try
            {
                final Version server_version = SemanticVersion.parse(payload.modVersion());
                final Version client_version = SemanticVersion.parse(CteerDefine.MOD_VERSION);
                final int     version_diff   = server_version.compareTo(client_version);
                
                if (version_diff != 0)
                {
                    final String info = "Mod '%s' on client (%s) is out of date with server (%s)".formatted(
                        CteerDefine.MOD_NAME,
                        client_version.getFriendlyString(),
                        server_version.getFriendlyString());
                
                    if (version_diff < 0)
                    {
                        context.responseSender().disconnect(Component.literal(info + ", please downgrade your client"));
                    }
                    else
                    {
                        context.responseSender().disconnect(Component.literal(info + ", please update your client"));
                    }
                }
                else
                {
                    context.responseSender().sendPacket(ConfigureC2SFeatureSyncAck.INSTANCE);
                }
            }
            catch (final VersionParsingException ex)
            {
                throw new RuntimeException(ex);
            }
        }));
    }
    
    //******************************************************************************************************************
    private CteerNetwork() {}
}
