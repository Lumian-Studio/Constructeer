package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lumian.constructeer.entity.player.PlayerAttachments;

import java.util.Objects;



//**********************************************************************************************************************
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin
{
    //******************************************************************************************************************
    @SuppressWarnings("UnstableApiUsage")
    @Inject(
        method = { "method_41935", "method_41932" },
        at     = @At("HEAD"))
    public void updateLastBreakFaceDirection(final BlockPos blockPos, final Direction direction, final int i,
                                             final CallbackInfoReturnable<Packet<?>> cir)
    {
        Objects
            .requireNonNull(Minecraft.getInstance().player)
            .setAttached(PlayerAttachments.PLAYER_MULTI_MINING_BLOCK_FACE, direction);
    }
}
