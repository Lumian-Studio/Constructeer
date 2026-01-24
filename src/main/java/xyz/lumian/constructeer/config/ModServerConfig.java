package xyz.lumian.constructeer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.SneakMode;
import xyz.lumian.constructeer.item.multimining.timber.TimberMode;
import xyz.lumian.constructeer.tag.ModItemTags;
import xyz.lumian.constructeer.registry.RegistryId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;



//**********************************************************************************************************************
public record ModServerConfig(
    // General
    IntValue                  multiMiningHardLimit,
    
    // Toolbelt
    ConfigValue<List<String>> pouchAllowedTools,
    
    // Hammers
    EnumValue<SneakMode>      hammerSneakMode,
    BooleanValue              hammerCheckHardness,
    ConfigValue<List<?>>      hammerIncludes,
    ConfigValue<List<String>> hammerExcludes,
    ConfigValue<List<String>> hammerIgnored,
    EnumValue<TimberMode>     hammerTimberMode,
    
    // Plows
    EnumValue<SneakMode>      plowSneakMode,
    BooleanValue              plowCheckHardness,
    ConfigValue<List<?>>      plowIncludes,
    ConfigValue<List<String>> plowExcludes,
    ConfigValue<List<String>> plowIgnored,
    EnumValue<TimberMode>     plowTimberMode,
    
    // Saw config
    ConfigValue<List<String>> sawValidStemBlocks,
    IntValue                  sawMaxLeafDistance,
    IntValue                  sawMaxBlockCount,
    IntValue                  sawMinLeavesCount,
    BooleanValue              sawChopBelowCut,
    EnumValue<TimberMode>     sawTimberMode,
    BooleanValue              sawStopIfExceedingMaximum
)
{
    //******************************************************************************************************************
    private static final Supplier<String> DEFAULT_ID_SUPPLIER = Suppliers.memoize(() -> "namespace:path");
    
    //******************************************************************************************************************
    public record BlockPredicate(Either<RegistryId<Block>, ImmutableList<RegistryId<Block>>> entry)
    {
        //**************************************************************************************************************
        public static BlockPredicate of(final Object object)
        {
            return new BlockPredicate(switch (object)
            {
                case List<?> list -> Either.right(list.stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, (String) str))
                    .collect(ImmutableList.toImmutableList()));
                case String  str  -> Either.left(RegistryId.parse(Registries.BLOCK, str));
                
                default ->
                    throw new IllegalArgumentException("object is neither a registry id nor a list of registry ids");
            });
        }
        
        public static boolean validate(final Object object)
        {
            if (object instanceof List<?> list)
            {
                for (final var element : list)
                {
                    if (!(element instanceof String str) || !RegistryId.isValidRegistryId(str))
                    {
                        return false;
                    }
                }
                
                return true;
            }
            else if (object instanceof String str)
            {
                return RegistryId.isValidRegistryId(str);
            }
            
            return false;
        }
        
        //**************************************************************************************************************
        public boolean isGroup() { return this.entry.map(RegistryId::isTag, (l -> true)); }
    }
    
    //******************************************************************************************************************
    public static final ModConfigSpec   SPEC;
    public static final ModServerConfig INSTANCE;
    
    //==================================================================================================================
    static
    {
        final Pair<ModServerConfig, ModConfigSpec> result = (new ModConfigSpec.Builder())
            .configure(ModServerConfig::new);
        SPEC     = result.getValue();
        INSTANCE = result.getKey();
    }
    
    //******************************************************************************************************************
    public static HolderSet<Block> resolveIDs(final Stream<RegistryId<Block>> ids)
    {
        final Registry<Block> registry = BuiltInRegistries.BLOCK;
        return HolderSet.direct(ids
            .flatMap(id ->
            {
                final Optional<HolderSet<Block>> blocks_opt = id.resolveOptional(registry);
                
                if (blocks_opt.isEmpty())
                {
                    ModDefine.LOGGER.warn("the given id \"{}\" cannot be found in the block registry", id);
                    return Stream.empty();
                }
                
                return blocks_opt.orElseThrow().stream();
            })
            .distinct()
            .toList());
    }
    
    //==================================================================================================================
    private static boolean validateRegistryId(final Object object)
    {
        return (object instanceof String str && RegistryId.isValidRegistryId(str));
    }
    
    private static boolean validateListOfRegistryIds(final Object object)
    {
        if (!(object instanceof List<?> list))
        {
            return false;
        }
        
        return list.stream().allMatch(ModServerConfig::validateRegistryId);
    }
    
    //******************************************************************************************************************
    public ModServerConfig(final ModConfigSpec.Builder builder)
    {
        this(
            builder
                .comment("Declares the absolute maximum that multi mining tools can destroy, any multi mining action exceeding this limit will be cancelled.")
                .worldRestart()
                .defineInRange("multiMining.hardLimit", 1000, 0, Integer.MAX_VALUE),
            
            builder
                .comment("Declare allowed item IDs that can go in the Constructeer pouch item (tags start with a #).")
                .define(
                    "item.pouch.validTools",
                    (() -> List.of("#" + ModItemTags.COMMON_TOOLS.location())),
                    ModServerConfig::validateListOfRegistryIds),
            
            builder
                .comment("""
                    Determines the behaviour of what should happen when the player is sneaking while using the tool:
                    — none: This will just behave the same as if the player was not sneaking.
                    — vanilla: This will behave as if you are using the vanilla pendant. (meaning, just one block will be mined)
                    — weak: This will only break surrounding blocks that exactly match the actively mined block, ignoring groups and break times.""")
                .defineEnum("item.hammer.sneakMode", SneakMode.WEAK, EnumGetMethod.NAME_IGNORECASE),
            builder
                .comment("Whether neighbouring blocks should only be broken if their hardness is lower, or the same as the actively mined block.")
                .define("item.hammer.checkHardness", true),
            builder
                .comment("""
                    Specifies a set of block groups that should always be mined together, entries can be one of the following:
                    — Block tag: Specifies the blocks that will be mined together if the actively mined block is also inside this tag
                    — Block ID: Specifies the blocks that will always be mined together, regardless of the actively mined block
                    — Block/Tag list: Specifies a list of tags and blocks that should be mined together, much like with block tags""")
                .defineList(
                    "item.hammer.included",
                    (() -> List.of("#" + BlockTags.BASE_STONE_OVERWORLD.location())),
                    ModServerConfig.DEFAULT_ID_SUPPLIER::get,
                    BlockPredicate::validate),
            builder
                .comment("""
                    Specifies a set of block groups that should never be mined together, entries can be one of the following:
                    — Block tag: Specifies that all blocks in the tag will always be ignored from consideration
                    — Block ID: Same as with tags, but for single blocks""")
                .define(
                    "item.hammer.excludes",
                    List::of,
                    ModServerConfig::validateListOfRegistryIds),
            builder
                .comment("""
                    Specifies a set of blocks that should not trigger the tool, entries can be one of the following:
                    — Block tag: Specifies that all blocks inside the tag should be exempt from multi mining in general
                    — Block ID: Same as with tags, but for single blocks""")
                .define(
                    "item.hammer.ignored",
                    List::of,
                    ModServerConfig::validateListOfRegistryIds),
            builder
                .comment("Specifies the behaviour of how blocks are destroyed upon mining with the hammer.")
                .defineEnum("item.hammer.timberMode", TimberMode.INSTANT, EnumGetMethod.NAME_IGNORECASE),
            
            builder.defineEnum("item.plow.sneakMode", SneakMode.WEAK, EnumGetMethod.NAME_IGNORECASE),
            builder.define("item.plow.checkHardness", true),
            builder.defineList(
                "item.plow.included",
                (() -> List.of(("#" + BlockTags.DIRT.location()), ("#" + BlockTags.SNOW.location()))),
                ModServerConfig.DEFAULT_ID_SUPPLIER::get,
                BlockPredicate::validate),
            builder.define(
                "item.plow.excludes",
                List::of,
                ModServerConfig::validateListOfRegistryIds),
            builder.define(
                "item.plow.ignored",
                List::of,
                ModServerConfig::validateListOfRegistryIds),
            builder
                .comment("Specifies the behaviour of how blocks are destroyed upon mining with the plow.")
                .defineEnum("item.plow.timberMode", TimberMode.INSTANT, EnumGetMethod.NAME_IGNORECASE),
            
            builder
                .comment("""
                    Specifies a set of blocks that should be considered valid log blocks and trigger a cutting event:
                    — Block tag: Specifies that all blocks inside the tag should be considered valid tree logs
                    — Block ID: Same as with tags, but for single blocks""")
                .define(
                    "item.saw.validStemBlocks",
                    List.of("#" + BlockTags.LOGS.location()),
                    ModServerConfig::validateListOfRegistryIds),
            builder
                .comment("""
                    Specifies the maximum distance block state value of leaf blocks, at which it won't scan for further neighbouring blocks.
                    See https://minecraft.fandom.com/wiki/Block_states#Leaves""")
                .defineInRange("item.saw.maxLeafDistance", 7, 1, 7),
            builder
                .comment("""
                    Specifies the maximum number of blocks that can be destroyed on one tree.
                    "If this is negative, the multi mining hard limit will be imposed instead.""")
                .defineInRange("item.saw.maxBlockCount", 500, 0, 1000),
            builder
                .comment("Specifies the minimum leaves a tree stem should have so that it is considered a tree.")
                .defineInRange("item.saw.minLeavesCount", 1, 1, Integer.MAX_VALUE),
            builder
                .comment("Specifies whether logs below the cut point should also be cut together with the main tree stem.")
                .define("item.saw.chopBelowCut", false),
            builder
                .comment("Specifies the behaviour of how blocks are destroyed upon chopping with the saw.")
                .defineEnum("item.saw.timberMode", TimberMode.FALLING, EnumGetMethod.NAME_IGNORECASE),
            builder
                .comment("Specifies whether the tree should not be cut if the maximum of blocks has been exceeded.")
                .define("item.saw.stopIfExceedingMaximum", true)
        );
    }
}
