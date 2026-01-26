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

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.List;



//**********************************************************************************************************************
public record ModServerConfig(
    ConfigValue<List<String>> pouchAllowedTools,
    IntValue                  multiMiningHardLimit,
    ToolConfig                hammer,
    ToolConfig                plow,
    SawConfig                 saw
)
{
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
    public ModServerConfig(final ModConfigSpec.Builder builder)
    {
        this(
            builder
                .comment("Declare allowed item IDs that can go in the Constructeer pouch item (tags start with a #).")
                .define(
                    "toolbelt.pouch.validTools",
                    (() -> List.of("#" + ModItemTags.COMMON_TOOLS.location())),
                    ConfigHelper::validateListOfRegistryIds),
            
            builder
                .comment("Declares the absolute maximum that multi mining tools can destroy, any multi mining action exceeding this limit will be cancelled.")
                .worldRestart()
                .defineInRange("multiMining.hardLimit", 1000, 0, Integer.MAX_VALUE),
            
            new ToolConfig("hammer", builder),
            new ToolConfig("plow",   builder),
            new SawConfig(builder)
        );
    }
}
