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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.enchantment.ModEnchantments;
import xyz.lumian.constructeer.entity.player.PlayerAttachments;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.multimining.area.IAreaProvider;
import xyz.lumian.constructeer.item.multimining.damage.DamageType;
import xyz.lumian.constructeer.item.multimining.timber.IJustinTimbermode;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.CodecUtil;

import java.util.*;



//**********************************************************************************************************************
public record MultiMining(IAreaProvider areaProvider, Holder<IJustinTimbermode> timberMode, DamageType damageType)
    implements IMultiMining
{
    //******************************************************************************************************************
    public static final MapCodec<MultiMining> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            IAreaProvider.CODEC
                .fieldOf("areaProvider")
                .forGetter(MultiMining::areaProvider),
            ModRegistries.BuiltIn.MULTI_MINING_TIMBER_MODE.holderByNameCodec()
                .fieldOf("timberMode")
                .forGetter(MultiMining::timberMode),
            DamageType.CODEC
                .fieldOf("damageType")
                .forGetter(MultiMining::damageType))
        .apply(instance, MultiMining::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMining> STREAM_CODEC = StreamCodec.composite(
        IAreaProvider.STREAM_CODEC,                                           MultiMining::areaProvider,
        ByteBufCodecs.holderRegistry(ModRegistries.MULTI_MINING_TIMBER_MODE), MultiMining::timberMode,
        CodecUtil.smallEnum(DamageType.values()),                             MultiMining::damageType,
        MultiMining::new);
    
    //******************************************************************************************************************
    public static void initialise()
    {
        PlayerBlockBreakEvents.BEFORE.register(((level, player, pos, state, blockEntity) ->
        {
            final ItemStack    stack = player.getMainHandItem();
            final IMultiMining mm    = stack.get(ModComponents.MULTI_MINING);
            
            if (mm != null && !player.isCreative())
            {
                @SuppressWarnings("UnstableApiUsage")
                final Direction look_dir = Objects
                    .requireNonNull(player)
                    .getAttached(PlayerAttachments.PLAYER_MULTI_MINING_BLOCK_FACE);
                
                if (look_dir != null)
                {
                    final Result result = mm.mine(look_dir, player, stack, new BlockContext(level, state, pos),
                                                  MiningFlag.DEFAULT_FLAGS);
                    
                    if (result == Result.CAPPED)
                    {
                        player.displayClientMessage(ModLang.MULTI_MINING_STRUCTURE_TOO_BIG, true);
                    }
                    
                    return result.shouldBreakMined;
                }
            }
            
            return true;
        }));
    }
    
    //******************************************************************************************************************
    @Override public MultiMiningType<MultiMining> type() { return MultiMiningType.CUSTOM; }
    
    //==================================================================================================================
    @Override
    public Result mine(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                       final Set<MiningFlag> flags)
    {
        if (!(player instanceof ServerPlayer s_player))
        {
            return Result.FAILED;
        }
        
        final ServerLevel level = s_player.level();
        
        final List<BlockContext> blocks;
        {
            final Action action = this.execute(face, player, stack, block);
            
            if (action.result().shouldAbort)
            {
                return action.result();
            }
            
            if (action.blocks().isEmpty())
            {
                return Result.FAILED;
            }
            
            blocks = action.blocks();
        }
        
        if (!MultiMiningEvents.BEFORE.invoker().before(face, player, stack, block, blocks))
        {
            return Result.FAILED;
        }
        
        final boolean do_drops = (!player.preventsBlockDrops() && flags.contains(MiningFlag.SHOULD_DROP_ITEMS));
        
        final IJustinTimbermode mode = this.timberMode().value();
        
        if (!mode.cryMeARiver(face, player, stack, block, blocks, do_drops))
        {
            return Result.FAILED;
        }
        
        /// VOLATILE [net.minecraft.server.level.ServerPlayerGameMode#destroyBlock(BlockPos)]
        if (flags.contains(MiningFlag.SEND_VIBRATIONS))
        {
            level.gameEvent(GameEvent.BLOCK_DESTROY, block.pos(), GameEvent.Context.of(player, block.state()));
        }
        
        final boolean do_damage = (flags.contains(MiningFlag.CALL_MINED) && !player.preventsBlockDrops());
        
        if (do_damage)
        {
            stack.mineBlock(level, block.state(), block.pos(), player);
        }
        
        boolean notify_piglins = flags.contains(MiningFlag.ANGER_PIGLINS);
        
        for (final var to_destroy : blocks)
        {
            if (notify_piglins && to_destroy.is(BlockTags.GUARDED_BY_PIGLINS))
            {
                PiglinAi.angerNearbyPiglins(level, player, false);
                notify_piglins = false;
            }
            
            if (do_damage && !to_destroy.pos().equals(block.pos()))
            {
                if (this.damageType.shouldDamage(stack, to_destroy))
                {
                    stack.mineBlock(level, to_destroy.state(), to_destroy.pos(), player);
                }
            }
        }
        
        MultiMiningEvents.AFTER.invoker().after(face, player, stack, block, blocks);
        return Result.SUCCESS;
    }
    
    @Override
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
