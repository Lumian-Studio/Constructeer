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

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.SemanticContract;

import java.util.*;



//**********************************************************************************************************************
public interface IMultiMining
{
    //******************************************************************************************************************
    enum MiningFlag
        implements StringRepresentable
    {
        ANGER_PIGLINS(true),
        SEND_VIBRATIONS(true),
        CALL_MINED(true),
        SHOULD_DROP_ITEMS(true),
        ;
        
        //**************************************************************************************************************
        public static final ImmutableSet<MiningFlag> DEFAULT_FLAGS = ImmutableSet.copyOf(
            EnumSet.copyOf(Arrays
            .stream(MiningFlag.values())
            .filter(flag -> flag.isDefault)
            .toList()));
        
        public static final int DEFAULT_MASK = DEFAULT_FLAGS.stream()
            .mapToInt(flag -> (1 << flag.ordinal()))
            .reduce(0, ((i, flag) -> (i | flag)));
        
        //==============================================================================================================
        public static final Codec<MiningFlag> CODEC = StringRepresentable.fromEnumWithMapping(
            MiningFlag::values,
            (name -> name.toLowerCase(Locale.ROOT)));
        
        public static final Codec<EnumSet<MiningFlag>> SET_CODEC = Codec.list(MiningFlag.CODEC)
            .xmap(EnumSet::copyOf, List::copyOf);
        
        public static final StreamCodec<ByteBuf, MiningFlag> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map((val -> MiningFlag.values()[val]), MiningFlag::ordinal);
        
        public static final StreamCodec<ByteBuf, EnumSet<MiningFlag>> SET_STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(MiningFlag::bitsToSet, MiningFlag::setToBits);
        
        //**************************************************************************************************************
        public static int apply(final MiningFlag...flags)
        {
            int mask = 0;
            
            for (final var flag : flags)
            {
                mask |= (1 << flag.ordinal());
            }
            
            return mask;
        }
        
        //==============================================================================================================
        public static int setToBits(final EnumSet<MiningFlag> set)
        {
            return set.stream()
                .mapToInt(flag -> (1 << flag.ordinal()))
                .reduce(0, ((i, flag) -> i | flag));
        }
        
        public static EnumSet<MiningFlag> bitsToSet(final int bits)
        {
            final EnumSet<MiningFlag> set = EnumSet.noneOf(MiningFlag.class);
            
            for (int i = 0; i < MiningFlag.values().length; ++i)
            {
                if (((bits >>> i) & 1) == 1)
                {
                    set.add(MiningFlag.values()[i]);
                }
            }
            
            return set;
        }
        
        //**************************************************************************************************************
        public final boolean isDefault;
        
        //**************************************************************************************************************
        MiningFlag(final boolean isDefault) { this.isDefault = isDefault; }
        
        //==============================================================================================================
        @Override public String getSerializedName() { return this.name().toLowerCase(Locale.ROOT); }
    }
    
    enum Result
    {
        // The operation completed successfully.
        SUCCESS(false, false),
        
        // The operation could not be completed successfully, as the multi-mining block limit was reached.
        CAPPED(false, true),
        
        // The operation could not be completed successfully, but it didn't fail completely.
        PASS(false, true),
        
        // The operation failed completely, continue like a normal tool.
        FAILED(true, true),
        ;
        
        //**************************************************************************************************************
        public final boolean shouldBreakMined;
        public final boolean shouldAbort;
        
        //**************************************************************************************************************
        Result(final boolean shouldBreakBlock, final boolean shouldAbort)
        {
            this.shouldBreakMined = shouldBreakBlock;
            this.shouldAbort      = shouldAbort;
        }
    }
    
    record Action(List<BlockContext> blocks, Result result) {}
    
    //******************************************************************************************************************
    Codec<IMultiMining> CODEC = ModRegistries.BuiltIn.MULTI_MINING_TYPE.byNameCodec()
        .<IMultiMining>dispatch(IMultiMining::type, MultiMiningType::codec);
    StreamCodec<RegistryFriendlyByteBuf, IMultiMining> STREAM_CODEC = ByteBufCodecs
        .registry(ModRegistries.MULTI_MINING_TYPE)
        .<IMultiMining>dispatch(IMultiMining::type, MultiMiningType::streamCodec);
    
    //******************************************************************************************************************
    MultiMiningType<? extends IMultiMining> type();
    
    //==================================================================================================================
    @SemanticContract.Server
    default Result mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                         final int flags)
    {
        return this.mine(face, player, stack, block, MiningFlag.bitsToSet(flags));
    }
    
    @SemanticContract.Server
    default Result mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                         final MiningFlag flag, MiningFlag ...moreFlags)
    {
        return this.mine(face, player, stack, block, EnumSet.of(flag, moreFlags));
    }
    
    @SemanticContract.Server
    Result mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                final Set<MiningFlag> flags);
    
    Action execute(final Direction face, final Player player, final ItemStack stack, final BlockContext block);
}
