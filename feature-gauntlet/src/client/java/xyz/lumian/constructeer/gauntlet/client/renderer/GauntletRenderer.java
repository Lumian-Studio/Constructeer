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
package xyz.lumian.constructeer.gauntlet.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.client.integration.accessory.IAccessoryRenderer;
import xyz.lumian.constructeer.client.registry.CteerRenderRegistry;
import xyz.lumian.constructeer.gauntlet.client.renderer.state.CteerGauntletRenderDataKeys;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.gauntlet.item.GauntletItem;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.integration.geckolib.EquipmentDefaultedGeoModel;



//**********************************************************************************************************************
public enum GauntletRenderer
    implements IAccessoryRenderer
{
    INSTANCE;
    
    //******************************************************************************************************************
    public static final class ModelRenderer
        extends GeoObjectRenderer<GauntletItem, ItemStack, HumanoidRenderState>
    {
        //**************************************************************************************************************
        public ModelRenderer() { super(new EquipmentDefaultedGeoModel<>(CteerDefine.id("gauntlet_of_power"))); }
    }
    
    public static class Layer<S extends HumanoidRenderState, M extends HumanoidModel<S>>
        extends RenderLayer<S, M>
    {
        //**************************************************************************************************************
        public Layer(final RenderLayerParent<S, M> renderer) { super(renderer); }
        
        //==============================================================================================================
        @Override
        public void submit(final PoseStack pose, final SubmitNodeCollector nodes, final int light,
                           final HumanoidRenderState renderState, final float yRot, final float xRot)
        {
            final ItemStack gauntlet = GauntletRenderer.INSTANCE.getHumanoidRenderStateEquipment(renderState);
            
            if (
                !gauntlet.is(CteerGauntletItems.GAUNTLET_OF_POWER)
                || renderState.getDataOrDefault(CteerGauntletRenderDataKeys.HAS_ACCESSORY, false)
            )
            {
                return;
            }
            
            GauntletRenderer.INSTANCE.submit(gauntlet, this.getParentModel(), pose, nodes, light);
        }
    }
    
    //******************************************************************************************************************
    @ApiStatus.Internal
    public static void initialise()
    {
        CteerRenderRegistry.registerAccessoryRenderer(CteerGauntletItems.GAUNTLET_OF_POWER, GauntletRenderer.INSTANCE);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, helper, context) ->
        {
            switch (renderer)
            {
                case AvatarRenderer<?>            rend -> helper.register(new Layer<>(rend));
                case HumanoidMobRenderer<?, ?, ?> rend -> helper.register(new Layer<>(rend));
                default                                -> {}
            }
        });
    }
    
    //******************************************************************************************************************
    private final ModelRenderer renderer = new ModelRenderer();

    //******************************************************************************************************************
    @Override public IAccessory.SlotReference allowedSlot() { return IAccessory.SlotConstants.HAND; }
    
    //==================================================================================================================
    @Override
    public void render(final ItemStack stack, final int slotIndex,
                       final EntityModel<? extends LivingEntityRenderState> model, final PoseStack pose,
                       final SubmitNodeCollector nodes, final int light, final LivingEntityRenderState renderState,
                       final float yaw, final float pitch)
    {
        if (!(renderState instanceof HumanoidRenderState) || !(model instanceof HumanoidModel<?> h_model))
        {
            return;
        }
        
        this.submit(stack, h_model, pose, nodes, light);
    }
    
    //==================================================================================================================
    public void submit(final ItemStack gauntlet, final HumanoidModel<?> model, final PoseStack pose,
                       final SubmitNodeCollector nodes, final int light)
    {
        pose.pushPose();
        {
            pose.mulPose(Axis.XP.rotationDegrees(180f));
            pose.mulPose(Axis.YP.rotationDegrees(180f));
            pose.translate(-0.127f, -1.27f, -0.5f);
            final CameraRenderState cam = Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState;
            this.renderer.performRenderPass((GauntletItem) CteerGauntletItems.GAUNTLET_OF_POWER, gauntlet, pose, nodes,
                                            cam, light, 0,
                (((renderPassInfo, snapshots) -> snapshots.get("Gauntlet").ifPresent(bone -> bone
                    .setRotation(-model.rightArm.xRot, -model.rightArm.yRot, -model.leftArm.zRot)))));
        }
        pose.popPose();
    }
}
