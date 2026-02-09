package xyz.lumian.constructeer.toolbelt.client.player;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.toolbelt.client.gui.screen.ToolbeltWheelScreen;



//**********************************************************************************************************************
@SuppressWarnings("UnstableApiUsage")
public class CteerClientPlayerAttachments
{
    //******************************************************************************************************************
    public static final AttachmentType<ToolbeltWheelScreen> WHEEL_SCREEN = AttachmentRegistry
        .create(CteerDefine.id("wheel_screen"));
}
