package xyz.lumian.constructeer.gauntlet.item;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;



//**********************************************************************************************************************
public class GauntletItem
    extends Item
    implements GeoItem
{
    //******************************************************************************************************************
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    
    //******************************************************************************************************************
    public GauntletItem(final Properties properties) { super(properties); }
    
    //==================================================================================================================
    @Override public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.geoCache; }
}
