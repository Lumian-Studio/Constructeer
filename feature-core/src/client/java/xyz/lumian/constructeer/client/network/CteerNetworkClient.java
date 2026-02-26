package xyz.lumian.constructeer.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.chat.Component;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.network.CteerNetworkRegistry;
import xyz.lumian.constructeer.network.payload.ConfigureC2SFeatureSyncAck;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public class CteerNetworkClient
    implements IBootstrap
{
    //******************************************************************************************************************
    @Override
    public void bootstrap(final BootstrapReport report)
    {
        ClientConfigurationNetworking.registerGlobalReceiver(CteerNetworkRegistry.CONFIG_FEATURE_SYNC, ((payload, context) ->
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
}
