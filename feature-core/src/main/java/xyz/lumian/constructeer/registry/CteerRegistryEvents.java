package xyz.lumian.constructeer.registry;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;



//**********************************************************************************************************************
public class CteerRegistryEvents
{
    //******************************************************************************************************************
    /// Invoked right before Minecraft's registries are to be frozen.
    public static final Event<Runnable> MC_REGISTRIES_FROZEN_BEFORE = EventFactory.createArrayBacked(Runnable.class,
        (listeners) -> () -> Arrays.stream(listeners).forEach(Runnable::run));
    
    /// Invoked right after Minecraft's registries had been frozen.
    public static final Event<Runnable> MC_REGISTRIES_FROZEN_AFTER = EventFactory.createArrayBacked(Runnable.class,
        (listeners) -> () -> Arrays.stream(listeners).forEach(Runnable::run));
}
