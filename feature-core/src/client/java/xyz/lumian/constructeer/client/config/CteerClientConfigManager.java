package xyz.lumian.constructeer.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.config.CteerConfigManager;



//**********************************************************************************************************************
public final class CteerClientConfigManager
    extends CteerConfigManager
{
    //******************************************************************************************************************
    @Override
    @Nullable
    public HolderLookup.Provider getLookup()
    {
        final ClientLevel level = Minecraft.getInstance().level;
        return (level != null ? level.registryAccess() : null);
    }
}
