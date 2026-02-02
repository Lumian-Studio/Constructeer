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

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.multimining.util.BlockContext;
import xyz.lumian.constructeer.multimining.util.Cuboid;

import java.util.Map;



//**********************************************************************************************************************
/// A three by three area provider that, depending on the given face the player is looking, provides a 2-dimensional
/// area of blocks around the target block.
///
/// This provider facilitates the provider `data` argument for extending the given area into the third dimension,
/// opposite of the block face direction.
public interface IThreeByThreeProvider
    extends IAreaProvider
{
    //******************************************************************************************************************
    /// A 2-dimensional cuboid that extends 3 blocks in any of 2 axes but only 1 wide on the third axis.
    Map<Direction.Axis, Cuboid> AXIS_CUBOIDS = ImmutableMap.copyOf(Util.makeEnumMap(
        Direction.Axis.class,
        (axis -> switch (axis)
        {
            case Direction.Axis.Z -> new Cuboid(3, 3, 1, new Vec3i(1, 1, 0));
            case Direction.Axis.X -> new Cuboid(1, 3, 3, new Vec3i(0, 1, 1));
            case Direction.Axis.Y -> new Cuboid(3, 1, 3, new Vec3i(1, 0, 1));
        })));
    
    //******************************************************************************************************************
    boolean acceptBlock(Player player, ItemStack stack, BlockContext mainBlock, BlockContext relBlock);
    
    @Override
    default Result provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                           final int modifier, final Output output)
    {
        final Cuboid cuboid = IThreeByThreeProvider.AXIS_CUBOIDS
            .get(face.getAxis())
            .expanded(face.getOpposite(), modifier);
        
        for (final var rel_pos : cuboid)
        {
            final BlockContext neighbour = block.relative(rel_pos);
            
            if (this.acceptBlock(player, stack, block, neighbour) && !output.acceptAndTest(neighbour))
            {
                return Result.SUCCESS;
            }
        }
        
        return Result.SUCCESS;
    }
}
