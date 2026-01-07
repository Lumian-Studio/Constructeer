package xyz.lumian.constructeer.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import xyz.lumian.constructeer.item.ToolbeltItem;

import java.util.Locale;



//**********************************************************************************************************************
public enum LockMode
    implements StringRepresentable
{
    /// Any tool can go in the slot
    UNLOCKED,
    
    /// Only tools that satisfy the filter (and different variants of it like stone or wood pickaxes)
    /// can go in the slot
    LOCKED,
    
    /// Only a specific variant of a specific item can go in the slot
    LOCKED_STRICT,
    ;
    
    //******************************************************************************************************************
    public static final Codec<LockMode> CODEC = StringRepresentable.fromEnum(LockMode::values);
    
    public static final StreamCodec<ByteBuf, LockMode> STREAM_CODEC = ByteBufCodecs.BYTE
        .map((b -> LockMode.values()[(int) b]), (e -> (byte) e.ordinal()));
    
    //******************************************************************************************************************
    @Override public String getSerializedName() { return this.name().toLowerCase(Locale.ROOT); }
}
