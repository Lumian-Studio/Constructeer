package xyz.lumian.constructeer.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerStatRegistry
{
    //******************************************************************************************************************
    public static Identifier registerCustom(final Identifier id)
    {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
        Constructeer.sendGlobalBootstrapReport("registered custom stat '%s'", id);
        return id;
    }
}
