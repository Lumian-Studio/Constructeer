package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;



//**********************************************************************************************************************
public class CteerSoundEventRegistry
{
    //******************************************************************************************************************
    public static SoundEvent registerVariable(final Identifier id)
    {
        return CteerRegistries.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
    
    public static SoundEvent registerFixed(final Identifier id, final float range)
    {
        return CteerRegistries.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }
}
