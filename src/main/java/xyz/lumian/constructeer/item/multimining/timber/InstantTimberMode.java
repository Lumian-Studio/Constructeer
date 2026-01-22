package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.entity.BlockEntity;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public class InstantTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        final ItemStack copy = stack.copy();
        final Tool      tool = stack.get(DataComponents.TOOL);
        
        for (final var to_destroy : blocks)
        {
            if (to_destroy.state().isAir())
            {
                continue;
            }
            
            final boolean destroyed = TimberModeHelper.destroy(player, to_destroy.state(), to_destroy.pos(), true);
            
            if (destroyed && !player.preventsBlockDrops())
            {
                final BlockEntity be = to_destroy.getBlockEntity();
                TimberModeHelper.mined(player, copy, tool, to_destroy.state(), mainBlock.pos(), be, doDropsIfEligible);
            }
        }
        
        return true;
    }
}
