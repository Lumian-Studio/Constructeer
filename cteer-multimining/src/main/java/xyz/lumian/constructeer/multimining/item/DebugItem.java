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
package xyz.lumian.constructeer.multimining.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import xyz.lumian.constructeer.multimining.util.BlockContext;



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
