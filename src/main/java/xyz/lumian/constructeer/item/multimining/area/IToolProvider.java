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
package xyz.lumian.constructeer.item.multimining.area;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.item.multimining.predicate.IMultiMiningPredicate;
import xyz.lumian.constructeer.item.multimining.predicate.IToolPredicate;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
/// In addition to [IThreeByThreeProvider], this provider is specifically geared towards tools that mine in a 3xNx3
/// block matrix.
public interface IToolProvider
    extends IThreeByThreeProvider
{
    //******************************************************************************************************************
    IToolPredicate predicate();
    
    //==================================================================================================================
    @Override
    default boolean acceptBlock(final Player player, final ItemStack stack, final BlockContext mainBlock,
                               final BlockContext relBlock)
    {
        return this.predicate().test(player, mainBlock, relBlock);
    }
    
    @Override
    default Result provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                           final int modifier, final Output output)
    {
        if (!this.predicate().canExecute(player, block, stack))
        {
            return Result.FAILED;
        }
        
        return IThreeByThreeProvider.super.provide(face, player, stack, block, modifier, output);
    }
}
