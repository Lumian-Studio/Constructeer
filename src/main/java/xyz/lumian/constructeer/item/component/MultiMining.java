package xyz.lumian.constructeer.item.component;

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
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.enchantment.ModEnchantments;
import xyz.lumian.constructeer.entity.player.PlayerAttachments;
import xyz.lumian.constructeer.item.multimining.area.IAreaProvider;
import xyz.lumian.constructeer.item.multimining.timber.IJustinTimbermode;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.SemanticContract;

import java.util.*;



//**********************************************************************************************************************
public record MultiMining(
    IAreaProvider             areaProvider,
    Optional<Identifier>      useStat,
    Holder<IJustinTimbermode> timberMode,
    int                       outlineRenderColour,
    ImmutableSet<MiningFlag>  miningFlags
)
{
    //******************************************************************************************************************
    public enum MiningFlag
        implements StringRepresentable
    {
        ANGER_PIGLINS(true),
        SEND_VIBRATIONS(true),
        AWARD_USE_STATE(true),
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
            ModRegistries.BuiltIn.TIMBER_MODE.holderByNameCodec()
                .fieldOf("timberMode")
                .forGetter(MultiMining::timberMode),
            Codec.INT
                .optionalFieldOf("outlineRenderColour", 0)
                .forGetter(MultiMining::outlineRenderColour),
            MiningFlag.SET_CODEC
                .fieldOf("miningFlags")
                .orElseGet(() -> EnumSet.copyOf(MiningFlag.DEFAULT_FLAGS))
                .xmap(ImmutableSet::copyOf, EnumSet::copyOf)
                .forGetter(MultiMining::miningFlags))
        .apply(instance, MultiMining::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMining> STREAM_CODEC = StreamCodec.composite(
        IAreaProvider.STREAM_CODEC,                                             MultiMining::areaProvider,
        ByteBufCodecs.optional(Identifier.STREAM_CODEC),                        MultiMining::useStat,
        ByteBufCodecs.holderRegistry(ModRegistries.TIMBER_MODE),                MultiMining::timberMode,
        ByteBufCodecs.VAR_INT,                                                  MultiMining::outlineRenderColour,
        MiningFlag.SET_STREAM_CODEC.map(ImmutableSet::copyOf, EnumSet::copyOf), MultiMining::miningFlags,
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
        final           IAreaProvider             areaProvider,
        final @Nullable Identifier                useStat,
        final           Holder<IJustinTimbermode> timberMode,
        final           int                       outlineRenderColour
    )
    {
        this(areaProvider, Optional.ofNullable(useStat), timberMode, outlineRenderColour, MiningFlag.DEFAULT_FLAGS);
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
        
        /// VOLATILE [net.minecraft.server.level.ServerPlayerGameMode#destroyBlock(BlockPos)]
        if (flags.contains(MiningFlag.SEND_VIBRATIONS))
        {
            level.gameEvent(GameEvent.BLOCK_DESTROY, block.pos(), GameEvent.Context.of(player, block.state()));
        }
        
        if (flags.contains(MiningFlag.ANGER_PIGLINS))
        {
            for (final var to_destroy : blocks)
            {
                if (to_destroy.is(BlockTags.GUARDED_BY_PIGLINS))
                {
                    PiglinAi.angerNearbyPiglins(level, player, false);
                    break;
                }
            }
        }
        
        final boolean do_drops = flags.contains(MiningFlag.SHOULD_DROP_ITEMS);
        
        if (
            this.timberMode.value().cryMeARiver(face, player, stack, block, blocks, do_drops)
            && !player.preventsBlockDrops()
        )
        {
            if (flags.contains(MiningFlag.CALL_MINED))
            {
                stack.mineBlock(level, block.state(), block.pos(), player);
            }
            
            if (flags.contains(MiningFlag.AWARD_USE_STATE))
            {
                this.useStat().ifPresent(player::awardStat);
            }
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
