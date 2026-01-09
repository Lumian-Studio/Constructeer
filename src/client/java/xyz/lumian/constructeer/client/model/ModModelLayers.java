package xyz.lumian.constructeer.client.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;



//**********************************************************************************************************************
public final class ModModelLayers
{
    //******************************************************************************************************************
    public static final ModelLayerLocation PLAYER_TOOLBELT = register("player", "toolbelt", ToolbeltModel::createLayer);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static ModelLayerLocation register(final String path, final String layer,
                                               final EntityModelLayerRegistry.TexturedModelDataProvider provider)
    {
        final ModelLayerLocation location = new ModelLayerLocation(Identifier.withDefaultNamespace(path), layer);
        EntityModelLayerRegistry.registerModelLayer(location, provider);
        return location;
    }
    
    //******************************************************************************************************************
    private ModModelLayers() {}
}
