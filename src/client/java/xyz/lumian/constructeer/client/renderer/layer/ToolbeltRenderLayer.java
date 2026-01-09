package xyz.lumian.constructeer.client.renderer.layer;

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
import xyz.lumian.constructeer.client.config.ModClientConfig;
import xyz.lumian.constructeer.client.impl.IHumanoidRenderStateExtension;
import xyz.lumian.constructeer.client.model.ModModelLayers;
import xyz.lumian.constructeer.client.model.ToolbeltModel;



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
        
        final ItemStack  toolbelt   = ((IHumanoidRenderStateExtension) renderState).constructeer$getToolbeltEquipment();
        final Equippable equippable = toolbelt.get(DataComponents.EQUIPPABLE);
        
        if (equippable != null && equippable.assetId().isPresent())
        {
            this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                                                equippable.assetId().get(), this.model, renderState, toolbelt,
                                                poseStack, nodeCollector, packedLight, renderState.outlineColor);
        }
    }
}
