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
package xyz.lumian.constructeer.toolbelt.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.client.integration.trinkets.AbstractTrinketRenderer;
import xyz.lumian.constructeer.integration.trinkets.ITrinkets;
import xyz.lumian.constructeer.toolbelt.client.renderer.state.CteerToolbeltRenderDataKeys;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;



//**********************************************************************************************************************
public class ToolbeltTrinketRenderer
    extends AbstractTrinketRenderer
{
    //******************************************************************************************************************
    private @Nullable ToolbeltRenderLayer<?, ?> layer;
    
    //******************************************************************************************************************
    public ToolbeltTrinketRenderer()
    {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(((type, renderer, helper, context) ->
        {
            if (!(renderer instanceof AvatarRenderer<?> avatar_renderer))
            {
                return;
            }
            
            this.layer = new ToolbeltRenderLayer<>(
                avatar_renderer, context);
        }));
    }
    
    //==================================================================================================================
    @Override
    public void render(final EntityModel<? extends LivingEntityRenderState> model, final PoseStack pose,
                       final SubmitNodeCollector nodes, final int packedLight, final TrinketRenderState state,
                       final LivingEntityRenderState livingEntityRenderState, final float yaw, final float pitch)
    {
        if (
               this.layer == null
            || livingEntityRenderState.entityType != EntityType.PLAYER
            || !(livingEntityRenderState instanceof HumanoidRenderState h_state)
            || h_state.legsEquipment.is(CteerToolbeltItems.TOOLBELT)
            || !state.slotGroup.equals(ITrinkets.DefaultSlot.LEGS_BELT.group)
            || !state.slotType .equals(ITrinkets.DefaultSlot.LEGS_BELT.type)
        )
        {
            return;
        }
        
        h_state.setData(CteerToolbeltRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT, state.stack);
        this.layer.submit(pose, nodes, packedLight, h_state, yaw, pitch);
    }
}
