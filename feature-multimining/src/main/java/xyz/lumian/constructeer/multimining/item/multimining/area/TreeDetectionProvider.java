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

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.level.BlockContext;
import xyz.lumian.constructeer.util.CodecUtil;
import xyz.lumian.constructeer.level.Cuboid;

import java.util.*;



//**********************************************************************************************************************
/// A provider that detects trees from a given log block by scanning upwards, trying to find more logs and leaves that
/// are attached to the stem.
///
/// Log blocks only account for the tree if they are part of the main stem (in a 3x2x3 around another log) or are
/// neighbouring a leaf block that is already considered part of the main stem.
///
/// To determine whether leaves are part of the main stem, two conditions have to hold true:
/// 1. Only blocks that have [BlockStateProperties#PERSISTENT] set and set to `false`
/// 2. Its [BlockStateProperties#DISTANCE] (measured in taxicab distance) has to be incrementally exact to the previous
/// leaf block already part of the main stem. If a leaf block is right next to a log block and has this value as `1`,
/// it is considered the root leaf block and part of the main stem, from there the distance will increase by one and
/// check whether the next leaf has the incremented distance, if yes, add it, otherwise ignore it. The maximum distance
/// is determined through `maximumLeafDistance`.
///
/// This provider uses the return value of the output to determine whether any more blocks are requested, if the
/// output returns `false`, no more blocks will be scanned.
///
/// The return value of the provider method determines whether the operation upon ending determined that the scanned
/// area is in fact a valid tree, which only holds true if there is at least one log, one associated leaf block,
/// and the count of the blocks is less or equal to [#maxBlockCount] (or [#stopIfExceedingMaximum] is `false`) in which
/// case it will return [xyz.lumian.constructeer.multimining.item.multimining.area.IAreaProvider.Result#SUCCESS]. However, if the
/// tree is a valid tree but [#maxBlockCount] is exceeded and [#stopIfExceedingMaximum] is `true`, then this will
/// return [xyz.lumian.constructeer.multimining.item.multimining.area.IAreaProvider.Result#PASS],
/// otherwise [xyz.lumian.constructeer.multimining.item.multimining.area.IAreaProvider.Result#FAILED].
public class TreeDetectionProvider
    implements IAreaProvider
{
    //******************************************************************************************************************
    public static final int       DEFAULT_MAX_LEAF_DISTANCE         = 7;
    public static final int       DEFAULT_MAX_BLOCK_COUNT           = 500;
    public static final int       DEFAULT_MIN_LEAVES_COUNT          = 1;
    public static final boolean   DEFAULT_SCAN_DOWNWARDS            = false;
    public static final boolean   DEFAULT_STOP_IF_EXCEEDING_MAXIMUM = true;
    public static final boolean   DEFAULT_SCAN_DISJOINTED_LOGS      = false;
    public static final SneakMode DEFAULT_SNEAK_MODE                = SneakMode.NONE;
    
    public static final MapCodec<TreeDetectionProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            RegistryCodecs.homogeneousList(Registries.BLOCK)
                .fieldOf("validStemBlocks")
                .forGetter(prov -> prov.validStemBlocks),
            Codec.intRange(1, 7)
                .optionalFieldOf("maxLeafDistance", TreeDetectionProvider.DEFAULT_MAX_LEAF_DISTANCE)
                .forGetter(prov -> prov.maxLeafDistance),
            Codec.intRange(0, Integer.MAX_VALUE)
                 .optionalFieldOf("maxBlockCount", TreeDetectionProvider.DEFAULT_MAX_BLOCK_COUNT)
                 .forGetter(prov -> prov.maxBlockCount),
            Codec.intRange(0, Integer.MAX_VALUE)
                 .optionalFieldOf("minLeavesCount", TreeDetectionProvider.DEFAULT_MIN_LEAVES_COUNT)
                 .forGetter(prov -> prov.minLeavesCount),
            Codec.BOOL
                .optionalFieldOf("scanDownwards", TreeDetectionProvider.DEFAULT_SCAN_DOWNWARDS)
                .forGetter(prov -> prov.scanDownwards),
            Codec.BOOL
                .optionalFieldOf("stopIfExceedingMaximum", TreeDetectionProvider.DEFAULT_STOP_IF_EXCEEDING_MAXIMUM)
                .forGetter(prov -> prov.stopIfExceedingMaximum),
            Codec.BOOL
                .optionalFieldOf("scanDisjointedLogs", TreeDetectionProvider.DEFAULT_SCAN_DISJOINTED_LOGS)
                .forGetter(prov -> prov.scanDisjointedLogs),
            SneakMode.CODEC
                .optionalFieldOf("sneakMode", TreeDetectionProvider.DEFAULT_SNEAK_MODE)
                .forGetter(prov -> prov.sneakMode))
        .apply(instance, TreeDetectionProvider::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, TreeDetectionProvider> STREAM_CODEC = StreamCodec
        .composite(
            ByteBufCodecs.holderSet(Registries.BLOCK),                  (prov -> prov.validStemBlocks),
            ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue), (prov -> prov.maxLeafDistance),
            ByteBufCodecs.VAR_INT,                                      (prov -> prov.maxBlockCount),
            ByteBufCodecs.VAR_INT,                                      (prov -> prov.minLeavesCount),
            ByteBufCodecs.BOOL,                                         (prov -> prov.scanDownwards),
            ByteBufCodecs.BOOL,                                         (prov -> prov.stopIfExceedingMaximum),
            ByteBufCodecs.BOOL,                                         (prov -> prov.scanDisjointedLogs),
            CodecUtil.smallEnum(SneakMode.values()),                    (prov -> prov.sneakMode),
            TreeDetectionProvider::new);
    
    //------------------------------------------------------------------------------------------------------------------
    private static final List<BlockPos> STEM_SCAN_AREA;
    
    //==================================================================================================================
    static
    {
        final List<BlockPos> posses = (new Cuboid(3, 3, 3, new Vec3i(1, 1, 1))).compute(BlockPos.ZERO);
        posses.removeIf(pos ->
            pos.equals(BlockPos.ZERO)
            || Arrays.stream(Direction.values()).anyMatch(dir -> pos.equals(dir.getUnitVec3i()))
        );
        STEM_SCAN_AREA = ImmutableList.copyOf(posses);
    }
    
    //******************************************************************************************************************
    /// A [HolderSet] with all blocks that should be considered valid stem blocks.
    public final HolderSet<Block> validStemBlocks;
    
    /// The maximum distance value a leaf block can have at which it won't scan for further leaves or logs.
    /// Cannot be less than 1.
    public final int maxLeafDistance;
    
    /// The maximum block count that is allowed to be collected, everything after that will be ignored.
    public final int maxBlockCount;
    
    /// The minimum number of leaf blocks required to count as a tree.
    public final int minLeavesCount;
    
    /// Whether logs should be scanned below the initial log block.
    public final boolean scanDownwards;
    
    /// Whether the tree should not be considered a tree if the scanned blocks exceed [#maxBlockCount].
    public final boolean stopIfExceedingMaximum;
    
    /// Whether logs should be still considered part of the tree even if there is one leaf block between.
    public final boolean scanDisjointedLogs;
    
    /// Determines how sneaking affects the provider.
    public final SneakMode sneakMode;
    
    //******************************************************************************************************************
    /// Creates a new [TreeDetectionProvider] instance.
    /// @param validStemBlocks        [#validStemBlocks]
    /// @param maxLeafDistance        [#maxLeafDistance]
    /// @param maxBlockCount          [#maxBlockCount]
    /// @param minLeavesCount         [#minLeavesCount]
    /// @param scanDownwards          [#scanDownwards]
    /// @param stopIfExceedingMaximum [#stopIfExceedingMaximum]
    /// @param scanDisjointedLogs     [#scanDisjointedLogs]
    /// @param sneakMode              [#sneakMode]
    public TreeDetectionProvider(
        final HolderSet<Block> validStemBlocks,
        final int              maxLeafDistance,
        final int              maxBlockCount,
        final int              minLeavesCount,
        final boolean          scanDownwards,
        final boolean          stopIfExceedingMaximum,
        final boolean          scanDisjointedLogs,
        final SneakMode        sneakMode
    )
    {
        if (maxLeafDistance < 1)
        {
            throw new IllegalArgumentException("maximum leaf distance may not be less than 1");
        }
        
        if (minLeavesCount < 0)
        {
            throw new IllegalArgumentException("minimum leaf count must be 0 or above");
        }
        
        if (maxBlockCount < 0)
        {
            throw new IllegalArgumentException("maximum block count may not be less than 0");
        }
        
        this.validStemBlocks        = validStemBlocks;
        this.maxLeafDistance        = maxLeafDistance;
        this.maxBlockCount          = maxBlockCount;
        this.minLeavesCount         = minLeavesCount;
        this.scanDownwards          = scanDownwards;
        this.stopIfExceedingMaximum = stopIfExceedingMaximum;
        this.scanDisjointedLogs     = scanDisjointedLogs;
        this.sneakMode              = sneakMode;
    }
    
    /// Creates a defaulted [TreeDetectionProvider] instance.
    /// @param holderGetter The [HolderGetter] which is needed if tags have not been loaded yet
    public TreeDetectionProvider(final HolderGetter<Block> holderGetter)
    {
        this(holderGetter.getOrThrow(BlockTags.LOGS),
             TreeDetectionProvider.DEFAULT_MAX_LEAF_DISTANCE,
             TreeDetectionProvider.DEFAULT_MAX_BLOCK_COUNT,
             TreeDetectionProvider.DEFAULT_MIN_LEAVES_COUNT,
             TreeDetectionProvider.DEFAULT_SCAN_DOWNWARDS,
             TreeDetectionProvider.DEFAULT_STOP_IF_EXCEEDING_MAXIMUM,
             TreeDetectionProvider.DEFAULT_SCAN_DISJOINTED_LOGS,
             TreeDetectionProvider.DEFAULT_SNEAK_MODE);
    }
    
    //==================================================================================================================
    @Override public AreaProviderType<? extends IAreaProvider> type() { return AreaProviderType.TREE_DETECTION; }
    
    //==================================================================================================================
    @Override
    public Result provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                          final Output output)
    {
        if (player.isCrouching() && this.sneakMode == SneakMode.VANILLA)
        {
            return Result.FAILED;
        }
        
        if (!block.is(this.validStemBlocks) || this.maxBlockCount < (this.minLeavesCount + 1))
        {
            return Result.FAILED;
        }
        
        final Set<BlockPos> visited = new HashSet<>(32);
        visited.add(block.pos());
        
        final Deque<BlockContext> nodes = new ArrayDeque<>(16);
        nodes.addLast(block);
        
        final ObjectArrayFIFOQueue<BlockContext> leaf_nodes = new ObjectArrayFIFOQueue<>(16);
        
        int count      = 0;
        int leaf_count = 0;
        
        while (!nodes.isEmpty())
        {
            if (count == this.maxBlockCount)
            {
                if (this.stopIfExceedingMaximum)
                {
                    return Result.PASS;
                }
                
                return (leaf_count >= this.minLeavesCount ? Result.SUCCESS : Result.FAILED);
            }
            
            final BlockContext log = nodes.pollFirst();
            
            if (!output.acceptAndTest(log))
            {
                return (leaf_count >= this.minLeavesCount ? Result.SUCCESS : Result.FAILED);
            }
            
            ++count;
            
            final BlockPos.MutableBlockPos log_pos    = log.pos().mutable();
            final boolean                  add_leaves = (!player.isCrouching() || this.sneakMode != SneakMode.WEAK);
            
            // Scan further logs in all non-cardinal directions
            for (final var pos : TreeDetectionProvider.STEM_SCAN_AREA)
            {
                log_pos.setWithOffset(log.pos(), pos);
                
                if (!this.scanDownwards && log_pos.getY() < block.pos().getY())
                {
                    continue;
                }
                
                if (visited.contains(log_pos))
                {
                    continue;
                }
                
                final BlockContext log_block = log.relative(pos);
                
                if (log_block.is(this.validStemBlocks))
                {
                    visited.add(log_block.pos());
                    nodes.addLast(log_block);
                }
            }
            
            // Scan for leaves stuff and logs in all cardinal directions
            for (final var log_dir : Direction.values())
            {
                log_pos.setWithOffset(log.pos(), log_dir.getUnitVec3i());
                
                if (visited.contains(log_pos))
                {
                    continue;
                }
                
                final BlockContext test_block = log.withPos(log_pos);
                
                // Check if persistent is false
                if (!test_block.state().getValueOrElse(BlockStateProperties.PERSISTENT, true))
                {
                    final int main_distance = test_block.state().getValueOrElse(BlockStateProperties.DISTANCE, 0);
                    
                    // leaf is directly connected
                    if (main_distance == 1)
                    {
                        leaf_nodes.enqueueFirst(test_block);
                        
                        while (!leaf_nodes.isEmpty())
                        {
                            final BlockContext leaf     = leaf_nodes.dequeue();
                            final int          distance = leaf.state().getValue(BlockStateProperties.DISTANCE);
                            
                            if (distance > this.maxLeafDistance)
                            {
                                continue;
                            }
                            
                            if (count == this.maxBlockCount)
                            {
                                if (this.stopIfExceedingMaximum)
                                {
                                    return Result.PASS;
                                }
                                
                                return (leaf_count >= this.minLeavesCount ? Result.SUCCESS : Result.FAILED);
                            }
                            
                            if (add_leaves && !output.acceptAndTest(leaf))
                            {
                                return (leaf_count >= this.minLeavesCount ? Result.SUCCESS : Result.FAILED);
                            }
                            
                            ++leaf_count;
                            ++count;
                            
                            final BlockPos.MutableBlockPos leaf_pos = leaf.pos().mutable();
                            
                            for (final var node_dir : Direction.values())
                            {
                                leaf_pos.setWithOffset(leaf.pos(), node_dir);
                                
                                if (visited.contains(leaf_pos))
                                {
                                    continue;
                                }
                                
                                final BlockContext leaf_block = log.withPos(leaf_pos);
                                
                                if (
                                    add_leaves
                                    && !leaf_block.state().getValueOrElse(BlockStateProperties.PERSISTENT, true)
                                )
                                {
                                    final int leaf_distance = leaf_block.state()
                                        .getValueOrElse(BlockStateProperties.DISTANCE, 0);
                                    
                                    if (leaf_distance == (distance + 1))
                                    {
                                        leaf_nodes.enqueueFirst(leaf_block);
                                    }
                                    else if (leaf_distance > 0)
                                    {
                                        continue;
                                    }
                                }
                                else if (this.scanDisjointedLogs && leaf_block.is(this.validStemBlocks))
                                {
                                    nodes.addLast(leaf_block);
                                }
                                
                                visited.add(leaf_block.pos());
                            }
                        }
                    }
                    
                    // distance value is greater than 1, we leave it to be scheduled for later
                    else if (main_distance > 0)
                    {
                        continue;
                    }
                }
                else if (
                    (log_pos.getY() >= block.pos().getY() || this.scanDownwards)
                    && test_block.is(this.validStemBlocks)
                )
                {
                    nodes.addLast(test_block);
                }
                
                visited.add(test_block.pos());
            }
        }
        
        return (leaf_count >= this.minLeavesCount ? Result.SUCCESS : Result.FAILED);
    }
}
