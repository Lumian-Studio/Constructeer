package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;



//**********************************************************************************************************************
public class CteerEntityRegistry
{
    //******************************************************************************************************************
    public static <T extends Entity> EntityType<T> register(final Identifier id, final EntityType.Builder<T> type)
    {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        return CteerRegistries.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }
}
