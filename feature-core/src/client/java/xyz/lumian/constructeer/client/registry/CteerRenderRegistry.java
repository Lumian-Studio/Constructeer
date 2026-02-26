package xyz.lumian.constructeer.client.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.client.integration.ClientCompat;
import xyz.lumian.constructeer.client.integration.accessory.IAccessoryRenderer;
import xyz.lumian.constructeer.registry.IBootstrap;

import java.util.Objects;



//**********************************************************************************************************************
public final class CteerRenderRegistry
    implements IBootstrap
{
    //******************************************************************************************************************
    public static void registerAccessoryRenderer(final Item item, final IAccessoryRenderer renderer)
    {
        if (ClientCompat.ACCESSORY_RENDER_REGISTRY != null)
        {
            ClientCompat.ACCESSORY_RENDER_REGISTRY.register(item, Objects.requireNonNull(renderer));
            Constructeer.sendGlobalBootstrapReport(
                "registering accessory renderer %s for item '%s'",
                renderer.getClass().getName(), BuiltInRegistries.ITEM.getKey(item));
        }
    }
}
