package xyz.lumian.constructeer.client.registry;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerKeybindRegistry
{
    //******************************************************************************************************************
    public static KeyMapping.Category registerCategory(final Identifier id)
    {
        return Util.make(KeyMapping.Category.register(id), (category -> Constructeer
            .sendGlobalBootstrapReport("registered key mapping category '%s'", id)));
    }
    
    public static KeyMapping registerMapping(final KeyMapping mapping)
    {
        return Util.make(KeyBindingHelper.registerKeyBinding(mapping), (mapping1 -> Constructeer
            .sendGlobalBootstrapReport(
                "registered key mapping '%s' for key '%s'",
                mapping1.getName(),
                mapping1.saveString())));
    }
}
