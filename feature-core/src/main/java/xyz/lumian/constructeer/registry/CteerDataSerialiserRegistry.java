package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import xyz.lumian.constructeer.CteerDefine;



//**********************************************************************************************************************
public final class CteerDataSerialiserRegistry
{
    //******************************************************************************************************************
    private static <T> EntityDataSerializer<T> register(final String                                          name,
                                                        final StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        final EntityDataSerializer<T> serialiser = EntityDataSerializer.forValueType(codec);
        FabricTrackedDataRegistry.register(CteerDefine.id(name), serialiser);
        return serialiser;
    }
    
    //******************************************************************************************************************
    private CteerDataSerialiserRegistry() {}
}
