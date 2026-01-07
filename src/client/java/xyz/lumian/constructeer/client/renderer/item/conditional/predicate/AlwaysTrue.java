package xyz.lumian.constructeer.client.renderer.item.conditional.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import xyz.lumian.constructeer.client.config.ModClientConfig;
import xyz.lumian.constructeer.container.ToolbeltMenu;



//**********************************************************************************************************************
public class AlwaysTrue
    implements MenuPredicate
{
    //******************************************************************************************************************
    public static final AlwaysTrue           INSTANCE  = new AlwaysTrue();
    public static final MapCodec<AlwaysTrue> MAP_CODEC = MapCodec.unit(INSTANCE);
    
    //******************************************************************************************************************
    private AlwaysTrue() {}
    
    //==================================================================================================================
    @Override public MapCodec<? extends MenuPredicate> type() { return AlwaysTrue.MAP_CODEC; }
    
    //==================================================================================================================
    @Override public boolean test(final AbstractContainerMenu menu, final Slot slot) { return true; }
}
