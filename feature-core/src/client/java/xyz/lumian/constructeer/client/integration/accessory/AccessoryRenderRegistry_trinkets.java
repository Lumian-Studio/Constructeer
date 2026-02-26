package xyz.lumian.constructeer.client.integration.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;



//**********************************************************************************************************************
public record AccessoryRenderRegistry_trinkets()
    implements IAccessoryRenderRegistry
{
    //******************************************************************************************************************
    private record Renderer(
        IAccessoryRenderer renderer,
        String             group,
        String             name,
        EquipmentSlot      fallback
    ) implements TrinketRenderer
    {
        //**************************************************************************************************************
        public Renderer(final IAccessoryRenderer renderer)
        {
            this(
                renderer,
                renderer.allowedSlot().id(),
                renderer.allowedSlot().id().indexOf('/'),
                renderer.allowedSlot().vanillaPendant()
            );
        }
        
        //--------------------------------------------------------------------------------------------------------------
        private Renderer(final IAccessoryRenderer renderer, final String name, final int index,
                         final EquipmentSlot fallback)
        {
            this(renderer, name.substring(0, index), name.substring(index + 1), fallback);
        }
        
        //==============================================================================================================
        @Override
        public void render(final ItemStack stack, final SlotReference slotReference,
                           final EntityModel<? extends LivingEntityRenderState> contextModel, final PoseStack pose,
                           final SubmitNodeCollector nodes, final int light, final LivingEntityRenderState state,
                           final float limbAngle, final float limbDistance)
        {
            final SlotType type = slotReference.inventory().getSlotType();
            
            if (!type.getGroup().equals(this.group) || !type.getName().equals(this.name))
            {
                return;
            }
            
            this.renderer.render(stack, slotReference.index(), contextModel, pose, nodes, light, state, limbAngle,
                                 limbDistance);
        }
    }
    
    //******************************************************************************************************************
    @Override
    public void register(final Item item, final IAccessoryRenderer renderer)
    {
        TrinketRendererRegistry.registerRenderer(item, new Renderer(renderer));
    }
}
