/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.toolbelt.client.mixin;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.toolbelt.client.renderer.state.CteerToolbeltRenderDataKeys;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;



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
        state.setData(CteerToolbeltRenderDataKeys.LIVING_FALL_DISTANCE, mob.fallDistance);
        final ItemStack equipment = state.legsEquipment;
        
        if (equipment.is(CteerToolbeltItems.TOOLBELT))
        {
            state.setData(CteerToolbeltRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT, equipment);
        }
    }
}
