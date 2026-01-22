package xyz.lumian.constructeer.stat;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModStats
{
    //******************************************************************************************************************
    public static final Identifier HAMMER_USED = register("hammers_used");
    public static final Identifier PLOW_USED   = register("plows_used");
    public static final Identifier SAW_USED    = register("saws_used");
    
    //******************************************************************************************************************
    public static void initialise() {}

    //==================================================================================================================
    private static Identifier register(final String path)
    {
        final Identifier id = ModDefine.id(path);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
        return id;
    }
    
    //******************************************************************************************************************
    private ModStats() {}
}
