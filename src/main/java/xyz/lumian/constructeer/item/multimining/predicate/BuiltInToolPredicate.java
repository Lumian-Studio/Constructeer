package xyz.lumian.constructeer.item.multimining.predicate;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.item.multimining.SneakMode;
import xyz.lumian.constructeer.registry.RegistryId;

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
            ConstructeerMain.addServerReloadListener(config -> INSTANCE.set(new HammerType(
                BuiltInToolPredicate.IncludeList.of(config.hammerIncludes().get().stream()
                    .map(ModServerConfig.BlockPredicate::of)),
                ModServerConfig.resolveIDs(config.hammerExcludes().get().stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, str))),
                ModServerConfig.resolveIDs(config.hammerIgnored().get().stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, str))),
                config.hammerSneakMode()    .get(),
                config.hammerCheckHardness().get())));
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
            ConstructeerMain.addServerReloadListener(config -> INSTANCE.set(new PlowType(
                BuiltInToolPredicate.IncludeList.of(config.plowIncludes().get().stream()
                    .map(ModServerConfig.BlockPredicate::of)),
                ModServerConfig.resolveIDs(config.plowExcludes().get().stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, str))),
                ModServerConfig.resolveIDs(config.plowIgnored().get().stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, str))),
                config.plowSneakMode()    .get(),
                config.plowCheckHardness().get())));
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
    
    record IncludeList(HolderSet<Block> blocks, ImmutableList<HolderSet<Block>> groups)
    {
        //**************************************************************************************************************
        public static IncludeList of(final Stream<ModServerConfig.BlockPredicate> includeList)
        {
            final Registry<Block>                                    registry   = BuiltInRegistries.BLOCK;
            final Map<Boolean, List<ModServerConfig.BlockPredicate>> predicates = includeList
                .collect(Collectors.partitioningBy(ModServerConfig.BlockPredicate::isGroup));
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
    IncludeList includeList();
    
    @Override default HolderSet<Block>       alwaysIncluded() { return this.includeList().blocks; }
    @Override default List<HolderSet<Block>> includeGroups()  { return this.includeList().groups; }
}
