package xyz.lumian.constructeer.client.config;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;
import xyz.lumian.constructeer.container.ToolbeltMenu;

import java.util.function.Predicate;



//**********************************************************************************************************************
public record ModClientConfig(
    ConfigValue<PouchContentRenderMode> pouchContentRenderMode,
    ConfigValue<ToolbeltRenderMode>     renderToolbeltModel,
    BooleanValue                        shouldRenderHammerOutline,
    IntValue                            hammerOutlineColour,
    BooleanValue                        shouldRenderPlowOutline,
    IntValue                            plowOutlineColour
)
{
    //******************************************************************************************************************
    public enum ToolbeltRenderMode
        implements Predicate<HumanoidRenderState>
    {
        ALWAYS           { public boolean test(HumanoidRenderState state) { return true; }},
        IF_NO_CHESTPLATE { public boolean test(HumanoidRenderState state) {
            return !state.chestEquipment.is(ItemTags.CHEST_ARMOR);
        }},
        NEVER            { public boolean test(HumanoidRenderState state) { return false; }}
    }
    
    public enum PouchContentRenderMode
        implements Predicate<Slot>
    {
        ALWAYS   { public boolean test(Slot slot) { return true; }},
        IN_POUCH { public boolean test(Slot slot) { return (slot instanceof ToolbeltMenu.PouchSlot); }},
        NEVER    { public boolean test(Slot slot) { return false; }},
    }
    
    //******************************************************************************************************************
    public static final ModConfigSpec   SPEC;
    public static final ModClientConfig INSTANCE;
    
    //==================================================================================================================
    static
    {
        final Pair<ModClientConfig, ModConfigSpec> result = (new Builder()).configure(ModClientConfig::new);
        SPEC     = result.getValue();
        INSTANCE = result.getKey();
    }
    
    //******************************************************************************************************************
    public ModClientConfig(final Builder builder)
    {
        this(
            builder
                .comment("Determines if and when the pouch content should be rendered on top of the pouch")
                .defineEnum("item.pouch.pouchContentRenderMode", PouchContentRenderMode.IN_POUCH),
            builder
                .comment("Determines if and when the toolbelt model should be renderer on the player model")
                .defineEnum("item.toolbelt.renderModel", ToolbeltRenderMode.IF_NO_CHESTPLATE),
            builder
                .comment("Determines whether the extended block outline should be rendered for any hammer tool")
                .define("item.hammer.renderOutline", true),
            builder
                .comment("Determines the colour used for rendering the extended outline of the hammer tool")
                .defineInRange("item.hammer.outlineColour", 0, 0x000000, 0xFFFFFF),
            builder
                .comment("Determines whether the extended block outline should be rendered for any plow tool")
                .define("item.plow.renderOutline", true),
            builder
                .comment("Determines the colour used for rendering the extended outline of the plow tool")
                .defineInRange("item.plow.outlineColour", 0, 0x000000, 0xFFFFFF)
        );
    }
}
