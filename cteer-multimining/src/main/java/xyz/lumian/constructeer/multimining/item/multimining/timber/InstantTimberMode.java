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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.entity.BlockEntity;
import xyz.lumian.constructeer.multimining.util.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public class InstantTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        final ItemStack copy = stack.copy();
        final Tool      tool = stack.get(DataComponents.TOOL);
        
        for (final var to_destroy : blocks)
        {
            if (to_destroy.state().isAir())
            {
                continue;
            }
            
            final boolean destroyed = TimberModeHelper.destroy(player, to_destroy.state(), to_destroy.pos(), true);
            
            if (destroyed && doDropsIfEligible)
            {
                final BlockEntity be = to_destroy.getBlockEntity();
                TimberModeHelper.mined(player, copy, tool, to_destroy.state(), mainBlock.pos(), be, true);
            }
        }
        
        return true;
    }
}
