package xyz.lumian.constructeer.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModSoundEvents
{
    //******************************************************************************************************************
    public static final SoundEvent TREE_FALLING = registerVariable("entity.falling_object.tree");
    public static final SoundEvent SAWING       = registerVariable("entity.player.sawing");
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static SoundEvent registerVariable(final String name)
    {
        final Identifier id = ModDefine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
    
    private static SoundEvent registerFixed(final String name, final float range)
    {
        final Identifier id = ModDefine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }
    
    //******************************************************************************************************************
    private ModSoundEvents() {}
}
