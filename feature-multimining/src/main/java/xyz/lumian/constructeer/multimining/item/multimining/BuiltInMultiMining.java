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
package xyz.lumian.constructeer.multimining.item.multimining;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.multimining.config.CteerMultiMiningServerConfig;
import xyz.lumian.constructeer.multimining.stat.CteerMultiMiningStats;
import xyz.lumian.constructeer.level.BlockContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;



//**********************************************************************************************************************
public class BuiltInMultiMining
    implements IMultiMining
{
    //******************************************************************************************************************
    public static final BuiltInMultiMining HAMMER
        = new BuiltInMultiMining(MultiMiningType.BUILTIN_HAMMER, CteerMultiMiningStats.HAMMER_USED);
    public static final BuiltInMultiMining PLOW
        = new BuiltInMultiMining(MultiMiningType.BUILTIN_PLOW, CteerMultiMiningStats.PLOW_USED);
    public static final BuiltInMultiMining SAW
        = new BuiltInMultiMining(MultiMiningType.BUILTIN_SAW, CteerMultiMiningStats.SAW_USED);
    
    //==================================================================================================================
    static
    {
        CteerMultiMiningServerConfig.INSTANCE.addListener(config ->
        {
            BuiltInMultiMining.HAMMER.update(config.hammer);
            BuiltInMultiMining.PLOW  .update(config.plow);
            BuiltInMultiMining.SAW   .update(config.saw);
        });
    }
    
    //******************************************************************************************************************
    private final           AtomicReference<@Nullable MultiMining> proxy;
    private final           MultiMiningType<BuiltInMultiMining>    type;
    private final @Nullable Identifier                             statType;
    
    //******************************************************************************************************************
    public BuiltInMultiMining(final MultiMiningType<BuiltInMultiMining> type, final @Nullable Identifier statType)
    {
        this.proxy    = new AtomicReference<>(null);
        this.type     = type;
        this.statType = statType;
    }
    
    //==================================================================================================================
    @Override public MultiMiningType<BuiltInMultiMining> type() { return this.type; }
    
    //==================================================================================================================
    @Override
    public Result mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                       final Set<MiningFlag> flags)
    {
        final Result result = Objects.requireNonNull(this.proxy.getPlain()).mine(face, player, stack, block, flags);
        
        if (
            !result.shouldAbort
            && this.statType != null
            && flags.contains(MiningFlag.CALL_MINED)
            && !player.preventsBlockDrops()
        )
        {
            player.awardStat(this.statType);
        }
        
        return result;
    }
    
    @Override
    public Action execute(final Direction face, final Player player, final ItemStack stack, final BlockContext block)
    {
        return Objects.requireNonNull(this.proxy.getPlain()).execute(face, player, stack, block);
    }
    
    //==================================================================================================================
    protected void update(final MmFactory factory) { this.proxy.setPlain(factory.createComponent()); }
}
