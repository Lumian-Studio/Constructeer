package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerEntityDataSerialiserRegistry
{
    //******************************************************************************************************************
    public static <T> EntityDataSerializer<T> register(final Identifier                                      id,
                                                       final StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        final EntityDataSerializer<T> serialiser = EntityDataSerializer.forValueType(codec);
        FabricTrackedDataRegistry.register(id, serialiser);
        Constructeer.sendGlobalBootstrapReport("registered entity data serialiser '%s'", id);
        return serialiser;
    }
}
