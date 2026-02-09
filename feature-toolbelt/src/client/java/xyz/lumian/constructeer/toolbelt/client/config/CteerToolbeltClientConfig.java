package xyz.lumian.constructeer.toolbelt.client.config;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.toolbelt.container.ToolbeltMenu;

import java.util.function.Predicate;



//**********************************************************************************************************************
public final class CteerToolbeltClientConfig
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
        ALWAYS   { public boolean test(Slot slot) { return true;                                     }},
        IN_POUCH { public boolean test(Slot slot) { return (slot instanceof ToolbeltMenu.PouchSlot); }},
        NEVER    { public boolean test(Slot slot) { return false;                                    }},
    }
    
    //******************************************************************************************************************
    public static final ModConfigSpec             SPEC;
    public static final CteerToolbeltClientConfig INSTANCE;
    
    //==================================================================================================================
    static
    {
        final var result = CteerConfigManager.buildConfig(CteerToolbeltClientConfig::new);
        SPEC     = result.getValue();
        INSTANCE = result.getKey();
    }
    
    //******************************************************************************************************************
    public final ConfigValue<PouchContentRenderMode> pouchContentRenderMode;
    public final ConfigValue<ToolbeltRenderMode>     renderToolbeltModel;
    
    //******************************************************************************************************************
    private CteerToolbeltClientConfig(final ModConfigSpec.Builder builder)
    {
        this.pouchContentRenderMode = builder
            .comment("Determines if and when the pouch content should be rendered on top of the pouch")
            .defineEnum("item.pouch.pouchContentRenderMode", PouchContentRenderMode.IN_POUCH);
        this.renderToolbeltModel    = builder
            .comment("Determines if and when the toolbelt model should be renderer on the player model")
            .defineEnum("item.toolbelt.renderModel", ToolbeltRenderMode.IF_NO_CHESTPLATE);
    }
}
