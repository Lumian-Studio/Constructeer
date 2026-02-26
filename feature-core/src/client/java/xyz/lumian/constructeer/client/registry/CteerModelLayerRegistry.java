package xyz.lumian.constructeer.client.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.Constructeer;



//**********************************************************************************************************************
public class CteerModelLayerRegistry
{
    //******************************************************************************************************************
    public static ModelLayerLocation register(final Identifier                                         modelId,
                                              final String                                             layer,
                                              final EntityModelLayerRegistry.TexturedModelDataProvider provider)
    {
        final ModelLayerLocation location = new ModelLayerLocation(modelId, layer);
        EntityModelLayerRegistry.registerModelLayer(location, provider);
        Constructeer.sendGlobalBootstrapReport("registered layer '%s' for model '%s'", layer, modelId);
        return location;
    }
}
