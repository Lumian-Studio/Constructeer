package xyz.lumian.constructeer.item.multimining.predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
public interface IMultiMiningPredicate
{
    //******************************************************************************************************************
    MultiMiningPredicateType<? extends IMultiMiningPredicate> type();
    
    //==================================================================================================================
    boolean canExecute(final Player player, final BlockContext main, final ItemStack stack);
    boolean test(Player player, BlockContext mainBlock, BlockContext testBlock);
}
