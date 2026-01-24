package xyz.lumian.constructeer.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModEntities
{
    //******************************************************************************************************************
    public static final EntityType<FallingObjectEntity> FALLING_OBJECT = register(
        "falling_object",
        EntityType.Builder
            .of(FallingObjectEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(10)
            .updateInterval(1));
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ModEntityDataSerialisers.initialise();
    }
    
    //==================================================================================================================
    private static <T extends Entity> EntityType<T> register(final String name, final EntityType.Builder<T> type)
    {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ModDefine.id(name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }
    
    //******************************************************************************************************************
    private ModEntities() {}
}
