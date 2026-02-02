/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.multimining.item.multimining.predicate;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.multimining.util.BlockContext;

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
