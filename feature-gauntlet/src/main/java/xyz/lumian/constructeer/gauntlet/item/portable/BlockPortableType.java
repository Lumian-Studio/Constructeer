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
package xyz.lumian.constructeer.gauntlet.item.portable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletDataComponents;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.level.BlockContext;
import xyz.lumian.constructeer.level.Cuboid;
import xyz.lumian.constructeer.registry.CteerRegistryEvents;

import java.util.*;
import java.util.function.BiConsumer;



//**********************************************************************************************************************
public class BlockPortableType
    implements IPortableType<BlockState, BlockPortableType.BlockData>
{
    //******************************************************************************************************************
    public record BlockData(BlockState state, Optional<TypedEntityData<BlockEntityType<?>>> blockEntity)
    {
        //**************************************************************************************************************
        public static final MapCodec<BlockData> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(
                BlockState.CODEC
                    .fieldOf("block_state")
                    .forGetter(BlockData::state),
                TypedEntityData.codec(BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec())
                               .optionalFieldOf("block_entity")
                               .forGetter(BlockData::blockEntity))
            .apply(inst, BlockData::new));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), BlockData::state,
            (state -> new BlockData(state, Optional.empty())));
        
        //**************************************************************************************************************
        public static @Nullable BlockData of(final BlockContext block)
        {
            final BlockEntity be = block.getBlockEntity();
            
            if (be == null)
            {
                return new BlockData(block.state(), Optional.empty());
            }
            
            try (final var collector = new ProblemReporter.ScopedCollector(be.problemPath(), CteerDefine.LOGGER))
            {
                @SuppressWarnings("resource") final Level level = block.level();
                
                final TagValueOutput output = TagValueOutput.createWithContext(collector, level.registryAccess());
                be.saveWithId(output);
                
                if (!collector.isEmpty())
                {
                    return null;
                }
                
                return new BlockData(
                    block.state(),
                    Optional.of(TypedEntityData.of(be.getType(), output.buildResult())));
            }
        }
    }
    
    //******************************************************************************************************************
    private static final BlockStateRetentionManager RETENTION_MANAGER = new BlockStateRetentionManager();
    private static final Cuboid.Cached              FAIL_PLACE_AREA
        = new Cuboid(3, 2, 3, new Vec3i(1, 0, 1)).compute(BlockPos.ZERO);
    
    //==================================================================================================================
    static
    {
        addResetRules(List.of(
            BlockStateProperties.ATTACHED, BlockStateProperties.ATTACH_FACE, BlockStateProperties.AXIS,
            BlockStateProperties.BAMBOO_LEAVES, BlockStateProperties.BED_PART, BlockStateProperties.BELL_ATTACHMENT,
            BlockStateProperties.BLOOM, BlockStateProperties.BOTTOM, BlockStateProperties.CAN_SUMMON,
            BlockStateProperties.CHEST_TYPE, BlockStateProperties.CREAKING_HEART_STATE, BlockStateProperties.DISARMED,
            BlockStateProperties.DISTANCE, BlockStateProperties.DOOR_HINGE, BlockStateProperties.DOUBLE_BLOCK_HALF,
            BlockStateProperties.DOWN, BlockStateProperties.DRAG, BlockStateProperties.DRIPSTONE_THICKNESS,
            BlockStateProperties.DUSTED, BlockStateProperties.EAST, BlockStateProperties.EAST_REDSTONE,
            BlockStateProperties.EAST_WALL, BlockStateProperties.ENABLED, BlockStateProperties.EXTENDED,
            BlockStateProperties.FACING, BlockStateProperties.FACING_HOPPER, BlockStateProperties.FALLING,
            BlockStateProperties.HALF, BlockStateProperties.HANGING, BlockStateProperties.HORIZONTAL_AXIS,
            BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.IN_WALL, BlockStateProperties.LEVEL,
            BlockStateProperties.LEVEL_CAULDRON, BlockStateProperties.LEVEL_COMPOSTER,
            BlockStateProperties.LEVEL_FLOWING, BlockStateProperties.LIT, BlockStateProperties.MAP,
            BlockStateProperties.MOISTURE, BlockStateProperties.NATURAL, BlockStateProperties.NORTH,
            BlockStateProperties.NORTH_REDSTONE, BlockStateProperties.NORTH_WALL, BlockStateProperties.OCCUPIED,
            BlockStateProperties.OMINOUS, BlockStateProperties.OPEN, BlockStateProperties.ORIENTATION,
            BlockStateProperties.POWER, BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE,
            BlockStateProperties.RAIL_SHAPE_STRAIGHT, BlockStateProperties.ROTATION_16,
            BlockStateProperties.SCULK_SENSOR_PHASE, BlockStateProperties.SHORT, BlockStateProperties.SHRIEKING,
            BlockStateProperties.SIDE_CHAIN_PART, BlockStateProperties.SIGNAL_FIRE, BlockStateProperties.SLAB_TYPE,
            BlockStateProperties.SLOT_3_OCCUPIED, BlockStateProperties.SOUTH, BlockStateProperties.SOUTH_REDSTONE,
            BlockStateProperties.SOUTH_WALL, BlockStateProperties.STABILITY_DISTANCE, BlockStateProperties.STAGE,
            BlockStateProperties.STAIRS_SHAPE, BlockStateProperties.TILT, BlockStateProperties.TIP,
            BlockStateProperties.TRIGGERED, BlockStateProperties.UP, BlockStateProperties.VERTICAL_DIRECTION,
            BlockStateProperties.WATERLOGGED, BlockStateProperties.WEST, BlockStateProperties.WEST_REDSTONE,
            BlockStateProperties.WEST_WALL
        ));
    }
    
    //******************************************************************************************************************
    /// Adds a block state rule that defines what properties should not be retained upon picking a block up or placing
    /// it down. This should only be used for custom mod properties, otherwise it will change how minecraft or other mod
    /// blocks behave.
    /// @param properties The properties to reset
    public static void addResetRules(final Collection<Property<?>> properties)
    {
        BlockPortableType.RETENTION_MANAGER.addResetRules(null, properties);
    }
    
    /// Adds a block state rule that defines what properties should not be retained upon picking a block up or placing
    /// it down.
    /// Other than [#addNonRetainedBlockStateProperty(Collection)], this will only apply to specific blocks.
    /// @param block      The [Block] to add the reset rules for
    /// @param properties The properties to reset
    /// @throws IllegalArgumentException If a rule for a Minecraft block is made or adds a property that the
    ///                                  given block's state definition does not have
    public static void addBlockResetRules(final Block block, final Collection<Property<?>> properties)
    {
        BlockPortableType.addRule(BlockPortableType.RETENTION_MANAGER::addResetRules, block, properties);
    }
    
    /// Adds a block state exemption rule that defines what properties should be retained even though a more generalised
    /// reset rule has been applied.
    /// @param block      The [Block] to add the retention rules for
    /// @param properties The properties to retain
    /// @throws IllegalArgumentException If a rule for a Minecraft block is made or adds a property that the
    ///                                  given block's state definition does not have
    public static void addBlockRetentionRules(final Block block, final Collection<Property<?>> properties)
    {
        BlockPortableType.addRule(BlockPortableType.RETENTION_MANAGER::addRetentionRules, block, properties);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static void addRule(final BiConsumer<Block, Collection<Property<?>>> function,
                                final Block block, final Collection<Property<?>> properties)
    {
        final Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        
        if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE))
        {
            throw new IllegalArgumentException("rules must not be added for the default namespace");
        }
        
        final Collection<Property<?>> def_props = block.getStateDefinition().getProperties();
        
        for (final var property : properties)
        {
            if (!def_props.contains(property))
            {
                throw new IllegalArgumentException(
                    "block state definition for block '" + id + "' cannot have property '" + property.getName() + "'");
            }
        }
        
        function.accept(block, properties);
    }
    
    //==================================================================================================================
    private static @Nullable BlockEntity tryLoadBlockEntity(final TypedEntityData<BlockEntityType<?>> data,
                                                            final Level level, final BlockState state,
                                                            final BlockPos pos)
    {
        if (!level.isClientSide())
        {
            final BlockEntity be = data.type().create(pos, state);
            
            if (!data.loadInto(be, level.registryAccess()))
            {
                return null;
            }
            
            return be;
        }
        
        return null;
    }
    
    //==================================================================================================================
    private static boolean canPlace(final Player player, final BlockPos pos)
    {
        @SuppressWarnings("resource")
        final Level level = player.level();
        return (
            level.getBlockState(pos).canBeReplaced()
            && level
                .getEntities(
                    (Entity) null,
                    AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)),
                    (entity -> entity.getType() != EntityType.ITEM))
                .isEmpty()
        );
    }
    
    //******************************************************************************************************************
    BlockPortableType()
    {
        ItemEvents.USE_ON.register(ctx ->
        {
            final Player player = ctx.getPlayer();
            
            if (player == null)
            {
                return null;
            }
            
            final BlockPos pos = ctx.getClickedPos();
            return Portable.tryPickup(ctx.getItemInHand(), player, pos, this, ctx.getLevel().getBlockState(pos));
        });
        
        Compat.getAccessory().ifPresent(integration -> BlockEvents.USE_WITHOUT_ITEM
            .register((state, level, pos, player, hitResult) ->
            {
                final ItemStack stack = integration
                    .getFirstMatchingItem(
                        player,
                        IAccessory.SlotConstants.HAND,
                        (stack1 ->  stack1.has(CteerGauntletDataComponents.GRABBER)),
                        false)
                    .orElse(ItemStack.EMPTY);
                return Portable.tryPickup(stack, player, pos, this, level.getBlockState(pos));
            }));
        
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_AFTER.register(BlockPortableType.RETENTION_MANAGER::bake);
    }
    
    //==================================================================================================================
    @Override
    public boolean canTake(final BlockState block, final Player player, final BlockPos pos)
    {
        return (block.getDestroySpeed(player, pos) >= 0f);
    }
    
    //==================================================================================================================
    @Override public MapCodec<BlockData>                             codec()       { return BlockData.MAP_CODEC; }
    @Override public StreamCodec<RegistryFriendlyByteBuf, BlockData> streamCodec() { return BlockData.STREAM_CODEC; }
    
    //==================================================================================================================
    @Override
    public @Nullable BlockData pickup(final BlockState state, final Player player, final BlockPos pos)
    {
        if (!BlockPortableType.canPlace(player, pos))
        {
            return null;
        }
        
        final Level     level = player.level();
        final BlockData data  = BlockData.of(new BlockContext(level, state, pos));

        if (data == null)
        {
            return null;
        }
        
        level.setBlock(pos, level.getFluidState(pos).createLegacyBlock(),
                       (Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS));
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        
        return data;
    }
    
    @Override
    public InteractionResult place(final BlockData block, final Player player, final BlockPos clickPos,
                                   final BlockPlaceContext context)
    {
        final Level       level = player.level();
        final BlockEntity be;
        
        BlockState state = block.state();
        
        if (block.blockEntity.isPresent())
        {
            be = BlockPortableType.tryLoadBlockEntity(block.blockEntity().orElseThrow(), level, block.state(),
                                                      context.getClickedPos());
            
            if (be == null)
            {
                return InteractionResult.FAIL;
            }
            
            state = be.getBlockState();
        }
        else be = null;
        
        final BlockState placement_state = state.getBlock().getStateForPlacement(context);
        
        if (placement_state != null)
        {
            state = BlockPortableType.RETENTION_MANAGER.updateState(state, placement_state);
        }
        
        if (!state.canSurvive(level, context.getClickedPos()))
        {
            return InteractionResult.PASS;
        }
        
        level.setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE);
        level.gameEvent(GameEvent.BLOCK_PLACE, context.getClickedPos(), GameEvent.Context.of(player, block.state()));
        
        if (be != null)
        {
            level.setBlockEntity(be);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void discard(final BlockData block, final Player player)
    {
        final List<BlockPos> posses = new ArrayList<>(BlockPortableType.FAIL_PLACE_AREA);
        
        if (player.isAlive())
        {
            posses.removeIf(pos -> (pos.equals(BlockPos.ZERO) || pos.equals(BlockPos.ZERO.above())));
        }
        
        final BlockPos                 player_pos = player.blockPosition();
        final BlockPos.MutableBlockPos place_pos  = new BlockPos.MutableBlockPos();
        
        for (final var off : BlockPortableType.FAIL_PLACE_AREA)
        {
            place_pos.setWithOffset(player_pos, off);
            
            if (BlockPortableType.canPlace(player, place_pos))
            {
                final BlockHitResult res = new BlockHitResult(new Vec3(place_pos), Direction.DOWN, place_pos, false);
                this.place(
                    block,
                    player,
                    place_pos,
                    new BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack.EMPTY, res));
                
                break;
            }
        }
    }
}
