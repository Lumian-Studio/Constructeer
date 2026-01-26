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
package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.entity.FallingObjectEntity;
import xyz.lumian.constructeer.entity.ModEntities;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;



//**********************************************************************************************************************
public class FallingTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    protected MultiMiningBox createMultiMiningBox(final Direction face, final Player player, final ItemStack stack,
                                                  final BlockContext mainBlock, final List<BlockContext> blocks,
                                                  final Function<BlockContext, List<ItemStack>> dropsCollector)
    {
        return MultiMiningBox.create(blocks, dropsCollector, mainBlock, this.getValidBaseBlocks());
    }
    
    protected Optional<HolderSet<Block>> getValidBaseBlocks() { return Optional.empty(); }
    
    protected @Nullable SoundEvent getSoundEffect(final Player player, final ItemStack stack) { return null; }
    
    protected Entity createEntity(final Direction face, final Player player, final ItemStack stack,
                                  final BlockContext mainBlock, final List<BlockContext> blocks,
                                  final boolean doDropsIfEligible)
    {
        final ServerLevel         level  = (ServerLevel) player.level();
        final FallingObjectEntity entity = ModEntities.FALLING_OBJECT.create(level, EntitySpawnReason.NATURAL);
        
        if (entity == null)
        {
            return null;
        }
        
        final Function<BlockContext, List<ItemStack>> drop_collector;
        
        if (doDropsIfEligible)
        {
            final ItemStack stack1 = stack.copy();
            final Tool      tool   = stack1.get(DataComponents.TOOL);
            drop_collector         = (block ->
            {
                TimberModeHelper.mined(player, stack1, block.state(), block.pos(), List.of(), false);
                return TimberModeHelper
                    .getDrops(player, stack1, tool, block.state(), block.pos(), block.getBlockEntity())
                    .orElse(List.of());
            });
        }
        else
        {
            drop_collector = (block -> List.of());
        }
        
        final MultiMiningBox box = this.createMultiMiningBox(face, player, stack, mainBlock, blocks, drop_collector);
        
        blocks.forEach(block -> TimberModeHelper.destroy(player, block.state(), block.pos(), false));
        entity.snapTo(box.base().add(new Vec3(mainBlock.pos())), 0f, 0f);
        
        final Direction fall_face = (face.getAxis().isVertical() ? player.getDirection() : face.getOpposite());
        entity.setBoxAndFallDirection(box, fall_face);
        entity.setEffect(this.getSoundEffect(player, stack));
        
        return entity;
    }
    
    //==================================================================================================================
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        final Entity entity = this.createEntity(face, player, stack, mainBlock, blocks, doDropsIfEligible);
        
        if (entity != null)
        {
            //noinspection resource
            return player.level().addFreshEntity(entity);
        }
        
        return TimberMode.INSTANT.cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
    }
}
