package xyz.lumian.constructeer.client.registry;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerEntityRenderRegistry
{
    //******************************************************************************************************************
    public static <T extends Entity> void register(final EntityType<? extends T>   type,
                                                   final EntityRendererProvider<T> provider)
    {
        EntityRenderers.register(type, provider);
        Constructeer.sendGlobalBootstrapReport("registered entity renderer for type '%s'",
                                               BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }
}
