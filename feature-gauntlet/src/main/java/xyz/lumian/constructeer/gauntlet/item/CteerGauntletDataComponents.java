package xyz.lumian.constructeer.gauntlet.item;

import net.minecraft.core.component.DataComponentType;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.gauntlet.item.portable.Grabber;
import xyz.lumian.constructeer.registry.CteerDataComponentRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public final class CteerGauntletDataComponents
    implements IBootstrap
{
    //******************************************************************************************************************
    public static final DataComponentType<Grabber> GRABBER = CteerDataComponentRegistry.register(
        CteerDefine.id("grabber"),
        (b -> b.persistent(Grabber.CODEC).networkSynchronized(Grabber.STREAM_CODEC)));
}
