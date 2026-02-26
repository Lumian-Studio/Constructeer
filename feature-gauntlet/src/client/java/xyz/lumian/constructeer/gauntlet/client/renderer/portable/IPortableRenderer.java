package xyz.lumian.constructeer.gauntlet.client.renderer.portable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.gauntlet.item.portable.IPortableType;



//**********************************************************************************************************************
public interface IPortableRenderer<T>
{
    //******************************************************************************************************************
    record FactoryContext(
        RenderLayerParent<AvatarRenderState, PlayerModel> renderer,
        EntityRendererProvider.Context                    context
    ) {}
    
    @FunctionalInterface
    interface Factory<T>
    {
        //**************************************************************************************************************
        /// Creates a new [IPortableRenderer] for a [IPortableType].
        /// @param context The factory context, state from this context should only be used in
        /// [#renderLayer(Object, PoseStack, SubmitNodeCollector, int, AvatarRenderState, float, float)]
        /// @return The new renderer
        IPortableRenderer<T> create(FactoryContext context);
    }
    
    //******************************************************************************************************************
    /// Hooks into [PlayerModel#setupAnim(Object)] at the bottom of the function to allow overriding model values if the
    /// player is currently carrying an object.
    /// @param object      The object the player is carrying
    /// @param renderState The player render state
    /// @param model       The player model
    default void animate(final T object, final AvatarRenderState renderState, final PlayerModel model)
    {
        if (renderState.swimAmount > 0f)
        {
            model.rightArm.xRot = (model.rightArm.xRot * -1f + (float) Math.PI);
        }
        else
        {
            model.rightArm.xRot = (renderState.isCrouching ? -0.4f : -0.7f);
            AnimationUtils.bobModelPart(model.rightArm, renderState.ageInTicks, 1f);
        }
        
        model.rightArm.zRot = 0f;
        model.leftArm .zRot = 0f;
        
        model.leftArm.xRot = model.rightArm.xRot;
    }
    
    /// Renders the portable layer on top of the player model. If [RenderLayer] specific initialisation is required,
    /// this can be done by overriding [#layerRenderSetup(RenderLayerParent, EntityRendererProvider.Context)].
    /// @param object The object the player is carrying
    /// @param layer  The render layer
    /// @param pose   The [PoseStack]
    /// @param nodes  The [SubmitNodeCollector]
    /// @param light  The light value
    /// @param yRot   The y rotation of the player
    /// @param xRot   The x rotation of the player
    void renderLayer(T object, RenderLayer<AvatarRenderState, PlayerModel> layer, PoseStack pose,
                     SubmitNodeCollector nodes, int light, AvatarRenderState renderState, float yRot, float xRot);
    
    /// Renders the first-person view of the player, if this is left empty, nothing will be drawn, no hands and no
    /// items.
    /// @param object           The object the player is carrying
    /// @param player           The player instance
    /// @param partialTick      The delta between ticks
    /// @param pitch            The view pitch of the camera
    /// @param hand             The hand that should be drawn
    /// @param swingProgress    The current progress of the swing animation
    /// @param item             The item inside the hand
    /// @param equippedProgress The current progress of the equip animation
    /// @param pose             The [PoseStack]
    /// @param nodes            The [SubmitNodeCollector]
    /// @param light            The light value
    void renderFirstPerson(T object, AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                           float swingProgress, ItemStack item, float equippedProgress, PoseStack pose,
                           SubmitNodeCollector nodes, int light);
}
