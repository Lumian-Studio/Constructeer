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

import com.mojang.serialization.*;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.multimining.registry.CteerMultiMiningRegistries;
import xyz.lumian.constructeer.level.BlockContext;



//**********************************************************************************************************************
public interface IAreaProvider
{
    //******************************************************************************************************************
    record Type<T extends IAreaProvider>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}
    
    @FunctionalInterface
    interface Output
    {
        //**************************************************************************************************************
        boolean acceptAndTest(final BlockContext block);
        default void accept(final BlockContext block) { this.acceptAndTest(block); }
    }
    
    enum Result
    {
        /// The action was successful, continue action.
        SUCCESS,
        
        /// The action failed, cancel action.
        FAILED,
        
        /// The action failed, but treat it as partially successful.
        PASS
    }
    
    //******************************************************************************************************************
    Codec<IAreaProvider> CODEC = CteerMultiMiningRegistries.AREA_PROVIDER_TYPE.byNameCodec()
        .dispatch(IAreaProvider::type, Type::codec);
    
    StreamCodec<RegistryFriendlyByteBuf, IAreaProvider> STREAM_CODEC = ByteBufCodecs
        .registry(CteerMultiMiningRegistries.AREA_PROVIDER_TYPE.key())
        .dispatch(IAreaProvider::type, Type::streamCodec);
    
    //******************************************************************************************************************
    Type<? extends IAreaProvider> type();
    
    //==================================================================================================================
    /// Does the actual area calculation.
    /// @param face   The block face the player was looking at
    /// @param player The player doing the action
    /// @param stack  The item stack in the main hand of the player with which the action was executed
    /// @param block  The block that was acted upon to trigger this provider
    /// @param output The output where provided [BlockContext] objects will be sent too; optionally returns a
    ///               boolean that can be used to determine whether any further collection of blocks should be
    ///               canceled
    /// @return The action [Result]
    Result provide(Direction face, Player player, ItemStack stack, BlockContext block, Output output);
}
