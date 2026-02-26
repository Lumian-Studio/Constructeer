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
package xyz.lumian.constructeer.network.payload;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.network.CteerNetworkRegistry;
import xyz.lumian.constructeer.network.IHandleableServerPayload;



//**********************************************************************************************************************
public class ConfigureC2SFeatureSyncAck
    implements IHandleableServerPayload<ServerConfigurationNetworking.Context>
{
    //******************************************************************************************************************
    public static final Identifier ID = CteerDefine.id("feature_sync_ack");
    
    public static final ConfigureC2SFeatureSyncAck INSTANCE = new ConfigureC2SFeatureSyncAck();
    
    public static final StreamCodec<FriendlyByteBuf, ConfigureC2SFeatureSyncAck> STREAM_CODEC
        = StreamCodec.unit(INSTANCE);
    
    //******************************************************************************************************************
    @Override public Type<ConfigureC2SFeatureSyncAck> type() { return CteerNetworkRegistry.CONFIG_FEATURE_SYNC_ACK; }
    
    //==================================================================================================================
    @Override
    public void handle(final ServerConfigurationNetworking.Context ctx)
    {
        ctx.networkHandler().completeTask(ConfigS2CFeatureSync.Task.TYPE);
    }
}
