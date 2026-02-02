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
package xyz.lumian.constructeer.multimining.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import xyz.lumian.constructeer.multimining.config.ModClientConfig;
import xyz.lumian.constructeer.multimining.model.ModModelLayers;
import xyz.lumian.constructeer.multimining.model.ToolbeltModel;
import xyz.lumian.constructeer.multimining.renderer.state.ModRenderDataKeys;



//**********************************************************************************************************************
public class ToolbeltRenderLayer<S extends HumanoidRenderState, M extends EntityModel<S>>
    extends RenderLayer<S, M>
{
    //******************************************************************************************************************
    private final ToolbeltModel          model;
    private final EquipmentLayerRenderer equipmentRenderer;
    
    //******************************************************************************************************************
    public ToolbeltRenderLayer(final RenderLayerParent<S, M> renderer, final EntityRendererProvider.Context context)
    {
        this(renderer, context.getModelSet(), context.getEquipmentRenderer());
    }
    
    public ToolbeltRenderLayer(final RenderLayerParent<S, M> renderer, final EntityModelSet models,
                               final EquipmentLayerRenderer equipmentRenderer)
    {
        super(renderer);
        this.model             = new ToolbeltModel(models.bakeLayer(ModModelLayers.PLAYER_TOOLBELT));
        this.equipmentRenderer = equipmentRenderer;
    }
    
    //==================================================================================================================
    @Override
    public void submit(final PoseStack poseStack, final SubmitNodeCollector nodeCollector, final int packedLight,
                       final HumanoidRenderState renderState, final float yRot, final float xRot)
    {
        if (!ModClientConfig.INSTANCE.renderToolbeltModel().get().test(renderState))
        {
            return;
        }
        
        final ItemStack  toolbelt   = renderState.getDataOrDefault(ModRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT,
                                                                   ItemStack.EMPTY);
        final Equippable equippable = toolbelt.get(DataComponents.EQUIPPABLE);
        
        if (equippable != null && equippable.assetId().isPresent())
        {
            this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                                                equippable.assetId().get(), this.model, renderState, toolbelt,
                                                poseStack, nodeCollector, packedLight, renderState.outlineColor);
        }
    }
}
