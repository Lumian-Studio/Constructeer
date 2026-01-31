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
package xyz.lumian.constructeer.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.chat.Component;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.network.ModPayloads;
import xyz.lumian.constructeer.network.client.ConfigureC2SFeatureSyncAck;



//**********************************************************************************************************************
public final class ModClientPayloadReceiver
{
    //******************************************************************************************************************
    private static Component createOutOfSyncMessage(
        @SuppressWarnings("SameParameterValue") final String modId,
        final Version serverFeature,
        final Version clientFeature
    )
    {
        return Component.literal(
            modId + " mod feature set is out of sync, "
            + (serverFeature.compareTo(clientFeature) > 0 ? "the client" : "the server") + " is not up to date");
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ClientConfigurationNetworking.registerGlobalReceiver(ModPayloads.CONFIGURE_FEATURE_SYNC, ((payload, context) ->
        {
            try
            {
                final SemanticVersion server_version = SemanticVersion.parse(payload.modVersion());
                final SemanticVersion client_version = SemanticVersion.parse(ModDefine.MOD_VERSION);
                
                if (server_version.compareTo((Version) client_version) != 0)
                {
                    context.responseSender().disconnect(ModClientPayloadReceiver.createOutOfSyncMessage(
                        ModDefine.MOD_ID,
                        server_version,
                        client_version));
                    return;
                }
            }
            catch (final VersionParsingException ex)
            {
                throw new RuntimeException(ex);
            }
            
            context.responseSender().sendPacket(ConfigureC2SFeatureSyncAck.INSTANCE);
        }));
    }
    
    //******************************************************************************************************************
    private ModClientPayloadReceiver() {}
}
