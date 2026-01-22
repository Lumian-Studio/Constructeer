package xyz.lumian.constructeer.entity.player;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Direction;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
@SuppressWarnings("UnstableApiUsage")
public final class PlayerAttachments
{
    //******************************************************************************************************************
    public static final AttachmentType<Direction> PLAYER_MULTI_MINING_BLOCK_FACE = AttachmentRegistry
        .create(ModDefine.id("player_multi_mining_block_face"));
    
    //******************************************************************************************************************
    private PlayerAttachments() {}
}
