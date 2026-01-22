package xyz.lumian.constructeer.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
public class DebugItem
    extends Item
{
    //******************************************************************************************************************
    public DebugItem(final Properties properties) { super(properties); }
    
    //==================================================================================================================
    @Override
    public InteractionResult useOn(final UseOnContext context)
    {
        if (!(context.getPlayer() instanceof ServerPlayer player))
        {
            return InteractionResult.PASS;
        }
        
        final BlockContext ctx = BlockContext.forLevel(context.getLevel(), context.getClickedPos());
        
        if (!ctx.is(Blocks.AIR))
        {
            player.sendSystemMessage(Component.literal(ctx.state().toString()));
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand)
    {
        return super.use(level, player, hand);
    }
}
