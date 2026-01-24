package xyz.lumian.constructeer.item.multimining.area;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.Cuboid;

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
