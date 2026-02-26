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
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.gauntlet.client.renderer.GauntletRenderer;
import xyz.lumian.constructeer.gauntlet.client.renderer.state.CteerGauntletRenderDataKeys;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.gauntlet.player.CteerGauntletPlayerAttachments;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.accessory.AccessorySlot;
import xyz.lumian.constructeer.integration.accessory.IAccessory;

import java.util.Objects;



//**********************************************************************************************************************
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
        at     = @At("TAIL")
    )
    public void extractRenderState(final Avatar avatar, final AvatarRenderState state, final float f,
                                   final CallbackInfo ci)
    {
        //noinspection UnstableApiUsage
        state.setData(
            CteerGauntletRenderDataKeys.PORTABLE,
            avatar.getAttached(CteerGauntletPlayerAttachments.PORTABLE));
        
        final IAccessory accessory = Compat.getAccessory().orElse(null);
        
        if (accessory == null || !(avatar instanceof Player player))
        {
            return;
        }
        
        if (accessory.isEquipped(IAccessory.SlotConstants.HAND, player, CteerGauntletItems.GAUNTLET_OF_POWER, false))
        {
            state.setData(CteerGauntletRenderDataKeys.HAS_ACCESSORY, true);
        }
    }
    
    @Inject(
        method = "renderRightHand",
        at     = @At("TAIL")
    )
    public void renderGauntletOnArm(final PoseStack pose, final SubmitNodeCollector nodes, final int light,
                                    final Identifier skinTexture, final boolean renderSleeve, final CallbackInfo ci)
    {
        final Player    player   = Objects.requireNonNull(Minecraft.getInstance().player);
        final ItemStack gauntlet = AccessorySlot
            .findEquipmentStack(IAccessory.SlotConstants.HAND, player, CteerGauntletItems.GAUNTLET_OF_POWER)
            .orElse(ItemStack.EMPTY);
        
        if (!gauntlet.isEmpty())
        {
            final PlayerModel model = ((AvatarRenderer<?>)(Object) this).getModel();
            GauntletRenderer.INSTANCE.submit(gauntlet, model, pose, nodes, light);
        }
    }
}
