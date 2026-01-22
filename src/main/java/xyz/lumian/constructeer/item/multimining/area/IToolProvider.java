package xyz.lumian.constructeer.item.multimining.area;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.item.multimining.predicate.IMultiMiningPredicate;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
/// In addition to [IThreeByThreeProvider], this provider is specifically geared towards tools that mine in a 3xNx3
/// block matrix.
public interface IToolProvider
    extends IThreeByThreeProvider
{
    //******************************************************************************************************************
    IMultiMiningPredicate predicate();
    
    //==================================================================================================================
    @Override
    default boolean acceptBlock(final Player player, final ItemStack stack, final BlockContext mainBlock,
                               final BlockContext relBlock)
    {
        return this.predicate().test(player, mainBlock, relBlock);
    }
    
    @Override
    default boolean provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                            final int modifier, final Output output)
    {
        if (!this.predicate().canExecute(player, block, stack))
        {
            return false;
        }
        
        return IThreeByThreeProvider.super.provide(face, player, stack, block, modifier, output);
    }
}
