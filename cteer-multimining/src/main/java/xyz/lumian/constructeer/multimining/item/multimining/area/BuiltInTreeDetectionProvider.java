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
package xyz.lumian.constructeer.multimining.item.multimining.area;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.multimining.ConstructeerMain;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.config.ConfigHelper;
import xyz.lumian.constructeer.multimining.item.multimining.SneakMode;
import xyz.lumian.constructeer.multimining.registry.RegistryId;
import xyz.lumian.constructeer.multimining.util.BlockContext;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;



//**********************************************************************************************************************
public class BuiltInTreeDetectionProvider
    implements IAreaProvider
{
    //******************************************************************************************************************
    private record Proxy(TreeDetectionProvider provider, SneakMode sneakMode) {}
    
    //******************************************************************************************************************
    public static final BuiltInTreeDetectionProvider           INSTANCE;
    public static final MapCodec<BuiltInTreeDetectionProvider> MAP_CODEC;
    
    //==================================================================================================================
    static
    {
        final HolderGetter<Block> blocks = BuiltInRegistries
            .acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        INSTANCE  = new BuiltInTreeDetectionProvider(new TreeDetectionProvider(blocks));
        MAP_CODEC = MapCodec.unit(() -> INSTANCE);
        
        final IntUnaryOperator max_block_converter = (input -> (input < 0 ? Integer.MAX_VALUE : input));
        ConstructeerMain.addServerReloadListener(config -> INSTANCE.providerProxy.setPlain(new Proxy(
            new TreeDetectionProvider(
                ConfigHelper.resolveIDs(config.saw().validStemBlocks().get().stream()
                    .map(str -> RegistryId.parse(Registries.BLOCK, str))),
                config.saw().maxLeafDistance().getAsInt(),
                max_block_converter.applyAsInt(config.saw().maxBlockCount().getAsInt()),
                config.saw().minLeavesCount()        .getAsInt(),
                config.saw().chopBelowCut()          .getAsBoolean(),
                config.saw().stopIfExceedingMaximum().getAsBoolean()),
            config.saw().sneakMode().get())));
    }
    
    //******************************************************************************************************************
    private final AtomicReference<Proxy> providerProxy;
    
    //******************************************************************************************************************
    public BuiltInTreeDetectionProvider(final TreeDetectionProvider provider)
    {
        this.providerProxy = new AtomicReference<>(new Proxy(provider, SneakMode.NONE));
    }
    
    //==================================================================================================================
    @Override public AreaProviderType<BuiltInTreeDetectionProvider> type() { return AreaProviderType.SAW; }
    
    //==================================================================================================================
    @Override
    public Result provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                          final int modifier, final Output output)
    {
        final Proxy proxy = this.providerProxy.getPlain();
        Output output1 = output;
        
        if (player.isCrouching())
        {
            if (proxy.sneakMode == SneakMode.VANILLA)
            {
                return Result.FAILED;
            }
            
            if (proxy.sneakMode == SneakMode.WEAK)
            {
                output1 = (block1 ->
                {
                    if (!block1.is(proxy.provider.validStemBlocks))
                    {
                        return true;
                    }
                    
                    return output.acceptAndTest(block1);
                });
            }
        }
        
        final Result result = proxy.provider.provide(face, player, stack, block, modifier, output1);
        
        if (result == Result.PASS && player instanceof ServerPlayer player1)
        {
            final Component message = ModLang.MULTI_MINING_STRUCTURE_TOO_BIG;
            player1.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
        
        return result;
    }
}
