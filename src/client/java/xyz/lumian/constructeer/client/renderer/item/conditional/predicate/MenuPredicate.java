package xyz.lumian.constructeer.client.renderer.item.conditional.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.function.BiPredicate;



//**********************************************************************************************************************
public interface MenuPredicate
    extends BiPredicate<AbstractContainerMenu, Slot>
{
    //******************************************************************************************************************
    MapCodec<? extends MenuPredicate> type();
}
