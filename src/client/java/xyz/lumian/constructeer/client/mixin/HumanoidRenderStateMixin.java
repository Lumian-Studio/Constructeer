package xyz.lumian.constructeer.client.mixin;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.lumian.constructeer.client.impl.IHumanoidRenderStateExtension;



//**********************************************************************************************************************
@Mixin(HumanoidRenderState.class)
public abstract class HumanoidRenderStateMixin
    implements IHumanoidRenderStateExtension
{
    //******************************************************************************************************************
    @Unique public double    fallDistance;
    @Unique public ItemStack toolbeltEquipment = ItemStack.EMPTY;
    
    //******************************************************************************************************************
    @Override
    public double constructeer$getFallDistance() { return this.fallDistance; }
    
    @Override
    public ItemStack constructeer$getToolbeltEquipment() { return this.toolbeltEquipment; }
    
    //==================================================================================================================
    @Override
    public void constructeer$setFallDistance(double value) { this.fallDistance = value; }
    
    @Override
    public void constructeer$setToolbeltEquipment(final ItemStack toolbelt)
    {
        this.toolbeltEquipment = toolbelt;
    }
}
