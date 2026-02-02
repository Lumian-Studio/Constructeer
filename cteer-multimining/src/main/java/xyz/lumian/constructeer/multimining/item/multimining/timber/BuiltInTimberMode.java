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
import xyz.lumian.constructeer.multimining.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.config.ModServerConfig;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;
import xyz.lumian.constructeer.multimining.util.BlockContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;



//**********************************************************************************************************************
public class BuiltInTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    public static final Holder<IJustinTimbermode> HAMMER
        = register("builtin_hammer", TimberMode.INSTANT, (config -> config.hammer().timberMode().get()));
    public static final Holder<IJustinTimbermode> PLOW
        = register("builtin_plow",   TimberMode.INSTANT, (config -> config.plow().timberMode().get()));
    public static final Holder<IJustinTimbermode> SAW;
    
    //==================================================================================================================
    static
    {
        SAW = register("builtin_saw", BuiltInFallingTreeTimberMode.INSTANCE.value(), (config ->
        {
            final String mode = config.saw().timberMode().get();
            return (mode.equalsIgnoreCase("falling_tree")
                ? BuiltInFallingTreeTimberMode.INSTANCE.value()
                : Arrays.stream(TimberMode.values())
                    .filter(tmode -> tmode.name().equalsIgnoreCase(mode))
                    .findFirst()
                    .orElseThrow());
        }));
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        BuiltInFallingTreeTimberMode.initialise();
    }
    
    //==================================================================================================================
    private static Holder<IJustinTimbermode> register(final String name, final IJustinTimbermode defaultMode,
                                                      final Function<ModServerConfig, IJustinTimbermode> updater)
    {
        final BuiltInTimberMode proxy = new BuiltInTimberMode(defaultMode);
        ConstructeerMain.addServerReloadListener(config -> proxy.mode.setPlain(updater.apply(config)));
        return Registry.registerForHolder(ModRegistries.BuiltIn.MULTI_MINING_TIMBER_MODE, ModDefine.id(name), proxy);
    }
    
    //******************************************************************************************************************
    private final AtomicReference<IJustinTimbermode> mode;
    
    //******************************************************************************************************************
    BuiltInTimberMode(final IJustinTimbermode mode) { this.mode = new AtomicReference<>(mode); }
    
    //==================================================================================================================
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        return this.mode.getPlain().cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
    }
}
