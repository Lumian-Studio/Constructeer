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
package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.config.ConfigHelper;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.registry.RegistryId;
import xyz.lumian.constructeer.sound.ModSoundEvents;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;



//**********************************************************************************************************************
public class BuiltInFallingTreeTimberMode
    extends FallingTimberMode
{
    //******************************************************************************************************************
    public static final Holder<IJustinTimbermode> INSTANCE = Registry.registerForHolder(
        ModRegistries.BuiltIn.MULTI_MINING_TIMBER_MODE,
        ModDefine.id("falling_tree"),
        new BuiltInFallingTreeTimberMode());
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private final AtomicReference<HolderSet<Block>> validStemBlocks = new AtomicReference<>(HolderSet.empty());
    
    //******************************************************************************************************************
    BuiltInFallingTreeTimberMode()
    {
        ConstructeerMain.addServerReloadListener(config -> this.validStemBlocks.setPlain(
            ConfigHelper.resolveIDs(config.saw().validStemBlocks().get().stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, str)))));
    }
    
    //==================================================================================================================
    @Override
    protected SoundEvent getSoundEffect(final Player player, final ItemStack stack)
    {
        return ModSoundEvents.TREE_FALLING;
    }
    
    @Override
    protected Optional<HolderSet<Block>> getValidBaseBlocks()
    {
        return Optional.of(this.validStemBlocks.getPlain());
    }
    
    @Override
    public boolean shouldDamageStack(final BlockContext block)
    {
        return block.is(this.validStemBlocks.getPlain());
    }
}
