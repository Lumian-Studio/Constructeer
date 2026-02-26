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
package xyz.lumian.constructeer.client.integration.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.integration.accessory.IAccessory;

import java.util.Objects;



//**********************************************************************************************************************
public interface IAccessoryRenderer
{
    //******************************************************************************************************************
    static ItemStack getHumanoidRenderStateEquipment(final EquipmentSlot slot, final HumanoidRenderState renderState)
    {
        return switch (Objects.requireNonNull(slot))
        {
            case MAINHAND -> renderState.rightHandItemStack;
            case OFFHAND  -> renderState.leftHandItemStack;
            case LEGS     -> renderState.legsEquipment;
            case FEET     -> renderState.feetEquipment;
            case HEAD     -> renderState.headEquipment;
            case CHEST    -> renderState.chestEquipment;
            default       -> ItemStack.EMPTY;
        };
    }
    
    //******************************************************************************************************************
    IAccessory.SlotReference allowedSlot();
    
    //==================================================================================================================
    default ItemStack getHumanoidRenderStateEquipment(final HumanoidRenderState renderState)
    {
        return IAccessoryRenderer.getHumanoidRenderStateEquipment(this.allowedSlot().vanillaPendant(), renderState);
    }
    
    //==================================================================================================================
    void render(ItemStack stack, int slotIndex, EntityModel<? extends LivingEntityRenderState> model, PoseStack pose,
                SubmitNodeCollector nodes, int light, LivingEntityRenderState renderState, float yaw, float pitch);
}
