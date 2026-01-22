package xyz.lumian.constructeer.item.multimining;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;



//**********************************************************************************************************************
public record MultiMiningBox(
    List<Part>      parts,
    BoundingBox     box,
    BlockPos        startPos,
    int             baseWidth,
    int             baseDepth
)
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
            BoundingBox.CODEC
                .fieldOf("bounding_box")
                .forGetter(MultiMiningBox::box),
            BlockPos.CODEC
                .fieldOf("start_pos")
                .forGetter(MultiMiningBox::startPos),
            Codec.INT
                .fieldOf("base_width")
                .forGetter(MultiMiningBox::baseWidth),
            Codec.INT
                .fieldOf("base_depth")
                .forGetter(MultiMiningBox::baseDepth))
        .apply(inst, MultiMiningBox::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMiningBox> STREAM_CODEC = StreamCodec.composite(
        Part         .STREAM_CODEC.apply(ByteBufCodecs.list()), MultiMiningBox::parts,
        BoundingBox  .STREAM_CODEC,                             MultiMiningBox::box,
        BlockPos     .STREAM_CODEC,                             MultiMiningBox::startPos,
        ByteBufCodecs.VAR_INT,                                  MultiMiningBox::baseWidth,
        ByteBufCodecs.VAR_INT,                                  MultiMiningBox::baseDepth,
        MultiMiningBox::new);
    
    //******************************************************************************************************************
    public static MultiMiningBox create(final Collection<BlockContext>                blocks,
                                        final Function<BlockContext, List<ItemStack>> dropCollector,
                                        final BlockContext                            startBlock)
    {
        int min_x      = Integer.MAX_VALUE;
        int min_y      = Integer.MAX_VALUE;
        int min_z      = Integer.MAX_VALUE;
        int max_x      = Integer.MIN_VALUE;
        int max_y      = Integer.MIN_VALUE;
        int max_z      = Integer.MIN_VALUE;
        int base_min_x = Integer.MAX_VALUE;
        int base_min_z = Integer.MAX_VALUE;
        int base_max_x = Integer.MIN_VALUE;
        int base_max_z = Integer.MIN_VALUE;
        
        final List<Part> parts = new ArrayList<>(blocks.size());
        
        for (final var block : blocks)
        {
            final BlockPos pos   = block.pos();
            final int      old_y = min_y;
            
            min_x = Math.min(min_x, pos.getX());
            min_y = Math.min(min_y, pos.getY());
            min_z = Math.min(min_z, pos.getZ());
            max_x = Math.max(max_x, pos.getX());
            max_y = Math.max(max_y, pos.getY());
            max_z = Math.max(max_z, pos.getZ());
            
            if (min_y == old_y)
            {
                base_min_x = Math.min(base_min_x, pos.getX());
                base_min_z = Math.min(base_min_z, pos.getZ());
                base_max_x = Math.max(base_max_x, pos.getX());
                base_max_z = Math.max(base_max_z, pos.getZ());
            }
            else if (min_y < old_y)
            {
                base_min_x = pos.getX();
                base_min_z = pos.getZ();
                base_max_x = pos.getX();
                base_max_z = pos.getY();
            }
            
            parts.add(new Part(block.state(), block.pos().subtract(startBlock.pos()), dropCollector.apply(block)));
        }
        
        final BoundingBox box   = new BoundingBox(min_x, min_y, min_z, max_x, max_y, max_z);
        final int         width = (base_max_x - base_min_x + 1);
        final int         depth = (base_max_z - base_min_z + 1);
        return new MultiMiningBox(parts, box, startBlock.pos(), width, depth);
    }
    
    //******************************************************************************************************************
    public MultiMiningBox() { this(List.of(), new BoundingBox(0, 0, 0, 0, 0, 0), BlockPos.ZERO, 0, 0); }
}
