package xyz.lumian.constructeer.registry;


import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.CteerDefine;



//**********************************************************************************************************************
public final class CteerDataComponentRegistry
{
    //******************************************************************************************************************
    public static <T> DataComponentType<T> register(final String name, final DataComponentType<T> component)
    {
        final ResourceKey<DataComponentType<?>> key = ResourceKey
            .create(Registries.DATA_COMPONENT_TYPE, CteerDefine.id(name));
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, component);
    }
    
    //******************************************************************************************************************
    private CteerDataComponentRegistry() {}
}
