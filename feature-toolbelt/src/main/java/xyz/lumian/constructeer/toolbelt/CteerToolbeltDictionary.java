package xyz.lumian.constructeer.toolbelt;


import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.data.IDictionary;



//**********************************************************************************************************************
public enum CteerToolbeltDictionary
    implements IDictionary
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
        "Open Toolbelt Configuration"),
    KEYBIND_SHOW_TOOLBELT_WHEEL(
        "key.%s.toolbelt.show_wheel",
        "Show Toolbelt Wheel"),
    
    // Config Screen
    CONFIG_SCREEN_CATEGORY_RENDERING(
        "config.screen.%s.category.rendering",
        "Rendering"),
    CONFIG_SCREEN_CATEGORY_BEHAVIOUR(
        "config.screen.%s.category.behaviour",
        "Behaviour"),
    
    CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE(
        "config.screen.%s.option.pouchContentRenderMode",
        "Pouch GUI render mode"),
    CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL(
        "config.screen.%s.option.renderToolbeltModel",
        "Toolbelt model render mode"),
    
    CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE_TOOLTIP("config.screen.%s.option.pouchContentRenderMode.tooltip"),
    CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL_TOOLTIP    ("config.screen.%s.option.renderToolbeltModel.tooltip"),
    
    TOOLBELT_WHEEL_SCREEN_EMPTY_POUCH(
        "screen.%s.toolbelt.wheel.empty_pouch",
        "Empty Pouch"),
    TOOLBELT_WHEEL_SCREEN_ACTIONBAR_HAND_IS_FULL(
        "screen.%s.toolbelt.wheel.full_hand",
        "Hand is too full"),
    TOOLBELT_WHEEL_SCREEN_NOT_A_VALID_TOOL(
        "screen.%s.toolbelt.wheel.not_valid_tool",
        "Item in hand is not a valid tool"),
    TOOLBELT_WHEEL_SCREEN_NO_TOOLBELT_FOUND(
        "screen.%s.toolbelt.wheel.no_equipped_toolbelt",
        "No toolbelt is currently equipped"),
    ;
    //******************************************************************************************************************
    private final TranslatableContents translatable;
    
    @Nullable
    private final String enUsDefault;
    
    //******************************************************************************************************************
    CteerToolbeltDictionary(final String key, final @Nullable String englishDefault)
    {
        this.translatable = IDictionary.forKey(CteerDefine.formatId(key));
        this.enUsDefault  = englishDefault;
    }
    
    CteerToolbeltDictionary(final String key) { this(key, null); }
    
    //==================================================================================================================
    @Override public TranslatableContents translatable() { return this.translatable; }
    @Override public @Nullable String     defaultEnUs()  { return this.enUsDefault; }
}
