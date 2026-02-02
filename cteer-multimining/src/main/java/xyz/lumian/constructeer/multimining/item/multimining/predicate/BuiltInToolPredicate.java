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
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.multimining.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.config.ConfigHelper;
import xyz.lumian.constructeer.multimining.config.ToolConfig;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.multimining.registry.RegistryId;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;



//**********************************************************************************************************************
public interface BuiltInToolPredicate
    extends IToolPredicate
{
    //******************************************************************************************************************
    record HammerType(IncludeList includeList, HolderSet<Block> excluded, HolderSet<Block> ignored, SneakMode sneakMode,
                      boolean shouldCheckHardness)
        implements BuiltInToolPredicate
    {
        //**************************************************************************************************************
        public static final AtomicReference<HammerType> INSTANCE  = new AtomicReference<>(new HammerType());
        public static final MapCodec<IToolPredicate>    MAP_CODEC = MapCodec.unit(INSTANCE::get);
        
        //==============================================================================================================
        static
        {
            ConstructeerMain.addServerReloadListener(config ->
                HammerType.INSTANCE.setPlain(BuiltInToolPredicate.updateToolConfig(config.hammer(), HammerType::new)));
        }
        
        //**************************************************************************************************************
        public HammerType()
        {
            this(new IncludeList(HolderSet.empty(), ImmutableList.of()), HolderSet.empty(), HolderSet.empty(),
                 SneakMode.WEAK, true);
        }
        
        //==============================================================================================================
        @Override
        public MultiMiningPredicateType<? extends IMultiMiningPredicate> type()
        {
            return MultiMiningPredicateType.HAMMER;
        }
    }
    
    record PlowType(IncludeList includeList, HolderSet<Block> excluded, HolderSet<Block> ignored, SneakMode sneakMode,
                    boolean shouldCheckHardness)
        implements BuiltInToolPredicate
    {
        //**************************************************************************************************************
        public static final AtomicReference<PlowType> INSTANCE  = new AtomicReference<>(new PlowType());
        public static final MapCodec<IToolPredicate>  MAP_CODEC = MapCodec.unit(INSTANCE::get);
        
        //==============================================================================================================
        static
        {
            ConstructeerMain.addServerReloadListener(config ->
                PlowType.INSTANCE.setPlain(BuiltInToolPredicate.updateToolConfig(config.plow(), PlowType::new)));
        }
        
        //**************************************************************************************************************
        public PlowType()
        {
            this(new IncludeList(HolderSet.empty(), ImmutableList.of()), HolderSet.empty(), HolderSet.empty(),
                 SneakMode.WEAK, true);
        }
        
        //==============================================================================================================
        @Override
        public MultiMiningPredicateType<? extends IMultiMiningPredicate> type()
        {
            return MultiMiningPredicateType.PLOW;
        }
    }
    
    record BlockPredicate(Either<RegistryId<Block>, ImmutableList<RegistryId<Block>>> entry)
    {
        //**************************************************************************************************************
        public boolean isGroup() { return this.entry.map(RegistryId::isTag, (l -> true)); }
    }
    
    record IncludeList(HolderSet<Block> blocks, ImmutableList<HolderSet<Block>> groups)
    {
        //**************************************************************************************************************
        public static IncludeList of(final Stream<BlockPredicate> includeList)
        {
            final Registry<Block>                    registry   = BuiltInRegistries.BLOCK;
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
                            ModDefine.LOGGER.warn("could not find any block with id '{}'", reg_id.asIdString());
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
                                ModDefine.LOGGER.warn("could not find any blocks for tag '{}'", reg_id.asIdString());
                                return Stream.empty();
                            }
                            
                            return blocks_opt.stream();
                        }),
                        (list -> list.stream().flatMap(reg_id ->
                        {
                            final Optional<HolderSet<Block>> block_opt = reg_id.resolveOptional(registry);
                            
                            if (block_opt.isEmpty())
                            {
                                ModDefine.LOGGER.warn("could not find any blocks for tag/id '{}'", reg_id.asIdString());
                                return Stream.empty();
                            }
                            
                            return block_opt.stream();
                        }))))
                    .collect(ImmutableList.toImmutableList())
            );
        }
    }
    
    //******************************************************************************************************************
    private static <T extends BuiltInToolPredicate> T updateToolConfig(
        final ToolConfig                                                                        config,
        final Function5<IncludeList, HolderSet<Block>, HolderSet<Block>, SneakMode, Boolean, T> generator
    )
    {
        return generator.apply(
            BuiltInToolPredicate.IncludeList.of(config.includes().get().stream()
                .map(ConfigHelper::resolveBlockPredicate)),
            ConfigHelper.resolveIDs(config.excludes().get().stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, str))),
            ConfigHelper.resolveIDs(config.ignored().get().stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, str))),
            config.sneakMode()    .get(),
            config.checkHardness().get());
    }
    
    //******************************************************************************************************************
    IncludeList includeList();
    
    @Override default HolderSet<Block>       alwaysIncluded() { return this.includeList().blocks; }
    @Override default List<HolderSet<Block>> includeGroups()  { return this.includeList().groups; }
}
