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
package xyz.lumian.constructeer.multimining.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.common.ModConfigSpec;
import xyz.lumian.constructeer.config.ConfigHelper;
import xyz.lumian.constructeer.multimining.item.multimining.MmFactory;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMining;
import xyz.lumian.constructeer.multimining.item.multimining.area.ToolProvider;
import xyz.lumian.constructeer.multimining.item.multimining.damage.DamageType;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.ToolPredicate;
import xyz.lumian.constructeer.multimining.item.multimining.timber.TimberMode;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import xyz.lumian.constructeer.registry.RegistryId;

import java.util.List;



//**********************************************************************************************************************
public record ToolConfig(
    BooleanValue              checkHardness,
    ConfigValue<List<?>>      includes,
    ConfigValue<List<String>> excludes,
    ConfigValue<List<String>> ignored,
    EnumValue<TimberMode>     timberMode,
    EnumValue<SneakMode>      sneakMode,
    EnumValue<DamageType>     damageMultiplier
) implements MmFactory
{
    //******************************************************************************************************************
    public ToolConfig(final ModConfigSpec.Builder builder, final String toolName, final boolean defaultCheckHardness,
                      final List<?> defaultIncludes, final List<String> defaultExcludes,
                      final List<String> defaultIgnored, final TimberMode defaultTimberMode,
                      final SneakMode defaultSneakMode, final DamageType defaultDamageType)
    {
        this(builder, toolName, ("multiMining." + toolName), defaultCheckHardness, defaultIncludes, defaultExcludes,
             defaultIgnored, defaultTimberMode, defaultSneakMode, defaultDamageType);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private ToolConfig(final ModConfigSpec.Builder builder, final String toolName, final String prefix,
                       final boolean defaultCheckHardness, final List<?> defaultIncludes,
                       final List<String> defaultExcludes, final List<String> defaultIgnored,
                       final TimberMode defaultTimberMode, final SneakMode defaultSneakMode,
                       final DamageType defaultDamageType)
    {
        this(
            builder
                .comment("Whether neighbouring blocks should only be broken if their hardness is lower, or the same as the actively mined block.")
                .define((prefix + ".checkHardness"), defaultCheckHardness),
            builder
                .comment("""
                    Specifies a set of block groups that should always be mined together, entries can be one of the following:
                    — Block tag: Specifies the blocks that will be mined together if the actively mined block is also inside this tag
                    — Block ID: Specifies the blocks that will always be mined together, regardless of the actively mined block
                    — Block/Tag list: Specifies a list of tags and blocks that should be mined together, much like with block tags""")
                .defineList(
                    (prefix + ".included"),
                    (() -> defaultIncludes),
                    ConfigHelper.DEFAULT_ID_SUPPLIER::get,
                    CteerMultiMiningServerConfig::validateBlockPredicate),
            builder
                .comment("""
                    Specifies a set of block groups that should never be mined together, entries can be one of the following:
                    — Block tag: Specifies that all blocks in the tag will always be ignored from consideration
                    — Block ID: Same as with tags, but for single blocks""")
                .define((prefix + ".excludes"), defaultExcludes, ConfigHelper::validateListOfRegistryIds),
            builder
                .comment("""
                    Specifies a set of blocks that should not trigger the tool, entries can be one of the following:
                    — Block tag: Specifies that all blocks inside the tag should be exempt from multi mining in general
                    — Block ID: Same as with tags, but for single blocks""")
                .define((prefix + ".ignored"), defaultIgnored, ConfigHelper::validateListOfRegistryIds),
            builder
                .comment("Specifies the behaviour of how blocks are destroyed upon mining with the " + toolName + ".")
                .defineEnum((prefix + ".timberMode"), defaultTimberMode, EnumGetMethod.NAME_IGNORECASE),
            builder
                .comment("""
                    Determines the behaviour of what should happen when the player is sneaking while using the tool:
                    — NONE: This will just behave the same as if the player was not sneaking.
                    — VANILLA: This will behave as if you are using the vanilla pendant. (meaning, just one block will be mined)
                    — WEAK: This will only break surrounding blocks that exactly match the actively mined block, ignoring groups and break times.""")
                .defineEnum((prefix + ".sneakMode"), defaultSneakMode, EnumGetMethod.NAME_IGNORECASE),
            builder
                .comment("""
                    Specifies the damage the tool is taking upon destroying a particular area:
                    — SINGLE: Only the block that has been destroyed will account for the tool's damage
                    — ALL: All blocks that have been mined will account for the tool's damage""")
                .defineEnum((prefix + ".damageMultiplier"), defaultDamageType, EnumGetMethod.NAME_IGNORECASE)
        );
    }
    
    //==================================================================================================================
    @Override
    public MultiMining createComponent()
    {
        final ToolPredicate predicate = new ToolPredicate(
            ToolPredicate.IncludeList.ofPredicates(this.includes.get().stream()
                .map(CteerMultiMiningServerConfig::resolveBlockPredicate)),
            ConfigHelper.resolveIDs(this.excludes.get().stream().map(obj -> RegistryId.parse(Registries.BLOCK, obj))),
            ConfigHelper.resolveIDs(this.ignored .get().stream().map(obj -> RegistryId.parse(Registries.BLOCK, obj))),
            this.sneakMode.get(),
            this.checkHardness.getAsBoolean()
        );
        return new MultiMining(new ToolProvider(predicate), this.timberMode.get().getHolder(),
                               this.damageMultiplier.get());
    }
}
