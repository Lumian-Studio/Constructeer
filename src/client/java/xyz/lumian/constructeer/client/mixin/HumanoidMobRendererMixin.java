package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.client.renderer.state.ModRenderDataKeys;
import xyz.lumian.constructeer.item.ModItems;



//**********************************************************************************************************************
@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V",
        at     = @At("TAIL"))
    public void extractRenderState(final Mob mob, final HumanoidRenderState state, final float f, final CallbackInfo ci)
    {
        state.setData(ModRenderDataKeys.LIVING_FALL_DISTANCE, mob.fallDistance);
        final ItemStack equipment = state.legsEquipment;
        
        if (equipment.is(ModItems.TOOLBELT))
        {
            state.setData(ModRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT, equipment);
        }
    }
}
