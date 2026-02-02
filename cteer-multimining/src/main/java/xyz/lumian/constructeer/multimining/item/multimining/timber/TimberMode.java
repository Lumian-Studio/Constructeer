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
package xyz.lumian.constructeer.multimining.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;
import xyz.lumian.constructeer.multimining.util.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public enum TimberMode
    implements IJustinTimbermode
{
    /// Blocks will be destroyed immediately.
    INSTANT("instant", new InstantTimberMode()),
    
    /// Blocks will tilt in the opposite block face direction and "die".
    FALLING("falling", new FallingTimberMode()),
    ;
    
    //******************************************************************************************************************
    public static void initialise()
    {
        BuiltInTimberMode.initialise();
    }
    
    //******************************************************************************************************************
    private final IJustinTimbermode         mode;
    private final Holder<IJustinTimbermode> holder;
    
    //******************************************************************************************************************
    TimberMode(final String name, final IJustinTimbermode mode)
    {
        this.mode   = mode;
        this.holder = Registry
            .registerForHolder(ModRegistries.BuiltIn.MULTI_MINING_TIMBER_MODE, ModDefine.id(name), mode);
    }
    
    //==================================================================================================================
    public Holder<IJustinTimbermode> getHolder() { return this.holder; }
    
    //==================================================================================================================
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        return this.mode.cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
    }
}
