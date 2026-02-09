package xyz.lumian.constructeer.client.integration.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;



//**********************************************************************************************************************
record TrinketRenderingIntegration(
    AbstractTrinketRenderer                    renderer,
    AbstractTrinketRenderer.TrinketRenderState renderState
)
    implements TrinketRenderer
{
    //******************************************************************************************************************
    static
    {
        AbstractTrinketRenderer.RENDERERS.forEach((item, renderer) -> TrinketRendererRegistry.registerRenderer(
            item,
            new TrinketRenderingIntegration(renderer, new AbstractTrinketRenderer.TrinketRenderState())));
    }
    
    //******************************************************************************************************************
    @Override
    public void render(final ItemStack itemStack, final SlotReference slotReference,
                       final EntityModel<? extends LivingEntityRenderState> entityModel, final PoseStack poseStack,
                       final SubmitNodeCollector submitNodeCollector, final int packedLight,
                       final LivingEntityRenderState state, final float yaw, final float pitch)
    {
        final SlotType type = slotReference.inventory().getSlotType();
        this.renderState.stack            = itemStack;
        this.renderState.trinketContainer = slotReference.inventory();
        this.renderState.slotGroup        = type.getGroup();
        this.renderState.slotType         = type.getName();
        this.renderer.render(entityModel, poseStack, submitNodeCollector, packedLight, this.renderState, state, yaw,
                             pitch);
    }
}
