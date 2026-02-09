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
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.registry.RegistryId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;



//**********************************************************************************************************************
public record ToolPredicate(
    IncludeList      included,
    HolderSet<Block> excluded,
    HolderSet<Block> ignored,
    SneakMode        sneakMode,
    boolean          shouldCheckHardness
) implements IToolPredicate
{
    //******************************************************************************************************************
    public record BlockPredicate(Either<RegistryId<Block>, ImmutableList<RegistryId<Block>>> entry)
    {
        //**************************************************************************************************************
        public boolean isGroup() { return this.entry.map(RegistryId::isTag, (l -> true)); }
    }
    
    public record IncludeList(HolderSet<Block> blocks, ImmutableList<HolderSet<Block>> groups)
    {
        //**************************************************************************************************************
        public static final IncludeList EMPTY = new IncludeList(HolderSet.empty(), ImmutableList.of());
        
        public static final MapCodec<IncludeList> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(
                RegistryCodecs.homogeneousList(Registries.BLOCK)
                    .fieldOf("alwaysIncluded")
                    .forGetter(IncludeList::blocks),
                RegistryCodecs.homogeneousList(Registries.BLOCK).listOf()
                    .fieldOf("includeGroups")
                    .xmap(ImmutableList::copyOf, Function.identity())
                    .forGetter(IncludeList::groups))
            .apply(inst, IncludeList::new));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, IncludeList> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.BLOCK), IncludeList::blocks,
            ByteBufCodecs.holderSet(Registries.BLOCK)
                .apply(ByteBufCodecs.list())
                .map(ImmutableList::copyOf, Function.identity()), IncludeList::groups,
            IncludeList::new);
        
        //**************************************************************************************************************
        public static IncludeList ofPredicates(final Stream<BlockPredicate> includeList)
        {
            final Registry<Block> registry = BuiltInRegistries.BLOCK;
            final Map<Boolean, List<BlockPredicate>> predicates = includeList
                .collect(Collectors.partitioningBy(BlockPredicate::isGroup));
            return new IncludeList(
                HolderSet.direct(predicates.get(false).stream()
                    .flatMap(pred ->
                    {
                        final RegistryId<Block>       reg_id    = pred.entry().left().orElseThrow();
                        final Optional<Holder<Block>> block_opt = reg_id.resolveSingleOptional(registry);
                        
                        if (block_opt.isEmpty())
                        {
                            CteerDefine.LOGGER.warn("could not find any block with id '{}'", reg_id.asIdString());
                            return Stream.empty();
                        }
                        
                        return block_opt.stream();
                    })
                    .toList()),
                predicates.get(true).stream()
                    .flatMap(pred -> pred.entry().map(
                        (reg_id ->
                        {
                            final Optional<HolderSet<Block>> blocks_opt = reg_id.resolveOptional(registry);
                            
                            if (blocks_opt.isEmpty())
                            {
                                CteerDefine.LOGGER.warn("could not find any blocks for tag '{}'", reg_id.asIdString());
                                return Stream.empty();
                            }
                            
                            return blocks_opt.stream();
                        }),
                        (list -> list.stream().flatMap(reg_id ->
                        {
                            final Optional<HolderSet<Block>> block_opt = reg_id.resolveOptional(registry);
                            
                            if (block_opt.isEmpty())
                            {
                                CteerDefine.LOGGER.warn("could not find any blocks for tag/id '{}'",
                                    reg_id.asIdString());
                                return Stream.empty();
                            }
                            
                            return block_opt.stream();
                        }))))
                    .collect(ImmutableList.toImmutableList())
            );
        }
    }
    
    //******************************************************************************************************************
    public static final Codec<ToolPredicate> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            IncludeList.MAP_CODEC
                .forGetter(ToolPredicate::included),
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
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPredicate> STREAM_CODEC = StreamCodec.composite(
        IncludeList.STREAM_CODEC,                  ToolPredicate::included,
        ByteBufCodecs.holderSet(Registries.BLOCK), ToolPredicate::excluded,
        ByteBufCodecs.holderSet(Registries.BLOCK), ToolPredicate::ignored,
        SneakMode.STREAM_CODEC,                    ToolPredicate::sneakMode,
        ByteBufCodecs.BOOL,                        ToolPredicate::shouldCheckHardness,
        ToolPredicate::new);
    
    //******************************************************************************************************************
    public ToolPredicate() { this(IncludeList.EMPTY, HolderSet.empty(), HolderSet.empty(), SneakMode.WEAK, true); }
    
    //==================================================================================================================
    @Override public HolderSet<Block>       alwaysIncluded() { return this.included.blocks(); }
    @Override public List<HolderSet<Block>> includeGroups()  { return this.included.groups(); }
}
