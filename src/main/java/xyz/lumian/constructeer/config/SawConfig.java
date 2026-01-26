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
package xyz.lumian.constructeer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.ImmutableList;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.ModConfigSpec;
import xyz.lumian.constructeer.item.multimining.damage.DamageTypes;
import xyz.lumian.constructeer.item.multimining.SneakMode;
import xyz.lumian.constructeer.item.multimining.timber.TimberMode;
import net.neoforged.neoforge.common.ModConfigSpec.*;

import java.util.Arrays;
import java.util.List;



//**********************************************************************************************************************
public record SawConfig(
    ConfigValue<List<String>>   validStemBlocks,
    IntValue                    maxLeafDistance,
    IntValue                    maxBlockCount,
    IntValue                    minLeavesCount,
    BooleanValue                chopBelowCut,
    ConfigValue<String>         timberMode,
    BooleanValue                stopIfExceedingMaximum,
    EnumValue<SneakMode>        sneakMode,
    EnumValue<DamageTypes> damageMultiplier
) implements IMultiMiningConfig
{
    //******************************************************************************************************************
    public SawConfig(final ModConfigSpec.Builder builder)
    {
        this(
            builder
                .comment("""
                    Specifies a set of blocks that should be considered valid log blocks and trigger a cutting event:
                    — Block tag: Specifies that all blocks inside the tag should be considered valid tree logs
                    — Block ID: Same as with tags, but for single blocks""")
                .define(
                    "multiMining.saw.validStemBlocks",
                    List.of("#" + BlockTags.LOGS.location()),
                    ConfigHelper::validateListOfRegistryIds),
            builder
                .comment("""
                    Specifies the maximum distance block state value of leaf blocks, at which it won't scan for further neighbouring blocks.
                    See https://minecraft.fandom.com/wiki/Block_states#Leaves""")
                .defineInRange("multiMining.saw.maxLeafDistance", 7, 1, 7),
            builder
                .comment("""
                    Specifies the maximum number of blocks that can be destroyed on one tree.
                    If this is negative, the multi mining hard limit will be imposed instead.""")
                .defineInRange("multiMining.saw.maxBlockCount", 500, 0, Integer.MAX_VALUE),
            builder
                .comment("Specifies the minimum leaves a tree stem should have so that it is considered a tree.")
                .defineInRange("multiMining.saw.minLeavesCount", 1, 1, Integer.MAX_VALUE),
            builder
                .comment("Specifies whether logs below the cut point should also be cut together with the main tree stem.")
                .define("multiMining.saw.chopBelowCut", false),
            builder
                .comment("Specifies the behaviour of how blocks are destroyed upon chopping trees with the saw.")
                .defineInList("multiMining.saw.timberMode", "FALLING_TREE", ImmutableList.<String>builder()
                    .addAll(Arrays.stream(TimberMode.values()).map(TimberMode::name).toList())
                    .add("FALLING_TREE")
                    .build()),
            builder
                .comment("Specifies whether the tree should not be cut if the maximum of blocks has been exceeded.")
                .define("multiMining.saw.stopIfExceedingMaximum", true),
            builder
                .comment("""
                    Determines the behaviour of what should happen when the player is sneaking while using the tool:
                    — NONE: This will just behave the same as if the player was not sneaking.
                    — VANILLA: This will behave as if you are using the vanilla pendant. (meaning, just one block will be mined)
                    — WEAK: This will only break the tree logs while the leaves will be preserved.""")
                .defineEnum("multiMining.saw.sneakMode", SneakMode.WEAK, EnumGetMethod.NAME),
            builder
                .comment("""
                    Specifies the damage the tool is taking upon destroying a particular area:
                    — SINGLE: Only the block that has been destroyed will account for the tool's damage
                    — ALL: All blocks that have been mined will account for the tool's damage (ignoring leaves)""")
                .defineEnum("multiMining.saw.damageMultiplier", DamageTypes.SINGLE, EnumGetMethod.NAME)
        );
    }
}
