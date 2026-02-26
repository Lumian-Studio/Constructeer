package xyz.lumian.constructeer.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;



//**********************************************************************************************************************
public class CodecUtil
{
    //******************************************************************************************************************
    private record BlockEntityIntermediate(BlockPos pos, BlockState state, CompoundTag compound)
    {
        //**************************************************************************************************************
        public static final Codec<BlockEntityIntermediate> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                BlockPos.CODEC
                    .fieldOf("pos")
                    .forGetter(BlockEntityIntermediate::pos),
                BlockState.CODEC
                    .fieldOf("state")
                    .forGetter(BlockEntityIntermediate::state),
                CompoundTag.CODEC
                    .fieldOf("data")
                    .forGetter(BlockEntityIntermediate::compound))
            .apply(inst, BlockEntityIntermediate::new));
    }
    
    //******************************************************************************************************************
    public static <T extends Enum<T>> StreamCodec<ByteBuf, T> smallEnum(final T[] constants)
    {
        if (constants.length > 256)
        {
            throw new IllegalArgumentException("small enum codec can't have an enum bigger than 256 constants");
        }
        
        return ByteBufCodecs.BYTE.map((b -> constants[Byte.toUnsignedInt(b)]), (c -> (byte) c.ordinal()));
    }
    
    public static <T> StreamCodec<ByteBuf, T> constant(final Supplier<T> factory)
    {
        return new StreamCodec<>() {
            @Override public T decode(final ByteBuf object) { return factory.get(); }
            @Override public void encode(final ByteBuf object, final T object2) {}
        };
    }
}
