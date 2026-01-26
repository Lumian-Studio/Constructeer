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
package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.SemanticContract;

import java.util.List;
import java.util.function.Function;



//**********************************************************************************************************************
/// What??? A [Function] would've worked just as well instead of this interface? well... what goes around, comes around
@FunctionalInterface
public interface IJustinTimbermode
{
    //******************************************************************************************************************
    /// First cries you a river and then gives the blocks off to the level to deal with it in some way.
    /// @return `true` if the block should be removed
    @SemanticContract.Server
    boolean cryMeARiver(Direction face, Player player, ItemStack stack, BlockContext mainBlock,
                        List<BlockContext> blocks, boolean doDropsIfEligible);
    
    default boolean shouldDamageStack(BlockContext block) { return true; }
}
