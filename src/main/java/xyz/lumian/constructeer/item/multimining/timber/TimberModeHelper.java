package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.util.SemanticContract;

import java.util.List;
import java.util.Optional;



//**********************************************************************************************************************
public final class TimberModeHelper
{
    //******************************************************************************************************************
    @SemanticContract.Server
    public static void mined(final Player player, final @SemanticContract.Copied ItemStack stack,
                             final BlockState state, final BlockPos pos, final boolean doDropsIfEligible)
    {
        @SuppressWarnings("resource")
        final BlockEntity entity = player.level().getBlockEntity(pos);
        TimberModeHelper.mined(player, stack, stack.get(DataComponents.TOOL), state, pos, entity, doDropsIfEligible);
    }
    
    @SemanticContract.Server
    public static void mined(final Player player, final @SemanticContract.Copied ItemStack stack,
                             final @Nullable Tool tool, final BlockState state, final BlockPos pos,
                             final @Nullable BlockEntity blockEntity, final boolean doDropsIfEligible)
    {
        if (doDropsIfEligible)
        {
            TimberModeHelper
                .getDrops(player, stack, tool, state, pos, blockEntity)
                .ifPresent(drops -> TimberModeHelper.mined(player, stack, state, pos, drops, true));
        }
        else
        {
            TimberModeHelper.mined(player, stack, state, pos, List.of(), false);
        }
    }
    
    @SemanticContract.Server
    public static void mined(final Player player, final @SemanticContract.Copied ItemStack stack,
                             final BlockState state, final BlockPos pos, final List<ItemStack> drops,
                             final boolean doDropsIfEligible)
    {
        final ServerLevel level = (ServerLevel) player.level();
        final Block       block = state.getBlock();
        player.awardStat(Stats.BLOCK_MINED.get(block));
        player.causeFoodExhaustion(0.005F);
        
        if (doDropsIfEligible)
        {
            drops.forEach(drop -> Block.popResource(level, pos, drop));
        }
        
        state.spawnAfterBreak(level, pos, stack, true);
    }
    
    @SemanticContract.Server
    public static Optional<List<ItemStack>> getDrops(final Player player,
                                                     final @SemanticContract.Copied ItemStack stack,
                                                     final @Nullable Tool tool, final BlockState state,
                                                     final BlockPos pos, final @Nullable BlockEntity blockEntity)
    {
        if (!state.requiresCorrectToolForDrops() || (tool != null && tool.isCorrectForDrops(state)))
        {
            return Optional.of(Block.getDrops(state, (ServerLevel) player.level(), pos, blockEntity, player, stack));
        }
        
        return Optional.empty();
    }
    
    public static boolean destroy(final Player player, final BlockState state, final BlockPos pos,
                                  final boolean spawnParticles)
    {
        final Level   level     = player.level();
        final boolean destroyed = level.removeBlock(pos, false);
        
        if (destroyed)
        {
            if (spawnParticles)
            {
                TimberModeHelper.spawnDestructionParticles(player, state, pos);
            }
            
            state.getBlock().destroy(level, pos, state);
        }
        
        return destroyed;
    }
    
    public static void spawnDestructionParticles(final Player player, final BlockState state, final BlockPos pos)
    {
        //noinspection resource
        player.level().levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
    }
    
    //******************************************************************************************************************
    private TimberModeHelper() {}
}
