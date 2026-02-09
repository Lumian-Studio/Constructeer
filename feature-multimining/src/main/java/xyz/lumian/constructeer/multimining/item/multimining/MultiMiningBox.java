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
package xyz.lumian.constructeer.multimining.item.multimining;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xyz.lumian.constructeer.level.BlockContext;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;



//**********************************************************************************************************************
public record MultiMiningBox(List<Part> parts, Vec3 base, int baseWidth, int baseDepth)
{
    //******************************************************************************************************************
    public record Part(BlockState state, Vec3i offset, List<ItemStack> drops)
    {
        //**************************************************************************************************************
        public static final Codec<Part> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                BlockState.CODEC
                    .fieldOf("block_state")
                    .forGetter(Part::state),
                Vec3i.CODEC
                    .fieldOf("offset")
                    .forGetter(Part::offset),
                ItemStack.CODEC.listOf()
                    .fieldOf("drops")
                    .forGetter(Part::drops))
            .apply(inst, Part::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Part> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), Part::state,
            Vec3i.STREAM_CODEC,                                 Part::offset,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), Part::drops,
            Part::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, Part> STREAM_RENDER_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), Part::state,
            Vec3i.STREAM_CODEC,                                 Part::offset,
            ((state, pos) -> new Part(state, pos, List.of())));
    }
    
    //******************************************************************************************************************
    public static final Codec<MultiMiningBox> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            Part.CODEC.listOf()
                .fieldOf("parts")
                .forGetter(MultiMiningBox::parts),
            Vec3.CODEC
                .fieldOf("base")
                .forGetter(MultiMiningBox::base),
            Codec.INT
                .fieldOf("base_width")
                .forGetter(MultiMiningBox::baseWidth),
            Codec.INT
                .fieldOf("base_depth")
                .forGetter(MultiMiningBox::baseDepth))
        .apply(inst, MultiMiningBox::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMiningBox> STREAM_CODEC = StreamCodec.composite(
        Part         .STREAM_CODEC.apply(ByteBufCodecs.list()), MultiMiningBox::parts,
        Vec3         .STREAM_CODEC,                             MultiMiningBox::base,
        ByteBufCodecs.VAR_INT,                                  MultiMiningBox::baseWidth,
        ByteBufCodecs.VAR_INT,                                  MultiMiningBox::baseDepth,
        MultiMiningBox::new);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMiningBox> STREAM_RENDER_CODEC = StreamCodec
        .composite(
            Part         .STREAM_RENDER_CODEC.apply(ByteBufCodecs.list()), MultiMiningBox::parts,
            Vec3         .STREAM_CODEC,                                    MultiMiningBox::base,
            ByteBufCodecs.VAR_INT,                                         MultiMiningBox::baseWidth,
            ByteBufCodecs.VAR_INT,                                         MultiMiningBox::baseDepth,
            MultiMiningBox::new);
    
    //******************************************************************************************************************
    public static MultiMiningBox create(final Collection<BlockContext>                blocks,
                                        final Function<BlockContext, List<ItemStack>> dropCollector,
                                        final BlockContext                            startBlock,
                                        final Predicate<BlockState>                   validBaseBlockPredicate)
    {
        final List<Part> parts = blocks.stream()
            .map(block -> new Part(block.state(), block.pos().subtract(startBlock.pos()), dropCollector.apply(block)))
            .sorted(Comparator.comparingInt(part -> part.offset.getY()))
            .collect(Collectors.toList());
        
        int base_min_x = Integer.MAX_VALUE;
        int base_min_z = Integer.MAX_VALUE;
        int base_max_x = Integer.MIN_VALUE;
        int base_max_z = Integer.MIN_VALUE;
        int min_y      = Integer.MAX_VALUE;
        
        for (final var part : parts)
        {
            if (part.offset.getY() > min_y)
            {
                break;
            }
            
            if (validBaseBlockPredicate.test(part.state))
            {
                min_y      = part.offset.getY();
                base_min_x = Math.min(base_min_x, part.offset.getX());
                base_min_z = Math.min(base_min_z, part.offset.getZ());
                base_max_x = Math.max(base_max_x, part.offset.getX());
                base_max_z = Math.max(base_max_z, part.offset.getZ());
            }
        }
        
        final AABB base = new AABB(base_min_x, min_y, base_min_z, (base_max_x + 1), (min_y + 1), (base_max_z + 1));
        return new MultiMiningBox(parts, base.getBottomCenter(), (int) base.getXsize(), (int) base.getZsize());
    }
    
    //******************************************************************************************************************
    public MultiMiningBox() { this(List.of(), Vec3.ZERO, 0, 0); }
}
