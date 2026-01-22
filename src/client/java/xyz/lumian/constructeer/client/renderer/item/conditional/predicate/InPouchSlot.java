package xyz.lumian.constructeer.client.renderer.item.conditional.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import xyz.lumian.constructeer.client.config.ModClientConfig;



//**********************************************************************************************************************
public class InPouchSlot
    implements MenuPredicate
{
    //******************************************************************************************************************
    public static final InPouchSlot           INSTANCE  = new InPouchSlot();
    public static final MapCodec<InPouchSlot> MAP_CODEC = MapCodec.unit(INSTANCE);
    
    //******************************************************************************************************************
    private InPouchSlot() {}
    
    //==================================================================================================================
    @Override public MapCodec<? extends MenuPredicate> type() { return InPouchSlot.MAP_CODEC; }
    
    //==================================================================================================================
    @Override
    public boolean test(final AbstractContainerMenu menu, final Slot slot)
    {
        return ModClientConfig.INSTANCE.pouchContentRenderMode().get().test(slot);
    }
}
