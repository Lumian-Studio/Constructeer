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

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import xyz.lumian.constructeer.client.integration.trinkets.AbstractTrinketRenderer;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;



//**********************************************************************************************************************
public final class CteerToolbeltRenderLayers
{
    //******************************************************************************************************************
    private static <S extends HumanoidRenderState, M extends EntityModel<S>> ToolbeltRenderLayer<S, M> generateLayer(
        final RenderLayerParent<S, M>        renderer,
        final EntityRendererProvider.Context context
    )
    {
        return new ToolbeltRenderLayer<>(renderer, context);
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, helper, context) ->
        {
            switch (renderer)
            {
                case HumanoidMobRenderer<?,?,?> rend
                    -> helper.register(CteerToolbeltRenderLayers.generateLayer(rend, context));
                case AvatarRenderer<?>          rend
                    -> helper.register(CteerToolbeltRenderLayers.generateLayer(rend, context));
                default -> {}
            }
        });
        AbstractTrinketRenderer.registerRenderer(CteerToolbeltItems.TOOLBELT, new ToolbeltTrinketRenderer());
    }
    
    //******************************************************************************************************************
    private CteerToolbeltRenderLayers() {}
}
