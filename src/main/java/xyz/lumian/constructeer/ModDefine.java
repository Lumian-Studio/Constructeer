package xyz.lumian.constructeer;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//**********************************************************************************************************************
/// Declares constants that may or may not be used for the MOD.
public class ModDefine
{
    //******************************************************************************************************************
    /// The id of the mod, usually used for [Identifier] objects.
    public static final String MOD_ID = ModAutoDefine.ID;
    
    /// The name of the mod.
    public static final String MOD_NAME = ModAutoDefine.NAME;
    
    /// The current version of the mod.
    public static final String MOD_VERSION = ModAutoDefine.VERSION;
    
    /// The current version of Minecraft this mod works on.
    public static final String MINECRAFT_VERSION = ModAutoDefine.MC_VERSION;
    
    //==================================================================================================================
    /// The main logging instance for this mod.
    public static final Logger LOGGER = LoggerFactory.getLogger(ModDefine.MOD_ID);
    
    //******************************************************************************************************************
    /// Creates a new [Identifier] for this mod.
    /// @param path The path of the id
    /// @return The mod namespaced [Identifier]
    public static Identifier id(final String path) { return Identifier.fromNamespaceAndPath(ModDefine.MOD_ID, path); }
    
    public static String formatId(final String pattern) { return pattern.formatted(ModDefine.MOD_ID); }
}
