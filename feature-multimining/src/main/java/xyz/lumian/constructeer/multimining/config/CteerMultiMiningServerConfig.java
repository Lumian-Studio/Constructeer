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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.config.ReloadableConfig;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.multimining.item.multimining.damage.DamageType;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.ToolPredicate;
import xyz.lumian.constructeer.multimining.item.multimining.timber.TimberMode;
import xyz.lumian.constructeer.registry.RegistryId;

import java.util.Collections;
import java.util.List;



//**********************************************************************************************************************
public final class CteerMultiMiningServerConfig
    extends ReloadableConfig<CteerMultiMiningServerConfig>
{
    //******************************************************************************************************************
    public static final ModConfigSpec                SPEC;
    public static final CteerMultiMiningServerConfig INSTANCE;
    
    //==================================================================================================================
    static
    {
        final var result = CteerConfigManager.buildConfig(CteerMultiMiningServerConfig::new);
        SPEC     = result.getValue();
        INSTANCE = result.getKey();
    }
    
    //******************************************************************************************************************
    public static ToolPredicate.BlockPredicate resolveBlockPredicate(final Object object)
    {
        return new ToolPredicate.BlockPredicate(switch (object)
        {
            case List<?> list -> Either.right(list.stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, (String) str))
                .collect(ImmutableList.toImmutableList()));
            case String  str  -> Either.left(RegistryId.parse(Registries.BLOCK, str));
            
            default -> throw new IllegalArgumentException("object is neither a registry id nor a list of registry IDs");
        });
    }
    
    public static boolean validateBlockPredicate(final Object object)
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
    
    //******************************************************************************************************************
    public final IntValue   multiMiningHardLimit;
    public final ToolConfig hammer;
    public final ToolConfig plow;
    public final SawConfig  saw;
    
    //******************************************************************************************************************
    private CteerMultiMiningServerConfig(final ModConfigSpec.Builder builder)
    {
        this.multiMiningHardLimit = builder
            .comment("Declares the absolute maximum that multi mining tools can destroy, any multi mining action exceeding this limit will be cancelled.")
            .worldRestart()
            .defineInRange("multiMining.hardLimit", 1000, 0, Integer.MAX_VALUE);
        this.hammer = new ToolConfig(
            builder, "hammer", true,
            Collections.singletonList("#" + BlockTags.BASE_STONE_OVERWORLD.location()),
            List.of(), List.of(), TimberMode.INSTANT, SneakMode.NONE, DamageType.SINGLE);
        this.plow = new ToolConfig(
            builder, "plow", true,
            List.of(("#" + BlockTags.SNOW.location()), ("#" + BlockTags.DIRT.location())),
            List.of(), List.of(), TimberMode.INSTANT, SneakMode.NONE, DamageType.SINGLE);
        this.saw = new SawConfig(builder);
    }
    
    //==================================================================================================================
    @Override public ModConfigSpec spec() { return CteerMultiMiningServerConfig.SPEC; }
}
