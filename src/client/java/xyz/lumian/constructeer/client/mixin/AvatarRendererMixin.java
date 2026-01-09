package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.client.impl.IHumanoidRenderStateExtension;
import xyz.lumian.constructeer.item.ModItems;



//**********************************************************************************************************************
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
        at     = @At("TAIL"))
    public void extractRenderState(final Avatar avatar, final AvatarRenderState avatarRenderState,
                                   final float f, final CallbackInfo ci)
    {
        ((IHumanoidRenderStateExtension) avatarRenderState).constructeer$setFallDistance(avatar.fallDistance);
        final ItemStack equipment = avatarRenderState.legsEquipment;
        
        if (equipment.is(ModItems.TOOLBELT))
        {
            ((IHumanoidRenderStateExtension) avatarRenderState).constructeer$setToolbeltEquipment(equipment);
        }
    }
}
