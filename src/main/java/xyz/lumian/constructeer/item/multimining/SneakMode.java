package xyz.lumian.constructeer.item.multimining;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.Locale;
import java.util.function.BiPredicate;



//**********************************************************************************************************************
public enum SneakMode
    implements
        StringRepresentable,
        BiPredicate<BlockContext, BlockContext>
{
    NONE   {public boolean test(BlockContext b1, BlockContext b2) { return true;                     }},
    VANILLA{public boolean test(BlockContext b1, BlockContext b2) { return false;                    }},
    WEAK   {public boolean test(BlockContext b1, BlockContext b2) { return b1.state() == b2.state(); }};
    
    //******************************************************************************************************************
    public static final Codec<SneakMode> CODEC = StringRepresentable.fromEnum(SneakMode::values);
    public static final StreamCodec<ByteBuf, SneakMode> STREAM_CODEC = ByteBufCodecs
        .idMapper((i -> SneakMode.values()[i]), SneakMode::ordinal);
    
    //******************************************************************************************************************
    @Override public String getSerializedName() { return this.name().toLowerCase(Locale.ROOT); }
}
