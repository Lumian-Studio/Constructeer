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
package xyz.lumian.constructeer.multimining.item.component;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.config.ModServerConfig;
import xyz.lumian.constructeer.multimining.enchantment.ModEnchantments;
import xyz.lumian.constructeer.multimining.entity.player.PlayerAttachments;
import xyz.lumian.constructeer.multimining.item.multimining.area.IAreaProvider;
import xyz.lumian.constructeer.multimining.item.multimining.damage.IMultiMiningDamageType;
import xyz.lumian.constructeer.multimining.item.multimining.timber.IJustinTimbermode;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;
import xyz.lumian.constructeer.multimining.util.BlockContext;
import xyz.lumian.constructeer.multimining.util.SemanticContract;

import java.util.*;



//**********************************************************************************************************************
public record MultiMining(
    IAreaProvider                  areaProvider,
    Optional<Identifier>           useStat,
    Holder<IJustinTimbermode>      timberMode,
    int                            outlineRenderColour,
    ImmutableSet<MiningFlag>       miningFlags,
    Holder<IMultiMiningDamageType> damageType
)
{
    //******************************************************************************************************************
    public enum MiningFlag
        implements StringRepresentable
    {
        ANGER_PIGLINS(true),
        SEND_VIBRATIONS(true),
        AWARD_USE_STAT(true),
        CALL_MINED(true),
        SHOULD_DROP_ITEMS(true),
        ;
        
        //**************************************************************************************************************
        public static final ImmutableSet<MiningFlag> DEFAULT_FLAGS = ImmutableSet.copyOf(EnumSet.copyOf(Arrays
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
        public static int apply(final MiningFlag ...flags)
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
    
    public enum Result
    {
        SUCCESS(true,  false),
        CAPPED (false, true),
        FAILED (true,  true),
        PASS   (false, true),
        ;
        
        //**************************************************************************************************************
        public final boolean shouldBreakMined;
        public final boolean shouldCancel;
        
        //**************************************************************************************************************
        Result(final boolean shouldBreakBlock, final boolean shouldCancel)
        {
            this.shouldBreakMined = shouldBreakBlock;
            this.shouldCancel     = shouldCancel;
        }
    }
    
    public record Action(List<BlockContext> blocks, Result result) {}
    
    //******************************************************************************************************************
    public static final Codec<MultiMining> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(
            IAreaProvider.MAP_CODEC
                .forGetter(MultiMining::areaProvider),
            BuiltInRegistries.CUSTOM_STAT.byNameCodec()
                .optionalFieldOf("useStatId")
                .forGetter(MultiMining::useStat),
            ModRegistries.BuiltIn.MULTI_MINING_TIMBER_MODE.holderByNameCodec()
                .fieldOf("timberMode")
                .forGetter(MultiMining::timberMode),
            Codec.INT
                .optionalFieldOf("outlineRenderColour", 0)
                .forGetter(MultiMining::outlineRenderColour),
            MiningFlag.SET_CODEC
                .fieldOf("miningFlags")
                .orElseGet(() -> EnumSet.copyOf(MiningFlag.DEFAULT_FLAGS))
                .xmap(ImmutableSet::copyOf, EnumSet::copyOf)
                .forGetter(MultiMining::miningFlags),
            ModRegistries.BuiltIn.MULTI_MINING_DAMAGE_TYPE.holderByNameCodec()
                .fieldOf("damageType")
                .forGetter(MultiMining::damageType))
        .apply(instance, MultiMining::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMining> STREAM_CODEC = StreamCodec.composite(
        IAreaProvider.STREAM_CODEC,                                             MultiMining::areaProvider,
        ByteBufCodecs.optional(Identifier.STREAM_CODEC),                        MultiMining::useStat,
        ByteBufCodecs.holderRegistry(ModRegistries.MULTI_MINING_TIMBER_MODE),   MultiMining::timberMode,
        ByteBufCodecs.VAR_INT,                                                  MultiMining::outlineRenderColour,
        MiningFlag.SET_STREAM_CODEC.map(ImmutableSet::copyOf, EnumSet::copyOf), MultiMining::miningFlags,
        ByteBufCodecs.holderRegistry(ModRegistries.MULTI_MINING_DAMAGE_TYPE),   MultiMining::damageType,
        MultiMining::new);
    
    //******************************************************************************************************************
    public static void initialise()
    {
        PlayerBlockBreakEvents.BEFORE.register(((level, player, pos, state, blockEntity) ->
        {
            final ItemStack   stack = player.getMainHandItem();
            final MultiMining mm    = stack.get(ModComponents.MULTI_MINING);
            
            if (mm != null && !player.isCreative())
            {
                @SuppressWarnings("UnstableApiUsage")
                final Direction look_dir = Objects
                    .requireNonNull(player)
                    .getAttached(PlayerAttachments.PLAYER_MULTI_MINING_BLOCK_FACE);
                
                if (look_dir != null)
                {
                    return mm.mine(look_dir, player, stack, new BlockContext(level, state, pos));
                }
            }
            
            return true;
        }));
    }
    
    //******************************************************************************************************************
    public MultiMining(
        final           IAreaProvider                  areaProvider,
        final @Nullable Identifier                     useStat,
        final           Holder<IJustinTimbermode>      timberMode,
        final           int                            outlineRenderColour,
        final           Holder<IMultiMiningDamageType> damageType
    )
    {
        this(areaProvider, Optional.ofNullable(useStat), timberMode, outlineRenderColour, MiningFlag.DEFAULT_FLAGS,
             damageType);
    }
    
    //==================================================================================================================
    @SemanticContract.Server
    public boolean mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block)
    {
        return this.mine(face, player, stack, block, this.miningFlags);
    }
    
    @SemanticContract.Server
    public boolean mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                        final int flags)
    {
        return this.mine(face, player, stack, block, MiningFlag.bitsToSet(flags));
    }
    
    @SemanticContract.Server
    public boolean mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                        final MiningFlag flag, MiningFlag ...moreFlags)
    {
        return this.mine(face, player, stack, block, EnumSet.of(flag, moreFlags));
    }
    
    @SemanticContract.Server
    public boolean mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                        final Set<MiningFlag> flags)
    {
        //noinspection resource
        if (!(block.level() instanceof ServerLevel level))
        {
            return true;
        }
        
        final List<BlockContext> blocks;
        {
            final Action action = this.execute(face, player, stack, block);
            
            if (action.result.shouldCancel)
            {
                if (action.result == Result.CAPPED)
                {
                    final Component message = ModLang.MULTI_MINING_STRUCTURE_TOO_BIG;
                    ((ServerPlayer) player).connection.send(new ClientboundSetActionBarTextPacket(message));
                }
                
                return action.result.shouldBreakMined;
            }
            
            blocks = action.blocks;
        }
        
        if (blocks.isEmpty())
        {
            return true;
        }
        
        final boolean do_drops = (!player.preventsBlockDrops() && flags.contains(MiningFlag.SHOULD_DROP_ITEMS));
        
        final IJustinTimbermode mode = this.timberMode.value();
        
        if (!mode.cryMeARiver(face, player, stack, block, blocks, do_drops))
        {
            return true;
        }
        
        /// VOLATILE [net.minecraft.server.level.ServerPlayerGameMode#destroyBlock(BlockPos)]
        if (flags.contains(MiningFlag.SEND_VIBRATIONS))
        {
            level.gameEvent(GameEvent.BLOCK_DESTROY, block.pos(), GameEvent.Context.of(player, block.state()));
        }
        
        final boolean                notify_piglins = flags.contains(MiningFlag.ANGER_PIGLINS);
        final boolean                call_mined     = (
            flags.contains(MiningFlag.CALL_MINED)
            && !player.preventsBlockDrops()
        );
        final IMultiMiningDamageType damage_type    = this.damageType.value();
        
        if (call_mined)
        {
            stack.mineBlock(level, block.state(), block.pos(), player);
        }
        
        for (final var to_destroy : blocks)
        {
            if (notify_piglins && to_destroy.is(BlockTags.GUARDED_BY_PIGLINS))
            {
                PiglinAi.angerNearbyPiglins(level, player, false);
                break;
            }
            
            if (call_mined && !to_destroy.pos().equals(block.pos()))
            {
                if (damage_type.shouldDamage(to_destroy) && mode.shouldDamageStack(to_destroy))
                {
                    stack.mineBlock(level, to_destroy.state(), to_destroy.pos(), player);
                }
            }
        }
        
        if (!player.preventsBlockDrops() && flags.contains(MiningFlag.AWARD_USE_STAT))
        {
            this.useStat().ifPresent(player::awardStat);
        }
        
        return false;
    }
    
    public Action execute(final Direction face, final Player player, final ItemStack stack, final BlockContext block)
    {
        @SuppressWarnings("resource")
        final int penetration = player.level().registryAccess()
            .lookup (Registries.ENCHANTMENT)
            .flatMap(reg -> reg.get(ModEnchantments.PENETRATION))
            .map    (ref -> stack.getEnchantments().getLevel(ref))
            .orElse (0);
        
        final int                         cap    = ModServerConfig.INSTANCE.multiMiningHardLimit().getAsInt();
        final Map<BlockPos, BlockContext> blocks = new Object2ObjectArrayMap<>(32);
        final IAreaProvider.Output        output = (to_mine ->
        {
            blocks.put(to_mine.pos(), to_mine);
            return (blocks.size() <= cap);
        });
        
        final IAreaProvider.Result res = this.areaProvider().provide(face, player, stack, block, penetration, output);
        
        if (res == IAreaProvider.Result.SUCCESS)
        {
            if (blocks.size() > cap)
            {
                return new Action(new ArrayList<>(blocks.values()), Result.CAPPED);
            }
            
            return new Action(new ArrayList<>(blocks.values()), Result.SUCCESS);
        }
        
        return new Action(List.of(), switch (res)
        {
            case PASS    -> Result.PASS;
            case FAILED  -> Result.FAILED;
            case SUCCESS -> throw new IllegalStateException("this should not happen");
        });
    }
}
