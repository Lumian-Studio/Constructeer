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
    /// After how many ticks should the multi mining outline renderer clear the rendering cache.
    public static final int MULTI_MINING_OUTLINE_RENDERER_UPDATE_TICKS = 5;
    
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
