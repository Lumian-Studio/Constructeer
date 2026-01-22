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
