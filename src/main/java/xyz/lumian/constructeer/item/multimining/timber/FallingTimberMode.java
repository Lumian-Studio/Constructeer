package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.phys.Vec3;
import xyz.lumian.constructeer.entity.FallingObjectEntity;
import xyz.lumian.constructeer.entity.ModEntities;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;
import xyz.lumian.constructeer.sound.ModSoundEvents;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;
import java.util.function.Function;



//**********************************************************************************************************************
public class FallingTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        final Function<BlockContext, List<ItemStack>> drop_collector;
        
        if (doDropsIfEligible)
        {
            final ItemStack stack1 = stack.copy();
            final Tool      tool   = stack1.get(DataComponents.TOOL);
            
            drop_collector = (block ->
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
        
        final ServerLevel         level  = (ServerLevel) player.level();
        final FallingObjectEntity entity = ModEntities.FALLING_OBJECT.create(level, EntitySpawnReason.NATURAL);
        
        if (entity != null)
        {
            final MultiMiningBox box = MultiMiningBox.create(blocks, drop_collector, mainBlock);
            
            blocks.forEach(block -> TimberModeHelper.destroy(player, block.state(), block.pos(), true));
            entity.snapTo(box.base().add(new Vec3(mainBlock.pos())), 0f, 0f);
            
            final Direction fall_face;
            
            if (face.getAxis() == Direction.Axis.Y)
            {
                fall_face = player.getDirection();
            } else fall_face = face.getOpposite();
            
            entity.setBoxAndFallDirection(box, fall_face);
            entity.setEffect(ModSoundEvents.TREE_FALLING);
            
            level.addFreshEntity(entity);
        }
        else
        {
            // Fallback if something fishy is going on
            TimberMode.INSTANT.cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
        }
        
        return true;
    }
}
