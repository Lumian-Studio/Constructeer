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
package xyz.lumian.constructeer.multimining.item.multimining.area;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.level.Cuboid;
import xyz.lumian.constructeer.multimining.enchantment.CteerMultiMiningEnchantments;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.IToolPredicate;
import xyz.lumian.constructeer.level.BlockContext;



//**********************************************************************************************************************
/// In addition to [IThreeByThreeProvider], this provider is specifically geared towards tools that mine in a 3xNx3
/// block matrix.
///
/// This provider will be using the tool's [CteerMultiMiningEnchantments#PENETRATION] enchantment level to extend
/// the cuboidal area in the opposite direction of the face the block was destroyed on.
public interface IToolProvider
    extends IThreeByThreeProvider
{
    //******************************************************************************************************************
    IToolPredicate predicate();
    
    //==================================================================================================================
    @Override
    default Cuboid getCuboidalArea(final Direction face, final Player player, final ItemStack stack,
                                   final BlockContext block)
    {
        @SuppressWarnings("resource")
        final int extension = player.level().registryAccess()
            .lookup (Registries.ENCHANTMENT)
            .flatMap(reg -> reg.get(CteerMultiMiningEnchantments.PENETRATION))
            .map    (ref -> stack.getEnchantments().getLevel(ref))
            .orElse (0);
        return IThreeByThreeProvider.super
            .getCuboidalArea(face, player, stack, block)
            .expanded(face.getOpposite(), extension);
    }
    
    //==================================================================================================================
    @Override
    default boolean acceptBlock(final Player player, final ItemStack stack, final BlockContext mainBlock,
                                final BlockContext relBlock)
    {
        return (
            this.predicate().test(player, mainBlock, relBlock)
            && (!relBlock.state().requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(relBlock.state()))
        );
    }
    
    @Override
    default Result provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                           final Output output)
    {
        if (!this.predicate().canExecute(player, block, stack))
        {
            return Result.FAILED;
        }
        
        return IThreeByThreeProvider.super.provide(face, player, stack, block, output);
    }
}
