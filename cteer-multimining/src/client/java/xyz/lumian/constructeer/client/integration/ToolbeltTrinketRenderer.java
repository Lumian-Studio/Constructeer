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
package xyz.lumian.constructeer.client.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.client.renderer.layer.ToolbeltRenderLayer;
import xyz.lumian.constructeer.client.renderer.state.ModRenderDataKeys;
import xyz.lumian.constructeer.integration.impl.ITrinkets;
import xyz.lumian.constructeer.item.ModItems;

import java.util.concurrent.atomic.AtomicReference;



//**********************************************************************************************************************
public class ToolbeltTrinketRenderer
    implements TrinketRenderer
{
    //******************************************************************************************************************
    static
    {
        TrinketRendererRegistry.registerRenderer(ModItems.TOOLBELT, new ToolbeltTrinketRenderer());
    }
    
    //******************************************************************************************************************
    private final AtomicReference<@Nullable ToolbeltRenderLayer<?, ?>> layer = new AtomicReference<>();
    
    //******************************************************************************************************************
    public ToolbeltTrinketRenderer()
    {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(((type, renderer, helper, context) ->
        {
            if (!(renderer instanceof AvatarRenderer<?> avatar_renderer))
            {
                return;
            }
            
            this.layer.setPlain(new ToolbeltRenderLayer<>(avatar_renderer, context));
        }));
    }
    
    //==================================================================================================================
    @Override
    public void render(final ItemStack stack, final SlotReference slotReference,
                       final EntityModel<? extends LivingEntityRenderState> entityModel, final PoseStack poseStack,
                       final SubmitNodeCollector submitNodeCollector, final int packedLight,
                       final LivingEntityRenderState livingEntityRenderState, final float yaw, final float pitch)
    {
        if (
            livingEntityRenderState.entityType != EntityType.PLAYER
            || !(livingEntityRenderState instanceof HumanoidRenderState h_state)
            || h_state.legsEquipment.is(ModItems.TOOLBELT)
        )
        {
            return;
        }
        
        final ToolbeltRenderLayer<?, ?> layer = this.layer.getPlain();
        
        if (layer == null)
        {
            return;
        }
        
        final String   group  = ITrinkets.DefaultSlot.LEGS_BELT.group;
        final String   type   = ITrinkets.DefaultSlot.LEGS_BELT.type;
        final SlotType s_type = slotReference.inventory().getSlotType();
        
        if (!s_type.getGroup().equals(group) || !s_type.getName().equals(type))
        {
            return;
        }
        
        h_state.setData(ModRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT, stack);
        layer.submit(poseStack, submitNodeCollector, packedLight, h_state, yaw, pitch);
    }
}
