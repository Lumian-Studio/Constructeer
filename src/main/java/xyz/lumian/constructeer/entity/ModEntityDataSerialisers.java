package xyz.lumian.constructeer.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;



//**********************************************************************************************************************
public final class ModEntityDataSerialisers
{
    //******************************************************************************************************************
    public static final EntityDataSerializer<MultiMiningBox> FALLING_OBJECT_RENDER_BOX
        = register("falling_object_render_box", MultiMiningBox.STREAM_CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static <T> EntityDataSerializer<T> register(final String name,
                                                        final StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        final EntityDataSerializer<T> serialiser = EntityDataSerializer.forValueType(codec);
        FabricTrackedDataRegistry.register(ModDefine.id(name), serialiser);
        return serialiser;
    }
    
    //******************************************************************************************************************
    private ModEntityDataSerialisers() {}
}
