package xyz.lumian.constructeer.gauntlet.item.portable;

import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.gauntlet.registry.CteerGauntletRegistries;
import xyz.lumian.constructeer.registry.CteerRegistries;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public final class CteerPortableTypes
    implements IBootstrap
{
    //******************************************************************************************************************
    /// The [IPortableType] algorithm for handling carrying blocks and block entities.
    public static final IPortableType<BlockPortableType.BlockData> BLOCK
        = register(CteerDefine.id("block"), new BlockPortableType());
    
    public static final IPortableType<EntityPortableType.EntityData> ENTITY
        = register(CteerDefine.id("entity"), new EntityPortableType());
    
    //******************************************************************************************************************
    /// Register a new [IPortableType] for the Constructeer portable system.
    /// @param id   The [Identifier] of the type
    /// @param type The [IPortableType] to register
    public static <T> IPortableType<T> register(final Identifier id, final IPortableType<T> type)
    {
        return CteerRegistries.register(CteerGauntletRegistries.PORTABLE_TYPE, id, type);
    }
}
