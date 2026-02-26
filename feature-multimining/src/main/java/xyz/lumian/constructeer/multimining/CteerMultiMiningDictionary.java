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
package xyz.lumian.constructeer.multimining;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.data.IDictionary;



//**********************************************************************************************************************
public enum CteerMultiMiningDictionary
    implements IDictionary
{
    // Tabs
    CREATIVE_TAB_TOOLS(
        "itemGroup.%s.tools",
        (CteerDefine.MOD_NAME + " Tools")),
    
    // Config Screen
    CONFIG_SCREEN_CATEGORY_RENDERING(
        "config.screen.%s.category.rendering",
        "Rendering"),
    CONFIG_SCREEN_CATEGORY_BEHAVIOUR(
        "config.screen.%s.category.behaviour",
        "Behaviour"),
    
    CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER(
        "config.screen.%s.option.shouldRenderHammerOutline",
        "Render hammer tool outline"),
    CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR(
        "config.screen.%s.option.hammerOutlineColour",
        "Hammer outline colour"),
    CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER(
        "config.screen.%s.option.shouldRenderPlowOutline",
        "Render plow tool outline"),
    CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR(
        "config.screen.%s.option.plowOutlineColour",
        "Plow outline colour"),
    CONFIG_SCREEN_OPTION_SAW_SHOULD_RENDER(
        "config.screen.%s.option.shouldRenderSawOutline",
        "Render saw tool outline"),
    CONFIG_SCREEN_OPTION_SAW_OUTLINE_COLOUR(
        "config.screen.%s.option.sawOutlineColour",
        "Saw outline colour"),
    
    CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER_TOOLTIP ("config.screen.%s.option.shouldRenderHammerOutline.tooltip"),
    CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR_TOOLTIP("config.screen.%s.option.hammerOutlineColour.tooltip"),
    CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER_TOOLTIP   ("config.screen.%s.option.shouldRenderPlowOutline.tooltip"),
    CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR_TOOLTIP  ("config.screen.%s.option.plowOutlineColour.tooltip"),
    CONFIG_SCREEN_OPTION_SAW_SHOULD_RENDER_TOOLTIP    ("config.screen.%s.option.shouldRenderSawOutline.tooltip"),
    CONFIG_SCREEN_OPTION_SAW_OUTLINE_COLOUR_TOOLTIP   ("config.screen.%s.option.sawOutlineColour.tooltip"),
    
    MULTI_MINING_STRUCTURE_TOO_BIG(
        "multi_mining.too_big",
        "Structure too big"),
    ;
    
    //******************************************************************************************************************
    private final TranslatableContents translatable;
    
    @Nullable
    private final String enUsDefault;
    
    //******************************************************************************************************************
    CteerMultiMiningDictionary(final String key, final @Nullable String englishDefault)
    {
        this.translatable = IDictionary.forKey(CteerDefine.formatId(key));
        this.enUsDefault  = englishDefault;
    }
    
    CteerMultiMiningDictionary(final String key) { this(key, null); }
    
    //==================================================================================================================
    @Override public TranslatableContents translatable() { return this.translatable; }
    @Override public @Nullable String     defaultEnUs()  { return this.enUsDefault; }
}
