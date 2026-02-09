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
package xyz.lumian.constructeer.multimining.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import xyz.lumian.constructeer.config.CteerConfigManager;



//**********************************************************************************************************************
public final class CteerMultiMiningClientConfig
{
    //******************************************************************************************************************
    public static final CteerMultiMiningClientConfig INSTANCE;
    public static final ModConfigSpec                SPEC;
    
    //==================================================================================================================
    static
    {
        final var result = CteerConfigManager.buildConfig(CteerMultiMiningClientConfig::new);
        INSTANCE = result.getKey();
        SPEC     = result.getValue();
    }
    
    //******************************************************************************************************************
    public final BooleanValue shouldRenderHammerOutline;
    public final IntValue     hammerOutlineColour;
    public final BooleanValue shouldRenderPlowOutline;
    public final IntValue     plowOutlineColour;
    public final BooleanValue shouldRenderSawOutline;
    public final IntValue     sawOutlineColour;
    
    //******************************************************************************************************************
    private CteerMultiMiningClientConfig(final Builder builder)
    {
        this.shouldRenderHammerOutline = builder
            .comment("Determines whether the extended block outline should be rendered for any hammer tool")
            .define("item.hammer.renderOutline", true);
        this.hammerOutlineColour = builder
            .comment("Determines the colour used for rendering the extended outline of the hammer tool")
            .defineInRange("item.hammer.outlineColour", 0x40A0E0, 0x000000, 0xFFFFFF);
        this.shouldRenderPlowOutline = builder
            .comment("Determines whether the extended block outline should be rendered for any plow tool")
            .define("item.plow.renderOutline", true);
        this.plowOutlineColour = builder
            .comment("Determines the colour used for rendering the extended outline of the plow tool")
            .defineInRange("item.plow.outlineColour", 0x7289DA, 0x000000, 0xFFFFFF);
        this.shouldRenderSawOutline = builder
            .comment("""
                Determines whether the extended block outline should be rendered for any saw tool
                This is disabled by default as it might be very resource intensive for large trees""")
            .define("item.saw.renderOutline", false);
        this.sawOutlineColour = builder
            .comment("Determines the colour used for rendering the extended outline of the plow tool")
            .defineInRange("item.saw.outlineColour", 0xFFD700, 0x000000, 0xFFFFFF);
        
    }
}
