package xyz.lumian.constructeer.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BooleanSupplier;



//**********************************************************************************************************************
public record ModClientConfig(
    ConfigValue<PouchContentRenderMode> pouchContentRenderMode,
    BooleanValue                        renderToolbeltModel
)
{
    //******************************************************************************************************************
    public enum PouchContentRenderMode
    {
        ALWAYS,
        IN_POUCH,
        NEVER,
        ;
        
        //**************************************************************************************************************
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
        final Pair<ModClientConfig, ModConfigSpec> result = (new Builder())
            .configure(ModClientConfig::new);
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
                .comment("Whether the toolbelt model should be rendered on the player")
                .define("item.toolbelt.renderModel", true)
        );
    }
}
