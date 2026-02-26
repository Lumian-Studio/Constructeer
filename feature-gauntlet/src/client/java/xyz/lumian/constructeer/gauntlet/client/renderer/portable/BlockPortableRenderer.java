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
package xyz.lumian.constructeer.gauntlet.client.renderer.portable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.gauntlet.item.portable.BlockPortableType;



//**********************************************************************************************************************
public class BlockPortableRenderer
    implements IPortableRenderer<BlockPortableType.BlockData>
{
    //******************************************************************************************************************
    BlockPortableRenderer(final FactoryContext context) {}
    
    //==================================================================================================================
    @Override
    public void renderLayer(final BlockPortableType.BlockData block,
                            final RenderLayer<AvatarRenderState, PlayerModel> layer, final PoseStack pose,
                            final SubmitNodeCollector nodes, final int packedLight, final AvatarRenderState renderState,
                            final float yRot, final float xRot)
    {
        pose.pushPose();
        {
            final PlayerModel model = layer.getParentModel();
            
            pose.scale(-0.5F, -0.5F, 0.5F);
            pose.translate(-0.5f, (model.rightArm.y * -0.141f), -0.5f);
            
            final float radius = 1.25f;
            
            final float y = (radius * Mth.cos(model.rightArm.xRot));
            final float z = (radius * Mth.sin(model.rightArm.xRot));
            pose.translate(0f, -y, (z * (renderState.swimAmount > 0f ? -1 : 1)));
            
            nodes.submitBlock(pose, block.state(), packedLight, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
        }
        pose.popPose();
    }
    
    @Override
    public void renderFirstPerson(final BlockPortableType.BlockData block, final AbstractClientPlayer player,
                                  final float partialTick, final float pitch, final InteractionHand hand,
                                  final float swingProgress, final ItemStack item, final float equippedProgress,
                                  final PoseStack pose, final SubmitNodeCollector nodes, final int packedLight)
    {
        pose.pushPose();
        {
            pose.translate(-0.05F, -0.30F + swingProgress * 0.05f, -0.50F - swingProgress * 0.1f);
            pose.mulPose(Axis.XP.rotationDegrees(-160.0F));
            pose.translate(0.25F, 0.1875F, 0.25F);
            pose.scale(-0.4F, -0.4F, 0.4F);
            pose.mulPose(Axis.YP.rotationDegrees(90.0F));
            
            nodes.submitBlock(pose, block.state(), packedLight, OverlayTexture.NO_OVERLAY, 0);
        }
        pose.popPose();
    }
}
