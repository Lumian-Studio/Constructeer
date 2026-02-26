package xyz.lumian.constructeer.integration.geckolib;

import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedGeoModel;



//**********************************************************************************************************************
public class EquipmentDefaultedGeoModel<T extends GeoAnimatable>
    extends DefaultedGeoModel<T>
{
    //******************************************************************************************************************
    public EquipmentDefaultedGeoModel(final Identifier assetSubpath) { super(assetSubpath); }
    
    //==================================================================================================================
    @Override protected String subtype() { return "equipment"; }
}
