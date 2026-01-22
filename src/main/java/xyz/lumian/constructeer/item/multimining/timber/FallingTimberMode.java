package xyz.lumian.constructeer.item.multimining.timber;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import org.joml.Matrix4f;
import xyz.lumian.constructeer.entity.FallingObjectEntity;
import xyz.lumian.constructeer.entity.ModEntities;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;
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
            blocks.forEach(block -> TimberModeHelper.destroy(player, block.state(), block.pos(), true));
            entity.snapTo(mainBlock.pos(), 0f, 0f);
            
            final Transformation transformation = new Transformation(new Matrix4f().translation(-0.5f, 0.0f, -0.5f));
            entity.setTransformation(transformation);
            
            entity.setFallingDirection(face.getOpposite());
            entity.setBox(MultiMiningBox.create(blocks, drop_collector, mainBlock));
            
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
