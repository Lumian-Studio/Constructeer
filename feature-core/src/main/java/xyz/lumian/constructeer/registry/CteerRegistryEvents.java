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
package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;



//**********************************************************************************************************************
public class CteerRegistryEvents
{
    //******************************************************************************************************************
    /// Invoked right before Minecraft's registries are to be frozen.
    /// When this event is invoked all Minecraft content is registered and all mods are done loading, this is useful
    /// to defer custom registration stuff (not talking about Minecraft's [net.minecraft.core.Registry]).
    public static final Event<Runnable> MC_REGISTRIES_FROZEN_BEFORE = EventFactory.createArrayBacked(Runnable.class,
        (listeners) -> () -> Arrays.stream(listeners).forEach(Runnable::run));
    
    /// Invoked right after Minecraft's registries had been frozen.
    /// This is useful for doing custom finishing touches, at this point all registries should have successfully been
    /// populated. (also for custom registration stuff if [#MC_REGISTRIES_FROZEN_BEFORE] was used to register your own
    /// things)
    public static final Event<Runnable> MC_REGISTRIES_FROZEN_AFTER = EventFactory.createArrayBacked(Runnable.class,
        (listeners) -> () -> Arrays.stream(listeners).forEach(Runnable::run));
}
