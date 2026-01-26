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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.network.ModPayloads;
import xyz.lumian.constructeer.network.client.ConfigureC2SFeatureSyncAck;
import xyz.lumian.constructeer.registry.ModRegistries;

import java.util.Map;
import java.util.Optional;



//**********************************************************************************************************************
public final class ModClientPayloadReceiver
{
    //******************************************************************************************************************
    private static Component createOutOfSyncMessage(
        final String modId,
        final int    serverFeature,
        final int    clientFeature
    )
    {
        return Component.literal(
            modId + " mod feature set is out of sync, "
            + (serverFeature > clientFeature ? "the client" : "the server") + " is not up to date");
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ClientConfigurationNetworking.registerGlobalReceiver(ModPayloads.CONFIGURE_FEATURE_SYNC, ((payload, context) ->
        {
            final Map<String, Integer> ap_types = payload.areaProviderApiVersion();
            
            for (final var entry : ap_types.entrySet())
            {
                final Identifier                    id   = Identifier.parse(entry.getKey());
                final Optional<AreaProviderType<?>> type = ModRegistries.BuiltIn.AREA_PROVIDER_TYPE.getOptional(id);
                
                if (type.isEmpty() || type.orElseThrow().apiVersion() != entry.getValue())
                {
                    context.responseSender().disconnect(createOutOfSyncMessage(
                        id.getNamespace(),
                        entry.getValue(),
                        type.map(AreaProviderType::apiVersion).orElse(0)));
                    return;
                }
            }
            
            context.responseSender().sendPacket(ConfigureC2SFeatureSyncAck.INSTANCE);
        }));
    }
    
    //******************************************************************************************************************
    private ModClientPayloadReceiver() {}
}
