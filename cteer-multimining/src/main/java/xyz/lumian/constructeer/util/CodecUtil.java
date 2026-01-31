package xyz.lumian.constructeer.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;



//**********************************************************************************************************************
public class CodecUtil
{
    //******************************************************************************************************************
    public static <T extends Enum<T>> StreamCodec<ByteBuf, T> smallEnum(final T[] constants)
    {
        if (constants.length > 256)
        {
            throw new IllegalArgumentException("small enum codec can't have an enum bigger than 256 constants");
        }
        
        return ByteBufCodecs.BYTE.map((b -> constants[Byte.toUnsignedInt(b)]), (c -> (byte) c.ordinal()));
    }
}
