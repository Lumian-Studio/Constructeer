package xyz.lumian.constructeer.gauntlet.player;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.gauntlet.item.portable.Portable;
import xyz.lumian.constructeer.registry.CteerAttachmentRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
@SuppressWarnings("UnstableApiUsage")
public final class CteerGauntletPlayerAttachments
    implements IBootstrap
{
    //******************************************************************************************************************
    public static final AttachmentType<Portable<?, ?>> PORTABLE = CteerAttachmentRegistry.register(
        CteerDefine.id("portable"),
        (b -> b
            .persistent(Portable.CODEC)
            .syncWith(Portable.STREAM_CODEC, AttachmentSyncPredicate.all())));
}
