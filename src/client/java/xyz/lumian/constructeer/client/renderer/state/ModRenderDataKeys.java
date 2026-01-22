package xyz.lumian.constructeer.client.renderer.state;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.world.item.ItemStack;



//**********************************************************************************************************************
public final class ModRenderDataKeys
{
    //******************************************************************************************************************
    public static final RenderStateDataKey<Double>    LIVING_FALL_DISTANCE        = RenderStateDataKey.create();
    public static final RenderStateDataKey<ItemStack> HUMANOID_TOOLBELT_EQUIPMENT = RenderStateDataKey.create();
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private ModRenderDataKeys() {}
}
