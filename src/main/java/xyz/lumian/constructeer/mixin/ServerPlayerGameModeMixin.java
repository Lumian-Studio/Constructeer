package xyz.lumian.constructeer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.entity.player.PlayerAttachments;



//**********************************************************************************************************************
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin
{
    //******************************************************************************************************************
    @Shadow @Final protected ServerPlayer player;
    
    //******************************************************************************************************************
    @SuppressWarnings("UnstableApiUsage")
    @Inject(
        method = "handleBlockBreakAction",
        at     = @At("HEAD")
    )
    public void updateLastBreakFaceDirection(final BlockPos pos, final ServerboundPlayerActionPacket.Action action,
                                             final Direction face, final int maxBuildHeight, final int sequence,
                                             final CallbackInfo ci)
    {
        this.player.setAttached(PlayerAttachments.PLAYER_MULTI_MINING_BLOCK_FACE, face);
    }
}
