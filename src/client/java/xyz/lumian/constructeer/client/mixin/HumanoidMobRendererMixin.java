package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.client.impl.IHumanoidRenderStateExtension;
import xyz.lumian.constructeer.item.ModItems;



//**********************************************************************************************************************
@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V",
        at     = @At("TAIL"))
    public void extractRenderState(final Mob mob, final HumanoidRenderState humanoidRenderState, final float f,
                                   final CallbackInfo ci)
    {
        ((IHumanoidRenderStateExtension) humanoidRenderState).constructeer$setFallDistance(mob.fallDistance);
        final ItemStack equipment = humanoidRenderState.legsEquipment;
        
        if (equipment.is(ModItems.TOOLBELT))
        {
            ((IHumanoidRenderStateExtension) humanoidRenderState).constructeer$setToolbeltEquipment(equipment);
        }
    }
}
