package xyz.lumian.constructeer.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import xyz.lumian.constructeer.entity.ModEntities;



//**********************************************************************************************************************
public final class ModEntityRenderers
{
    //******************************************************************************************************************
    public static void initialise()
    {
        EntityRenderers.register(ModEntities.FALLING_OBJECT, FallingObjectRenderer::new);
    }
    
    //******************************************************************************************************************
    private ModEntityRenderers() {}
}
