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
package xyz.lumian.constructeer.gauntlet.item.portable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletDataComponents;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.gauntlet.player.CteerGauntletPlayerAttachments;
import xyz.lumian.constructeer.gauntlet.registry.CteerGauntletRegistries;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.accessory.IAccessory;

import java.util.Objects;
import java.util.Optional;



//**********************************************************************************************************************
public record Portable<T, S>(IPortableType<T, S> type, S data)
{
    //******************************************************************************************************************
    @SuppressWarnings("unchecked")
    public static final Codec<Portable<?, ?>> CODEC = CteerGauntletRegistries.PORTABLE_TYPE.byNameCodec()
        .dispatch(Portable::type, Portable::createTypeCodec);
    
    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, Portable<?, ?>> STREAM_CODEC = ByteBufCodecs
        .registry(CteerGauntletRegistries.PORTABLE_TYPE.key())
        .dispatch(Portable::type, Portable::createStreamCodec);
    
    //******************************************************************************************************************
    public static void initialise()
    {
        // Discard what we carried upon death, but only if we lose our inventory
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) ->
        {
            //noinspection resource
            if ((entity instanceof ServerPlayer player) && !player.level().getGameRules().get(GameRules.KEEP_INVENTORY))
            {
                Portable.tryDiscard(player);
            }
        });
        
        ItemEvents.USE_ON.register(ctx ->
        {
            if (ctx.getPlayer() == null)
            {
                return null;
            }
            
            return Portable.tryPlace(ctx.getClickedPos(), new BlockPlaceContext(ctx));
        });
        
        Compat.getAccessory().ifPresent(integration -> BlockEvents.USE_WITHOUT_ITEM
            .register((state, level, pos, player, hitResult) ->
            {
                final ItemStack stack = integration
                    .getFirstMatchingItem(player, IAccessory.SlotConstants.HAND, CteerGauntletItems.GAUNTLET_OF_POWER,
                                          false)
                    .orElse(ItemStack.EMPTY);
                
                return Portable
                    .tryPlace(pos, new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hitResult));
            }));
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static @Nullable InteractionResult tryPlace(final BlockPos pos, final BlockPlaceContext ctx)
    {
        final Player player = ctx.getPlayer();
        
        if (
            player == null
            || ctx.getHand() != InteractionHand.MAIN_HAND
            || !ctx.getItemInHand().is(CteerGauntletItems.GAUNTLET_OF_POWER)
        )
        {
            return null;
        }
        
        final Portable<?, ?> previous = Portable.getCarriedObject(player).orElse(null);
        
        if (previous != null)
        {
            final InteractionResult result = previous.place(player, pos, ctx);
            
            if (result != InteractionResult.TRY_WITH_EMPTY_HAND)
            {
                if (result != InteractionResult.PASS)
                {
                    Portable.removeCarried(player, false);
                }
                
                return result;
            }
        }
        
        return null;
    }
    
    //==================================================================================================================
    public static <T, S> @Nullable InteractionResult tryPickup(
        final ItemStack           stack,
        final Player              player,
        final BlockPos            pos,
        final IPortableType<T, S> newType,
        final T                   newObject
    )
    {
        if (
            !stack.is(CteerGauntletItems.GAUNTLET_OF_POWER)
            || !player.isSecondaryUseActive()
            || Portable.isPlayerCarrying(player)
        )
        {
            return null;
        }
        
        final Grabber grabber = Objects.requireNonNull(stack.get(CteerGauntletDataComponents.GRABBER));
        
        if (!grabber.supports(newType))
        {
            return InteractionResult.FAIL;
        }
        
        if (!(player.isCreative() || newType.canTake(newObject, player, pos)))
        {
            return InteractionResult.FAIL;
        }
        
        final S data = newType.pickup(newObject, player, pos);
        
        if (data != null)
        {
            Portable.setCarried(player, new Portable<>(newType, data));
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.FAIL;
    }
    
    public static void tryDiscard(final Player player)
    {
        Portable.getCarriedObject(player).ifPresent(portable ->
        {
            portable.discard(player);
            Portable.removeCarried(player, true);
        });
    }
    
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isPlayerCarrying(final Player player)
    {
        return player.hasAttached(CteerGauntletPlayerAttachments.PORTABLE);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("UnstableApiUsage")
    private static Optional<Portable<?, ?>> getCarriedObject(final Player player)
    {
        return Optional.<Portable<?, ?>>ofNullable(player.getAttached(CteerGauntletPlayerAttachments.PORTABLE));
    }
    
    @SuppressWarnings("UnstableApiUsage")
    private static void setCarried(final Player player, final Portable<?, ?> portable)
    {
        if (player instanceof ServerPlayer s_player)
        {
            portable.begin(s_player);
            s_player.setAttached(CteerGauntletPlayerAttachments.PORTABLE, portable);
        }
    }
    
    @SuppressWarnings("UnstableApiUsage")
    private static void removeCarried(final Player player, final boolean discarded)
    {
        if (player instanceof ServerPlayer s_player)
        {
            final Portable<?, ?> portable = s_player.removeAttached(CteerGauntletPlayerAttachments.PORTABLE);
        
            if (portable != null)
            {
                portable.end(s_player, discarded);
            }
        }
    }
    
    //==================================================================================================================
    private static <T, S> MapCodec<Portable<T, S>> createTypeCodec(
        final IPortableType<T, S> type
    )
    {
        return type.codec().xmap((data -> new Portable<>(type, data)), Portable::data);
    }
    
    private static <T, S> StreamCodec<RegistryFriendlyByteBuf, Portable<T, S>> createStreamCodec(
        final IPortableType<T, S> type
    )
    {
        return type.streamCodec().map((data -> new Portable<>(type, data)), Portable::data);
    }
    
    //******************************************************************************************************************
    public boolean canTake(final T object, final Player player, final BlockPos pos)
    {
        return this.type.canTake(object, player, pos);
    }
    
    //==================================================================================================================
    private @Nullable Portable<T, S> pickup(final T object, final Player player, final BlockPos pos)
    {
        final S data = this.type.pickup(object, player, pos);
        return (data != null ? new Portable<>(this.type, data) : null);
    }
    
    private InteractionResult place(final Player player, final BlockPos clickPos, final BlockPlaceContext context)
    {
        return this.type.place(this.data, player, clickPos, context);
    }
    
    private void discard(final Player player) { this.type.discard(this.data, player); }
    
    //==================================================================================================================
    private void begin(final ServerPlayer player) { this.type.begin(player); }
    
    private void end(final ServerPlayer player, final boolean discarded)
    {
        this.type.end(player, discarded);
    }
}
