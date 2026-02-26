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
package xyz.lumian.constructeer.gauntlet.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.IPortableRenderer;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.PortableRenderExtension;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.gauntlet.item.portable.Portable;
import xyz.lumian.constructeer.gauntlet.player.CteerGauntletPlayerAttachments;



//**********************************************************************************************************************
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    //******************************************************************************************************************
    @Unique
    private static <T, T2> void render(final Portable<T> portable, final IPortableRenderer<T2> renderer,
                                       final AbstractClientPlayer player, final float partialTick, final float pitch,
                                       final InteractionHand hand, float swingProgress, final ItemStack item,
                                       final float equippedProgress, final PoseStack poseStack,
                                       final SubmitNodeCollector nodeCollector, final int packedLight)
    {
        //noinspection unchecked
        renderer.renderFirstPerson((T2) portable.object(), player, partialTick, pitch, hand, swingProgress, item,
                                   equippedProgress, poseStack, nodeCollector, packedLight);
    }
    
    //******************************************************************************************************************
    @Shadow @Final private Minecraft minecraft;
    @Shadow        private float     mainHandHeight;
    @Shadow        private ItemStack mainHandItem;
    
    @Unique private @Nullable Portable<?> portable;
    
    //******************************************************************************************************************
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight,
                                            float equippedProgress, float swingProgress, HumanoidArm arm);
    
    //==================================================================================================================
    @ModifyVariable(
        method = "renderHandsWithItems",
        at     = @At("STORE")
    )
    private ItemInHandRenderer.HandRenderSelection handRenderSelection(
        final ItemInHandRenderer.HandRenderSelection value,
        final float partialTick,
        final PoseStack poseStack,
        final SubmitNodeCollector nodeCollector,
        final LocalPlayer player,
        final int packedLight
    )
    {
        //noinspection UnstableApiUsage
        this.portable = player.getAttached(CteerGauntletPlayerAttachments.PORTABLE);
        return (this.portable != null ? ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS : value);
    }
    
    @Inject(
        method      = "renderArmWithItem",
        at          = @At("HEAD"),
        cancellable = true
    )
    private void renderArmWithItemIfCarrying(final AbstractClientPlayer player, final float partialTick,
                                             final float pitch, final InteractionHand hand, float swingProgress,
                                             final ItemStack item, final float equippedProgress,
                                             final PoseStack poseStack, final SubmitNodeCollector nodeCollector,
                                             final int packedLight, final CallbackInfo ci)
    {
        if (this.portable != null)
        {
            final IPortableRenderer<?> renderer = ((PortableRenderExtension<?>) this.portable.type()).getRenderer();
            
            if (renderer != null)
            {
                ci.cancel();
                
                final HumanoidArm humanoidArm = (hand == InteractionHand.MAIN_HAND
                    ? player.getMainArm()
                    : player.getMainArm().getOpposite());
                swingProgress = player.getAttackAnim(partialTick);
                
                poseStack.pushPose();
                {
                    ItemInHandRendererMixin.render(this.portable, renderer, player, partialTick, pitch, hand,
                                                   swingProgress, item, equippedProgress, poseStack, nodeCollector,
                                                   packedLight);
                    this.renderPlayerArm(poseStack, nodeCollector, packedLight, equippedProgress,
                                         (swingProgress * 0.01f), humanoidArm);
                }
                poseStack.popPose();
                
                return;
            }
        }
        
        if (!player.isScoping() && hand == InteractionHand.MAIN_HAND && item.is(CteerGauntletItems.GAUNTLET_OF_POWER))
        {
            ci.cancel();
            
            poseStack.pushPose();
            {
                this.renderPlayerArm(poseStack, nodeCollector, packedLight, equippedProgress, swingProgress,
                                     player.getMainArm());
            }
            poseStack.popPose();
        }
    }
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void resetRightArmImmediately(final CallbackInfo ci)
    {
        final LocalPlayer player = this.minecraft.player;
        assert (player != null);
        
        if (!player.isHandsBusy() && this.portable != null)
        {
			final float g = (this.mainHandItem != player.getMainHandItem() ? 0.0F : 1.0F);
			this.mainHandHeight = (this.mainHandHeight + Mth.clamp((g - this.mainHandHeight), -0.4F, 0.4F));
        }
    }
}
