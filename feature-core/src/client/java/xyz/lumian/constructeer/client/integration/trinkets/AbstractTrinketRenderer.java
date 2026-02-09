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
package xyz.lumian.constructeer.client.integration.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;



//**********************************************************************************************************************
public abstract class AbstractTrinketRenderer
{
    //******************************************************************************************************************
    public static final class TrinketRenderState
    {
        //**************************************************************************************************************
        public ItemStack stack;
        public Container trinketContainer;
        public String    slotGroup;
        public String    slotType;
    }
    
    //******************************************************************************************************************
    static final Map<Item, AbstractTrinketRenderer> RENDERERS = new Object2ObjectOpenHashMap<>();
    
    //******************************************************************************************************************
    public static void registerRenderer(final Item item, final AbstractTrinketRenderer renderer)
    {
        if (AbstractTrinketRenderer.RENDERERS.put(item, renderer) != null)
        {
            throw new IllegalStateException("Trying to register a renderer for an item that already exists!");
        }
    }
    
    //******************************************************************************************************************
    public abstract void render(EntityModel<? extends LivingEntityRenderState> model, PoseStack pose,
                                SubmitNodeCollector nodes, int packedLight, TrinketRenderState state,
                                LivingEntityRenderState entityRenderState, float yaw, float pitch);
}
