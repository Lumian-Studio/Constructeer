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

import net.minecraft.core.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

import java.util.Objects;



//**********************************************************************************************************************
public class BlockContext
{
    //******************************************************************************************************************
    public static BlockContext forLevel(final Level level, final BlockPos pos)
    {
        return new BlockContext(level, level.getBlockState(pos), pos);
    }
    
    public static BlockContext forPlayer(final Player player, final BlockPos pos)
    {
        return BlockContext.forLevel(player.level(), pos);
    }
    
    //******************************************************************************************************************
    private final Level       level;
    private final BlockPos    pos;
    private       BlockState  state;
    
    //******************************************************************************************************************
    public BlockContext(final Level level, final BlockState state, final BlockPos pos)
    {
        this.level  = Objects.requireNonNull(level, "level must not be null");
        this.pos    = Objects.requireNonNull(pos,   "pos must not be null");
        this.state  = Objects.requireNonNull(state, "state must not be null");
    }
    
    //==================================================================================================================
    public BlockState state() { return this.state; }
    public BlockPos   pos()   { return this.pos;   }
    public Level      level() { return this.level; }
    
    @Nullable public BlockEntity getBlockEntity() { return this.level.getBlockEntity(this.pos); }
    
    public float getHardness() { return this.state.getDestroySpeed(this.level, this.pos); }
    
    public LevelChunk getChunk()
    {
        final int x_section = SectionPos.blockToSectionCoord(this.pos.getX());
        final int z_section = SectionPos.blockToSectionCoord(this.pos.getZ());
        return (LevelChunk) this.level.getChunk(x_section, z_section, ChunkStatus.FULL, false);
    }
    
    //==================================================================================================================
    public boolean hasChunk() { return !this.getChunk().isEmpty(); }
    
    public boolean isStale() { return !this.is(this.level.getBlockState(this.pos)); }
    
    //==================================================================================================================
    public BlockContext neighbour(final Direction direction, final int distance)
    {
        if (distance == 0)
        {
            return this;
        }
        
        return BlockContext.forLevel(this.level, this.pos.relative(direction, distance));
    }
    
    public BlockContext neighbour(final Direction direction) { return this.neighbour(direction, 1); }
    
    public BlockContext relative(final Vec3i offset)
    {
        if (offset.equals(Vec3i.ZERO))
        {
            return this;
        }
        
        return BlockContext.forLevel(this.level, this.pos.offset(offset));
    }
    
    public BlockContext withPos(final BlockPos newPos)
    {
        if (newPos.equals(this.pos))
        {
            return this;
        }
        
        return BlockContext.forLevel(this.level, newPos);
    }
    
    //==================================================================================================================
    public boolean set(final BlockState state, final @Block.UpdateFlags int flags, final int recursionLeft)
    {
        if (this.level.setBlock(this.pos, state, flags, recursionLeft))
        {
            this.state = state;
            return true;
        }
        
        return false;
    }
    
    public boolean set(final BlockState state, final @Block.UpdateFlags int flags)
    {
        if (this.level.setBlock(this.pos, state, flags))
        {
            this.state = state;
            return true;
        }
        
        return false;
    }
    
    public boolean setAndUpdate(final BlockState state)
    {
        if (this.level.setBlockAndUpdate(this.pos, state))
        {
            this.state = state;
            return true;
        }
        
        return false;
    }
    
    public boolean destroy(final boolean shouldDrop, final @Nullable Entity entity, final int recursionLeft)
    {
        if (this.level.destroyBlock(this.pos, shouldDrop, entity, recursionLeft))
        {
            return this.update();
        }
        
        return false;
    }
    
    public boolean destroy(final boolean shouldDrop, final @Nullable Entity entity)
    {
        if (this.level.destroyBlock(this.pos, shouldDrop, entity))
        {
            return this.update();
        }
        
        return false;
    }
    
    public boolean destroy(final boolean shouldDrop) { return this.destroy(shouldDrop, null); }
    
    public boolean update()
    {
        final BlockState old_state = this.state;
        this.state = this.level.getBlockState(this.pos);
        return (old_state != this.state);
    }
    
    //==================================================================================================================
    public boolean is(final TagKey<Block>    tag)    { return this.state.is(tag);    }
    public boolean is(final Block            block)  { return this.state.is(block);  }
    public boolean is(final Holder<Block>    block)  { return this.state.is(block);  }
    public boolean is(final HolderSet<Block> blocks) { return this.state.is(blocks); }
    public boolean is(final BlockState       state)  { return (this.state == state); }
    
    //==================================================================================================================
    public boolean isSolid() { return this.state.isRedstoneConductor(this.level, this.pos); }
    
    //==================================================================================================================
    @Override
    public boolean equals(final @Nullable Object obj)
    {
        if (obj == this)                          return true;
        if (!(obj instanceof BlockContext other)) return false;
        return (this.pos.equals(other.pos) && this.level == other.level);
    }
    
    @Override public int hashCode() { return Objects.hash(this.pos, this.level); }
}
