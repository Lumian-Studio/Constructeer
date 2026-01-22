package xyz.lumian.constructeer.network.client;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.network.IHandleablePayload;
import xyz.lumian.constructeer.network.ModPayloads;
import xyz.lumian.constructeer.network.server.ConfigS2CFeatureSync;



//**********************************************************************************************************************
public class ConfigureC2SFeatureSyncAck
    implements IHandleablePayload<ServerConfigurationNetworking.Context>
{
    //******************************************************************************************************************
    public static final Identifier ID = ModDefine.id("feature_sync_ack");
    
    public static final ConfigureC2SFeatureSyncAck INSTANCE = new ConfigureC2SFeatureSyncAck();
    
    public static final StreamCodec<FriendlyByteBuf, ConfigureC2SFeatureSyncAck> STREAM_CODEC
        = StreamCodec.unit(INSTANCE);
    
    //******************************************************************************************************************
    @Override public Type<ConfigureC2SFeatureSyncAck> type() { return ModPayloads.CONFIGURE_FEATURE_SYNC_ACK; }
    
    //==================================================================================================================
    @Override
    public void handle(final ServerConfigurationNetworking.Context ctx)
    {
        ctx.networkHandler().completeTask(ConfigS2CFeatureSync.Task.TYPE);
    }
}
