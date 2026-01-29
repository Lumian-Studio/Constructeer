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
package xyz.lumian.constructeer.item.multimining;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.stat.ModStats;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;



//**********************************************************************************************************************
public class BuiltInMultiMining
    implements IMultiMining
{
    //******************************************************************************************************************
    public static final BuiltInMultiMining HAMMER = new BuiltInMultiMining(
        ModServerConfig.INSTANCE.hammer(),
        MultiMiningType.BUILTIN_HAMMER,
        ModStats.HAMMER_USED);
    public static final BuiltInMultiMining PLOW  = new BuiltInMultiMining(
        ModServerConfig.INSTANCE.plow(),
        MultiMiningType.BUILTIN_PLOW,
        ModStats.PLOW_USED);
    public static final BuiltInMultiMining SAW = new BuiltInMultiMining(
        ModServerConfig.INSTANCE.saw(),
        MultiMiningType.BUILTIN_SAW,
        ModStats.SAW_USED);
    
    //==================================================================================================================
    static
    {
        ConstructeerMain.addServerReloadListener(config ->
        {
            BuiltInMultiMining.HAMMER.update();
            BuiltInMultiMining.PLOW  .update();
            BuiltInMultiMining.SAW   .update();
        });
    }
    
    //******************************************************************************************************************
    private final           AtomicReference<@Nullable MultiMining> proxy;
    private final           MultiMiningType<BuiltInMultiMining>    type;
    private final @Nullable Identifier                             statType;
    private final           MmFactory                              factory;
    
    //******************************************************************************************************************
    public BuiltInMultiMining(final           MmFactory                           factory,
                              final           MultiMiningType<BuiltInMultiMining> type,
                              final @Nullable Identifier                          statType)
    {
        this.proxy    = new AtomicReference<>(null);
        this.type     = type;
        this.statType = statType;
        this.factory  = factory;
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
    protected void update() { this.proxy.setPlain(this.factory.createComponent()); }
}
