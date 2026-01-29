package xyz.lumian.constructeer.item.multimining;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.registry.ModRegistries;

import java.util.function.Supplier;



//**********************************************************************************************************************
public record MultiMiningType<T extends IMultiMining>(
    MapCodec<T>                             codec,
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
)
{
    //******************************************************************************************************************
    // HARDCODED
    public static final MultiMiningType<BuiltInMultiMining> BUILTIN_HAMMER
        = register(ModDefine.id("builtin_hammer"), (() -> BuiltInMultiMining.HAMMER));
    public static final MultiMiningType<BuiltInMultiMining> BUILTIN_PLOW
        = register(ModDefine.id("builtin_plow"),   (() -> BuiltInMultiMining.PLOW));
    public static final MultiMiningType<BuiltInMultiMining> BUILTIN_SAW
        = register(ModDefine.id("builtin_saw"),    (() -> BuiltInMultiMining.SAW));
    
    // REUSABLE
    public static final MultiMiningType<MultiMining> CUSTOM
        = register(ModDefine.id("custom"), MultiMining.MAP_CODEC, MultiMining.STREAM_CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    public static <T extends IMultiMining> MultiMiningType<T> register(
        final Identifier                              id,
        final MapCodec<T>                             codec,
        final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    )
    {
        return Registry
            .register(ModRegistries.BuiltIn.MULTI_MINING_TYPE, id, new MultiMiningType<>(codec, streamCodec));
    }
    
    public static MultiMiningType<BuiltInMultiMining> register(final Identifier                   id,
                                                               final Supplier<BuiltInMultiMining> unit)
    {
        final MapCodec<BuiltInMultiMining> unit_codec = MapCodec.unit(unit);
        return MultiMiningType.register(id, unit_codec, ByteBufCodecs.fromCodecWithRegistries(unit_codec.codec()));
    }
}
