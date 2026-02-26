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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.network.CteerNetworkRegistry;

import java.util.function.Consumer;



//**********************************************************************************************************************
public record ConfigS2CFeatureSync(String modVersion)
    implements CustomPacketPayload
{
    //******************************************************************************************************************
    public record Task()
        implements ConfigurationTask
    {
        //**************************************************************************************************************
        public static final ConfigurationTask.Type TYPE
            = new ConfigurationTask.Type(CteerDefine.formatId("%s_feature_sync"));
        
        //**************************************************************************************************************
        @Override public Type type() { return Task.TYPE; }
        
        //==============================================================================================================
        @Override
        public void start(final Consumer<Packet<?>> task)
        {
            task.accept(ServerConfigurationNetworking.createS2CPacket(new ConfigS2CFeatureSync(CteerDefine.MOD_VERSION)));
        }
    }
    
    //******************************************************************************************************************
    public static final Identifier ID = CteerDefine.id("feature_sync");
    
    public static final StreamCodec<FriendlyByteBuf, ConfigS2CFeatureSync> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ConfigS2CFeatureSync::modVersion,
        ConfigS2CFeatureSync::new);
    
    //******************************************************************************************************************
    @Override public Type<ConfigS2CFeatureSync> type() { return CteerNetworkRegistry.CONFIG_FEATURE_SYNC; }
}
