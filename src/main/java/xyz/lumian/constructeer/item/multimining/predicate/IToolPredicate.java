package xyz.lumian.constructeer.item.multimining.predicate;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.item.multimining.SneakMode;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public interface IToolPredicate
    extends IMultiMiningPredicate
{
    //******************************************************************************************************************
    HolderSet<Block> alwaysIncluded();
    List<HolderSet<Block>> includeGroups();
    HolderSet<Block> excluded();
    HolderSet<Block> ignored();
    SneakMode sneakMode();
    boolean shouldCheckHardness();
    
    //==================================================================================================================
    default boolean isIncluded(final BlockContext main, final BlockContext test)
    {
        if (test.is(this.alwaysIncluded()))
        {
            return true;
        }
        
        for (final var group : this.includeGroups())
        {
            if (main.is(group) && test.is(group))
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    default boolean canExecute(final Player player, final BlockContext main, final ItemStack stack)
    {
        if (player.isCrouching() && this.sneakMode() == SneakMode.VANILLA)
        {
            return false;
        }
        
        // Is the block possibly on the index? (and what the hell did it do to be on there)
        if (main.is(this.ignored()))
        {
            return false;
        }
        
        // Check if it is a "solid" block, and that it can be mined with the tool
        return (main.isSolid() && stack.isCorrectToolForDrops(main.state()));
    }
    
    default boolean hardnessSuffices(final BlockContext mainBlock, final BlockContext testBlock)
    {
        return (!this.shouldCheckHardness() || mainBlock.getHardness() >= testBlock.getHardness());
    }
    
    default boolean test(final Player player, final BlockContext main, final BlockContext test)
    {
        // If we got the same block, we don't need to test further
        if (main.pos().equals(test.pos()))
        {
            return true;
        }
        
        // Check if the block is not ignored and that sneaking allows the player to break multiple blocks
        if (player.isCrouching() && !this.sneakMode().test(main, test))
        {
            return false;
        }
        
        // Check whether the block is valid and that it matches
        return (
            test.isSolid()
            && (this.hardnessSuffices(main, test) || this.isIncluded(main, test))
            && !test.is(this.excluded())
        );
    }
}
