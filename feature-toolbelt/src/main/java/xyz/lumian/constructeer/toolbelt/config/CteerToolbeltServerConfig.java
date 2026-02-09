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
package xyz.lumian.constructeer.toolbelt.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import xyz.lumian.constructeer.config.ConfigHelper;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.config.ReloadableConfig;
import xyz.lumian.constructeer.registry.CteerTagRegistry;

import java.util.List;



//**********************************************************************************************************************
public final class CteerToolbeltServerConfig
    extends ReloadableConfig<CteerToolbeltServerConfig>
{
    //******************************************************************************************************************
    public static final ModConfigSpec             SPEC;
    public static final CteerToolbeltServerConfig INSTANCE;
    
    //==================================================================================================================
    static
    {
        final var result = CteerConfigManager.buildConfig(CteerToolbeltServerConfig::new);
        SPEC     = result.getValue();
        INSTANCE = result.getKey();
    }
    
    //******************************************************************************************************************
    public final ConfigValue<List<String>> allowedPouchTools;
    
    //******************************************************************************************************************
    private CteerToolbeltServerConfig(final Builder builder)
    {
        this.allowedPouchTools = builder
            .comment("Declare allowed item IDs that can go in the Constructeer pouch item (tags start with a #).")
            .define(
                "toolbelt.pouch.validTools",
                (() -> List.of("#" + CteerTagRegistry.COMMON_TOOLS.location())),
                ConfigHelper::validateListOfRegistryIds);
    }
    
    //==================================================================================================================
    @Override public ModConfigSpec spec() { return CteerToolbeltServerConfig.SPEC; }
}
