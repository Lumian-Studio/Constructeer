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
package xyz.lumian.constructeer.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;



//**********************************************************************************************************************
/// Describes a cuboidal area of position vectors, also directly integrates with [BlockPos].
/// The basic layout of this class describes the bounds of the cuboid shape and an optional pivot point that describes
/// where in the cuboidal area the start position is, for cases where the cuboid needs to be computed relative to
/// a given position. By default, this pivot starts by \[0, 0, 0], so the bottom left corner on the front.
public record Cuboid(int xCount, int yCount, int zCount, Vec3i pivot)
    implements Iterable<Vec3i>
{
    //******************************************************************************************************************
    /// A pre-computed list with the all the block positions in the cuboid.
    public static class Cached
        extends ArrayList<BlockPos>
    {
        //**************************************************************************************************************
        public final Cuboid cuboid;
        
        //**************************************************************************************************************
        Cached(final BlockPos[] positions, final Cuboid cuboid)
        {
            super(Arrays.asList(positions));
            this.cuboid = cuboid;
        }
    }
    
    public static class CubeIterator
        implements Iterator<Vec3i>
    {
        //**************************************************************************************************************
        private final Cuboid cuboid;
        private final int    size;
        private final Vec3i  origin;
        
        private int i;
        
        //**************************************************************************************************************
        public CubeIterator(final Cuboid cuboid, final Vec3i origin)
        {
            this.cuboid = cuboid;
            this.size   = cuboid.size();
            this.origin = origin;
            this.i      = 0;
        }
        
        //==============================================================================================================
        @Override public boolean hasNext() { return (this.i < this.size); }
        
        //==============================================================================================================
        @Override public Vec3i next() { return this.cuboid.getAbsoluteUnchecked(this.origin, this.i++); }
        
        public BlockPos nextPos(final BlockPos origin) { return origin.offset(this.next()); }
    }
    
    //******************************************************************************************************************
    /// Create a new [Cuboid] from the given two positions, where all positions between `startPos` and `endPos`
    /// (inclusive) are considered part of the cuboid.
    /// @param startPos The start position
    /// @param endPos   The end position
    /// @param origin   The origin position, where [Cuboid#pivot()] will be the difference between `origin` and the
    ///                 minimum in all 3 dimensions.
    public static Cuboid fromPointsWithOrigin(final Vec3i startPos, final Vec3i endPos, final Vec3i origin)
    {
        final int min_x = Math.min(startPos.getX(), endPos.getX());
        final int min_y = Math.min(startPos.getY(), endPos.getY());
        final int min_z = Math.min(startPos.getZ(), endPos.getZ());
        final int max_x = Math.max(startPos.getX(), endPos.getX());
        final int max_y = Math.max(startPos.getY(), endPos.getY());
        final int max_z = Math.max(startPos.getZ(), endPos.getZ());
        
        final Vec3i pivot = new Vec3i((origin.getX() - min_x), (origin.getY() - min_y), (origin.getZ() - min_z));
        return new Cuboid(((max_x - min_x) + 1), ((max_y - min_y) + 1), ((max_z - min_z) + 1), pivot);
    }
    
    /// Create a new [Cuboid] from the given two positions and a pivot of \[0, 0, 0], where all positions between
    /// `startPos` and `endPos` (inclusive) are considered part of the cuboid.
    /// @param startPos The start position
    /// @param endPos   The end position
    public static Cuboid fromPoints(final Vec3i startPos, final Vec3i endPos)
    {
        return Cuboid.fromPoints(startPos, endPos, Vec3i.ZERO);
    }
    
    /// Create a new [Cuboid] from the given two positions and the given pivot, where all positions between
    /// `startPos` and `endPos` (inclusive) are considered part of the cuboid.
    /// @param startPos The start position
    /// @param endPos   The end position
    /// @param pivot    The pivot of the cuboid, relative to \[0, 0, 0] of the cuboid
    public static Cuboid fromPoints(final Vec3i startPos, final Vec3i endPos, final Vec3i pivot)
    {
        final int min_x = Math.min(startPos.getX(), endPos.getX());
        final int min_y = Math.min(startPos.getY(), endPos.getY());
        final int min_z = Math.min(startPos.getZ(), endPos.getZ());
        final int max_x = Math.max(startPos.getX(), endPos.getX());
        final int max_y = Math.max(startPos.getY(), endPos.getY());
        final int max_z = Math.max(startPos.getZ(), endPos.getZ());
        
        return new Cuboid(((max_x - min_x) + 1), ((max_y - min_y) + 1), ((max_z - min_z) + 1), pivot);
    }
    
    //******************************************************************************************************************
    /// @throws IllegalArgumentException If `xCount`, `yCount` or `zCount` are less or equal to zero
    public Cuboid
    {
        if (xCount <= 0)
        {
            throw new IllegalArgumentException("x dimension must be above zero");
        }
        
        if (yCount <= 0)
        {
            throw new IllegalArgumentException("y dimension must be above zero");
        }
        
        if (zCount <= 0)
        {
            throw new IllegalArgumentException("z dimension must be above zero");
        }
    }
    
    //==================================================================================================================
    /// Gets the [BlockPos] with the given ordinal.
    /// @param startPos The starting position the resulting [BlockPos] will be a relative of
    /// @param ordinal  The order of the block in the cuboid, starting with `0` for \[0, 0, 0]
    /// @return The new [BlockPos] or `null` if `ordinal` was out of bounds
    public @Nullable BlockPos getBlockPos(final BlockPos startPos, final int ordinal)
    {
        if (ordinal < 0 || ordinal >= this.size())
        {
            return null;
        }
        
        return this.getBlockPosUnchecked(startPos, ordinal);
    }
    
    /// Gets the [BlockPos] with the given ordinal, this will not check the cuboid's bounds and will overflow.
    /// @param startPos The starting position the resulting [BlockPos] will be a relative of
    /// @param ordinal  The order of the block in the cuboid, starting with `0` for \[0, 0, 0]
    /// @return The new [BlockPos]
    public BlockPos getBlockPosUnchecked(final BlockPos startPos, final int ordinal)
    {
        return startPos.offset(this.getRelativeUnchecked(ordinal));
    }
    
    public Vec3i getAbsoluteUnchecked(final Vec3i origin, final int ordinal)
    {
        return origin.offset(this.getRelativeUnchecked(ordinal));
    }
    
    public Vec3i getRelativeUnchecked(final int ordinal)
    {
        final int x_div = (ordinal / this.xCount);
        final int y_div = (x_div   / this.yCount);
        return new Vec3i(
            (ordinal - (x_div * this.xCount) - this.pivot.getX()),
            (x_div   - (y_div * this.yCount) - this.pivot.getY()),
            (y_div                           - this.pivot.getZ()));
    }
    
    public int size() { return (this.xCount * this.yCount * this.zCount); }
    
    //==================================================================================================================
    /// Computes all positions relative to `startPos` and caches them in a [Cached] object.
    /// @param startPos The relative position that maps to this cuboid's [#pivot()]`
    /// @return The new [Cached] object
    public Cached compute(final BlockPos startPos)
    {
        return new Cached(this.stream(startPos).toArray(BlockPos[]::new), this);
    }
    
    /// Streams all given positions relative to `startPos` in the cuboidal area and wraps them as a [BlockPos] object.
    /// @param startPos The relative position that maps to this cuboid's [#pivot()]`
    /// @return The [Stream] providing all [BlockPos] positions
    public Stream<BlockPos> stream(final BlockPos startPos)
    {
        return this.stream().map(startPos::offset);
    }
    
    /// Streams all given positions relative to this cuboid's [#pivot()] in the cuboidal area.
    /// @return The [Stream] providing all [Vec3i] positions
    public Stream<Vec3i> stream() { return IntStream.range(0, this.size()).mapToObj(this::getRelativeUnchecked); }
    
    /// Gives back a [CubeIterator] that can be used to iterate over all positions in this cuboid in relative
    /// coordinates.
    /// @return The [CubeIterator]
    @Override public Iterator<Vec3i> iterator() { return new CubeIterator(this, Vec3i.ZERO); }
    
    //==================================================================================================================
    public Cuboid expanded(final Direction direction, final int amount)
    {
        if (amount == 0)
        {
            return this;
        }
        
        final int x_count = Math.max(1, (this.xCount + Math.abs(direction.getStepX()) * amount));
        final int y_count = Math.max(1, (this.yCount + Math.abs(direction.getStepY()) * amount));
        final int z_count = Math.max(1, (this.zCount + Math.abs(direction.getStepZ()) * amount));
        
        final Vec3i pivot;
        
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
        {
            final int pivot_x = (x_count - this.xCount);
            final int pivot_y = (y_count - this.yCount);
            final int pivot_z = (z_count - this.zCount);
            pivot = this.pivot.offset(pivot_x, pivot_y, pivot_z);
        }
        else pivot = this.pivot;
        
        return new Cuboid(x_count, y_count, z_count, pivot);
    }
    
    public Cuboid reduced(final Direction direction, final int amount)
    {
        return this.expanded(direction, -amount);
    }
    
    public Cuboid moved(final Direction direction, final int amount)
    {
        final int pivot_x = (direction.getStepX() * amount);
        final int pivot_y = (direction.getStepY() * amount);
        final int pivot_z = (direction.getStepZ() * amount);
        return new Cuboid(this.xCount, this.yCount, this.zCount, this.pivot.offset(pivot_x, pivot_y, pivot_z));
    }
}
