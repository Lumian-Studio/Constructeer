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
package xyz.lumian.constructeer.multimining.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMiningBox;
import xyz.lumian.constructeer.multimining.sound.CteerMultiMiningSoundEvents;
import xyz.lumian.constructeer.level.BlockContext;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;



//**********************************************************************************************************************
public class FallingTreeTimberMode
    extends FallingTimberMode
{
    //******************************************************************************************************************
    protected MultiMiningBox createMultiMiningBox(final Direction face, final Player player, final ItemStack stack,
                                                  final BlockContext mainBlock, final List<BlockContext> blocks,
                                                  final Function<BlockContext, List<ItemStack>> dropsCollector)
    {
        final Predicate<BlockState> base_predicate = (state -> state.getBlock() == mainBlock.state().getBlock());
        return MultiMiningBox.create(blocks, dropsCollector, mainBlock, base_predicate);
    }
    
    @Override
    protected SoundEvent getSoundEffect(final Player player, final ItemStack stack)
    {
        return CteerMultiMiningSoundEvents.TREE_FALLING;
    }
}
