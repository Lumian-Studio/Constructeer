package xyz.lumian.constructeer.multimining.item.multimining;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.level.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public class MultiMiningEvents
{
    //******************************************************************************************************************
    public interface MultiMiningBefore
    {
        //**************************************************************************************************************
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean before(Direction face, Player player, ItemStack toolStack, BlockContext mainBlock,
                       List<BlockContext> blocks);
    }
    
    public interface MultiMiningAfter
    {
        //**************************************************************************************************************
        void after(Direction face, Player player, ItemStack toolStack, BlockContext mainBlock,
                   List<BlockContext> blocks);
    }
    
    //******************************************************************************************************************
    public static final Event<MultiMiningBefore> BEFORE
        = EventFactory.createArrayBacked(MultiMiningBefore.class, (listeners -> (face, player, tool, main, blocks) ->
        {
            boolean cancelled = false;
            
            for (final var listener : listeners)
            {
                if (!listener.before(face, player, tool, main, blocks))
                {
                    cancelled = true;
                }
            }
            
            return !cancelled;
        }));
    
    public static final Event<MultiMiningAfter> AFTER
        = EventFactory.createArrayBacked(MultiMiningAfter.class, (listeners -> ((face, player, tool, main, blocks) ->
        {
            for (final var listener : listeners)
            {
                listener.after(face, player, tool, main, blocks);
            }
        })));
}
