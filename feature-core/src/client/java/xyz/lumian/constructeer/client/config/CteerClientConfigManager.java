package xyz.lumian.constructeer.client.config;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.config.CteerConfigManager;



//**********************************************************************************************************************
@ApiStatus.Internal
public final class CteerClientConfigManager
    extends CteerConfigManager
{
    //******************************************************************************************************************
    private CteerClientConfigManager()
    {
        // If we are on a dedicated server, we need to reload our configurations
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            if (!client.isLocalServer())
            {
                this.reloadAll(handler.registryAccess());
            }
        });
    }
    
    //==================================================================================================================
    @Override
    @Nullable
    public HolderLookup.Provider getLookup()
    {
        final LocalPlayer player = Minecraft.getInstance().player;
        return (player != null ? player.connection.registryAccess() : null);
    }
}
