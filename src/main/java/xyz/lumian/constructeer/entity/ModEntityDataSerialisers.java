package xyz.lumian.constructeer.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.component.MultiMining;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;

import java.util.List;



//**********************************************************************************************************************
public final class ModEntityDataSerialisers
{
    //******************************************************************************************************************
    public static final EntityDataSerializer<List<MultiMiningBox.Part>> MULTI_MINING_PARTS = register(
        "multi_mining_parts",
        MultiMiningBox.Part.STREAM_RENDER_CODEC.apply(ByteBufCodecs.list(MultiMining.TIMBER_CAP)));
    
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
