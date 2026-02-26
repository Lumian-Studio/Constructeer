package xyz.lumian.constructeer.registry;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.Constructeer;

import java.util.function.Consumer;



//**********************************************************************************************************************
@SuppressWarnings("UnstableApiUsage")
public class CteerAttachmentRegistry
{
    //******************************************************************************************************************
    public static <T> AttachmentType<T> register(final Identifier id)
    {
        return CteerAttachmentRegistry.register(id, (b -> {}));
    }
    
    public static <T> AttachmentType<T> register(final Identifier                              id,
                                                 final Consumer<AttachmentRegistry.Builder<T>> builder)
    {
        final AttachmentType<T> type = AttachmentRegistry.create(id, builder);
        Constructeer.sendGlobalBootstrapReport("registered data attachment '%s'", id);
        return type;
    }
}
