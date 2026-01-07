package xyz.lumian.constructeer.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;
import xyz.lumian.constructeer.tag.ModItemTags;
import xyz.lumian.constructeer.util.RegistryId;

import java.util.List;



//**********************************************************************************************************************
public record ModServerConfig(
    ConfigValue<List<String>> pouchAllowedTools
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
                .comment(
                    "Declare allowed item IDs that can go in the Constructeer pouch item (tags start with a #)\n"
                    + "This is a reloadable property")
                .define(
                    "item.pouch.validTools",
                    List.of("#" + ModItemTags.COMMON_TOOLS.location()),
                    (element -> (
                        element instanceof List<?> list
                        && list.stream().allMatch(elm -> (
                            elm instanceof String str
                            && RegistryId.isValidRegistryId(str)
                        ))
                    )))
        );
    }
}
