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
package xyz.lumian.constructeer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;



//**********************************************************************************************************************
public enum ModLang
    implements Component
{
    // Tooltips
    TOOLBELT_TOOLTIP_POUCH_COUNT(
        "item.%s.toolbelt.tooltip.pouch_count",
        "Upgrades: %s"),
    TOOLBELT_TOOLTIP_TOOLS(
        "item.%s.toolbelt.tooltip.tools",
        "Tools: %s"),
    POUCH_TOOLTIP_CONTENT(
        "item.%s.pouch.tooltip.content",
        "Content: %s"),
    POUCH_TOOLTIP_CONTENT_EMPTY(
        "item.%s.pouch.tooltip.content.empty",
        "Empty"),
    
    // Toolbelt
    KEYBIND_OPEN_TOOLBELT_CONFIG(
        "key.%s.toolbelt.open_config",
        (() -> "Open Toolbelt Configuration")),
    KEYBIND_SHOW_TOOLBELT_WHEEL(
        "key.%s.toolbelt.show_wheel",
        (() -> "Show Toolbelt Wheel")),
    
    // Tabs
    CREATIVE_TAB_TOOLS(
        "itemGroup.%s.tools",
        (() -> (ModDefine.MOD_NAME + " Tools"))),
    
    // Config Screen
    CONFIG_SCREEN_CATEGORY_RENDERING(
        "config.screen.%s.category.rendering",
        (() -> "Rendering")),
    CONFIG_SCREEN_CATEGORY_BEHAVIOUR(
        "config.screen.%s.category.behaviour",
        (() -> "Behaviour")),
    
    CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE(
        "config.screen.%s.option.pouchContentRenderMode",
        (() -> "Pouch GUI render mode")),
    CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL(
        "config.screen.%s.option.renderToolbeltModel",
        (() -> "Toolbelt model render mode")),
    CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER(
        "config.screen.%s.option.shouldRenderHammerOutline",
        (() -> "Render hammer tool outline")),
    CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR(
        "config.screen.%s.option.hammerOutlineColour",
        (() -> "Hammer outline colour")),
    CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER(
        "config.screen.%s.option.shouldRenderPlowOutline",
        (() -> "Render plow tool outline")),
    CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR(
        "config.screen.%s.option.plowOutlineColour",
        (() -> "Plow outline colour")),
    CONFIG_SCREEN_OPTION_ALLOWED_POUCH_TOOLS(
        "config.screen.%s.option.allowedPouchTools",
        (() -> "Allowed pouch tools")),
    
    CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE_TOOLTIP("config.screen.%s.option.pouchContentRenderMode.tooltip"),
    CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL_TOOLTIP    ("config.screen.%s.option.renderToolbeltModel.tooltip"),
    CONFIG_SCREEN_OPTION_ALLOWED_POUCH_TOOLS_TOOLTIP      ("config.screen.%s.option.allowedPouchTools.tooltip"),
    CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER_TOOLTIP     ("config.screen.%s.option.shouldRenderHammerOutline.tooltip"),
    CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR_TOOLTIP    ("config.screen.%s.option.hammerOutlineColour.tooltip"),
    CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER_TOOLTIP       ("config.screen.%s.option.shouldRenderPlowOutline.tooltip"),
    CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR_TOOLTIP      ("config.screen.%s.option.plowOutlineColour.tooltip"),
    
    TOOLBELT_WHEEL_SCREEN_EMPTY_POUCH(
        "screen.%s.toolbelt.wheel.empty_pouch",
        (() -> "Empty Pouch")),
    TOOLBELT_WHEEL_SCREEN_ACTIONBAR_HAND_IS_FULL(
        "screen.%s.toolbelt.wheel.full_hand",
        (() -> "Hand is too full")),
    TOOLBELT_WHEEL_SCREEN_NOT_A_VALID_TOOL(
        "screen.%s.toolbelt.wheel.not_valid_tool",
        (() -> "Item in hand is not a valid tool")),
    TOOLBELT_WHEEL_SCREEN_NO_TOOLBELT_FOUND(
        "screen.%s.toolbelt.wheel.no_equipped_toolbelt",
        (() -> "No toolbelt is currently equipped")),
    
    MULTI_MINING_STRUCTURE_TOO_BIG(
        "multi_mining.too_big",
        "Structure too big")
    ;
    
    //******************************************************************************************************************
    public final Component        component;
    public final Supplier<String> englishDefault;
    public final boolean          shouldGenerate;
    
    //******************************************************************************************************************
    ModLang(final String key, final Supplier<String> englishDefault)
    {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER)
        {
            this.component = Component.translatableWithFallback(ModDefine.formatId(key), englishDefault.get());
        }
        else
        {
            this.component = Component.translatable(ModDefine.formatId(key));
        }
        
        this.englishDefault = Suppliers.memoize(englishDefault);
        this.shouldGenerate = true;
    }
    
    ModLang(final String key, String englishDefault)
    {
        this.component      = Component.translatableWithFallback(ModDefine.formatId(key), englishDefault);
        this.englishDefault = Suppliers.memoize(() -> englishDefault);
        this.shouldGenerate = true;
    }
    
    ModLang(final String key)
    {
        this.component      = Component.translatable(key);
        this.englishDefault = (() -> "");
        this.shouldGenerate = false;
    }

    //==================================================================================================================
    @Override public Style                 getStyle()           { return this.component.getStyle();           }
    @Override public ComponentContents     getContents()        { return this.component.getContents();        }
    @Override public List<Component>       getSiblings()        { return this.component.getSiblings();        }
    @Override public FormattedCharSequence getVisualOrderText() { return this.component.getVisualOrderText(); }
    
    /// {@return the translation key of this translatable}
    public String getKey() { return ((TranslatableContents) this.component.getContents()).getKey(); }
    
    //==================================================================================================================
    /// Gets this translatable with the given arguments.
    /// @param args The translation arguments
    /// @return The new translatable
    public MutableComponent withArgs(final Object ...args)
    {
        return Component.translatableWithFallback(this.getKey(), this.englishDefault.get(), args);
    }
}
