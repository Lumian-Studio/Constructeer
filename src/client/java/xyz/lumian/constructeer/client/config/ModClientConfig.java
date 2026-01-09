package xyz.lumian.constructeer.client.config;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;



//**********************************************************************************************************************
public record ModClientConfig(
    ConfigValue<PouchContentRenderMode> pouchContentRenderMode,
    ConfigValue<ToolbeltRenderMode>     renderToolbeltModel
)
{
    //******************************************************************************************************************
    public enum ToolbeltRenderMode
        implements Predicate<HumanoidRenderState>
    {
        ALWAYS,
        IF_NO_CHESTPLATE,
        NEVER,
        ;
        
        //**************************************************************************************************************
        @Override
        public boolean test(final HumanoidRenderState state)
        {
            return switch (this)
            {
                case ALWAYS           -> true;
                case IF_NO_CHESTPLATE -> !state.chestEquipment.is(ItemTags.CHEST_ARMOR);
                case NEVER            -> false;
            };
        }
    }
    
    public enum PouchContentRenderMode
        implements Predicate<BooleanSupplier>
    {
        ALWAYS,
        IN_POUCH,
        NEVER,
        ;
        
        //**************************************************************************************************************
        @Override
        public boolean test(final BooleanSupplier inPouchSlotTest)
        {
            return switch (this)
            {
                case ALWAYS   -> true;
                case IN_POUCH -> inPouchSlotTest.getAsBoolean();
                case NEVER    -> false;
            };
        }
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
                .defineEnum("item.toolbelt.renderModel", ToolbeltRenderMode.IF_NO_CHESTPLATE)
        );
    }
}
