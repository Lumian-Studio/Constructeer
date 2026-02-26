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
package xyz.lumian.constructeer.toolbelt.client.renderer;

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
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.client.integration.accessory.IAccessoryRenderer;
import xyz.lumian.constructeer.client.registry.CteerRenderRegistry;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.integration.geckolib.EquipmentDefaultedGeoModel;
import xyz.lumian.constructeer.toolbelt.client.renderer.state.CteerToolbeltRenderDataKeys;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.item.ToolbeltItem;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.PouchContent;
import xyz.lumian.constructeer.toolbelt.item.component.ToolbeltStorage;

import java.util.Objects;



//**********************************************************************************************************************
public enum ToolbeltRenderer
    implements IAccessoryRenderer
{
    INSTANCE;
    
    //******************************************************************************************************************
    public static final class ModelRenderer
        extends GeoObjectRenderer<ToolbeltItem, ItemStack, HumanoidRenderState>
    {
        //**************************************************************************************************************
        public static final DataTicket<Integer> POUCH_MASK = DataTicket.create("pouch_mask", Integer.class);
        
        //**************************************************************************************************************
        public ModelRenderer() { super(new EquipmentDefaultedGeoModel<>(CteerDefine.id("toolbelt"))); }
        
        //==============================================================================================================
        @Override
        public void adjustModelBonesForRender(final RenderPassInfo<HumanoidRenderState> renderPassInfo,
                                              final BoneSnapshots snapshots)
        {
            final int mask = Objects.requireNonNull(renderPassInfo.getGeckolibData(ModelRenderer.POUCH_MASK));
            
            for (int i = 0; i < ToolbeltItem.COUNT_UPGRADES; ++i)
            {
                final int id = (i + 1);
                int bit = (1 << i);
                
                if ((mask & bit) == bit)
                {
                    bit <<= ToolbeltItem.COUNT_UPGRADES;
                    
                    // Flapping
                    if ((mask & bit) == 0)
                    {
                        snapshots.get(i > 0 ? "Lid" + id : "Lid").ifPresent(lid ->
                        {
                            final double fall_distance = renderPassInfo.renderState()
                                .getDataOrDefault(CteerToolbeltRenderDataKeys.LIVING_FALL_DISTANCE, 0d);
                            
                            if (fall_distance <= 0.01f)
                            {
                                lid.setRotX(Math.min(0f, (lid.getRotX() + 0.2f)));
                            }
                            else
                            {
                                lid.setRotX((float) (Math.PI * 0.25f * Math.min(1.5f, fall_distance)));
                            }
                        });
                    }
                }
                else
                {
                    snapshots.get(i > 0 ? "Pouch" + id : "Pouch").ifPresent(pouch ->
                    {
                        pouch.skipRender(true);
                        pouch.skipChildrenRender(true);
                    });
                }
            }
        }
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
            final ItemStack gauntlet = ToolbeltRenderer.INSTANCE.getHumanoidRenderStateEquipment(renderState);
            
            if (!gauntlet.is(CteerToolbeltItems.TOOLBELT))
            {
                return;
            }
            
            ToolbeltRenderer.INSTANCE.submit(gauntlet, pose, nodes, light, renderState);
        }
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        CteerRenderRegistry.registerAccessoryRenderer(CteerToolbeltItems.TOOLBELT, ToolbeltRenderer.INSTANCE);
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
    @Override public IAccessory.SlotReference allowedSlot() { return IAccessory.SlotConstants.BELT; }
    
    //==================================================================================================================
    @Override
    public void render(final ItemStack stack, final int slotIndex,
                       final EntityModel<? extends LivingEntityRenderState> model, final PoseStack pose,
                       final SubmitNodeCollector nodes, final int light, final LivingEntityRenderState renderState,
                       final float yaw, final float pitch)
    {
        if (!(renderState instanceof HumanoidRenderState h_state))
        {
            return;
        }
        
        this.submit(stack, pose, nodes, light, h_state);
    }
    
    //==================================================================================================================
    public void submit(final ItemStack gauntlet, final PoseStack pose, final SubmitNodeCollector nodes, final int light,
                       final HumanoidRenderState renderState)
    {
        final ToolbeltStorage storage = gauntlet.get(CteerToolbeltDataComponents.TOOLBELT_STORAGE);
        
        if (storage == null)
        {
            return;
        }
        
        pose.pushPose();
        {
            renderState.addGeckolibData(DataTickets.PACKED_LIGHT, light);
            
            final int mask = storage.stream().filter(entry -> (entry.id() > 0)).mapToInt(entry ->
            {
                int bits = (1 << (entry.id() - 1));
                
                if (!entry.pouch()
                    .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY)
                    .isEmpty()
                )
                {
                    bits |= (bits << ToolbeltItem.COUNT_UPGRADES);
                }
                
                return bits;
            }).reduce(0, (mask1, mask2) -> (mask1 | mask2));
            renderState.addGeckolibData(ModelRenderer.POUCH_MASK, mask);
            
            pose.mulPose(Axis.XP.rotationDegrees(180f));
            pose.mulPose(Axis.YP.rotationDegrees(180f));
            pose.translate(-0.5f, -1.25f, -0.5f);
            
            final CameraRenderState cam = Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState;
            this.renderer.performRenderPass(renderState, pose, nodes, cam);
        }
        pose.popPose();
    }
}
