/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.multimining.item.multimining.predicate;

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
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;

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
