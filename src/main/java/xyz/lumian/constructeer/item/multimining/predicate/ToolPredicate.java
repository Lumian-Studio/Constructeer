package xyz.lumian.constructeer.item.multimining.predicate;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.item.multimining.SneakMode;

import java.util.List;



//**********************************************************************************************************************
public record ToolPredicate(
    HolderSet<Block>       alwaysIncluded,
    List<HolderSet<Block>> includeGroups,
    HolderSet<Block>       excluded,
    HolderSet<Block>       ignored,
    SneakMode              sneakMode,
    boolean                shouldCheckHardness
) implements IToolPredicate
{
    //******************************************************************************************************************
    public static final MapCodec<ToolPredicate> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            RegistryCodecs.homogeneousList(Registries.BLOCK)
                .optionalFieldOf("alwaysIncluded", HolderSet.empty())
                .forGetter(ToolPredicate::alwaysIncluded),
            RegistryCodecs.homogeneousList(Registries.BLOCK).listOf()
                .optionalFieldOf("includeGroups", ImmutableList.of())
                .forGetter(ToolPredicate::includeGroups),
            RegistryCodecs.homogeneousList(Registries.BLOCK)
                .optionalFieldOf("excluded", HolderSet.empty())
                .forGetter(ToolPredicate::excluded),
            RegistryCodecs.homogeneousList(Registries.BLOCK)
                .optionalFieldOf("ignored", HolderSet.empty())
                .forGetter(ToolPredicate::ignored),
            SneakMode.CODEC
                .optionalFieldOf("sneakMode", SneakMode.WEAK)
                .forGetter(ToolPredicate::sneakMode),
            Codec.BOOL
                .optionalFieldOf("checkHardness", true)
                .forGetter(ToolPredicate::shouldCheckHardness))
        .apply(inst, ToolPredicate::new));
    
    public static final Codec<ToolPredicate> CODEC = MAP_CODEC.codec();
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPredicate> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderSet(Registries.BLOCK),                             ToolPredicate::alwaysIncluded,
        ByteBufCodecs.holderSet(Registries.BLOCK).apply(ByteBufCodecs.list()), ToolPredicate::includeGroups,
        ByteBufCodecs.holderSet(Registries.BLOCK),                             ToolPredicate::excluded,
        ByteBufCodecs.holderSet(Registries.BLOCK),                             ToolPredicate::ignored,
        SneakMode.STREAM_CODEC,                                                ToolPredicate::sneakMode,
        ByteBufCodecs.BOOL,                                                    ToolPredicate::shouldCheckHardness,
        ToolPredicate::new);
    
    //******************************************************************************************************************
    public ToolPredicate()
    {
        this(HolderSet.empty(), ImmutableList.of(), HolderSet.empty(), HolderSet.empty(), SneakMode.WEAK, true);
    }
    
    //==================================================================================================================
    @Override public MultiMiningPredicateType<ToolPredicate> type() { return MultiMiningPredicateType.TOOL; }
}
