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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.util.SemanticContract;



//**********************************************************************************************************************
/// The [IPortableType] class describes an algorithm for specific game objects upon interaction, to determine how this
/// object is being carried by the player (e.g. see [BlockPortableType] for carrying blocks).
/// @param <T> The object that is carried
/// @param <S> The type containing the serialised form of the carried object
public interface IPortableType<T, S>
{
    //******************************************************************************************************************
    Multimap<Holder<Attribute>, AttributeModifier> DEFAULT_MODIFIERS = ImmutableMultimap.of(
        Attributes.MOVEMENT_SPEED, IPortableType.reductionModifier(CteerDefine.id("speed"), 0.5),
        Attributes.GRAVITY, IPortableType.promotionModifier(CteerDefine.id("gravity"), 0.3),
        Attributes.ATTACK_SPEED, IPortableType.reductionModifier(CteerDefine.id("attack"), 0.7),
        Attributes.BLOCK_BREAK_SPEED, IPortableType.reductionModifier(CteerDefine.id("mining"), 0.7),
        Attributes.BLOCK_INTERACTION_RANGE, IPortableType.reductionModifier(CteerDefine.id("reach"), 0.2)
    );
    
    //==================================================================================================================
    private static AttributeModifier reductionModifier(final Identifier id, final double factor)
    {
        return new AttributeModifier(id, -factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    
    private static AttributeModifier promotionModifier(final Identifier id, final double factor)
    {
        return new AttributeModifier(id, factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    
    //******************************************************************************************************************
    /// The [MapCodec] used to store the portable on disk upon unloading a player and later reloading it.
    MapCodec<S> codec();
    
    /// The [StreamCodec] used to transfer portable data to the client.
    /// The data that is synced will usually just be important for rendering purposes so data that is not important for
    /// such usually best is omitted (unless there is other things that are needed for custom portables).
    StreamCodec<RegistryFriendlyByteBuf, S> streamCodec();
    
    //==================================================================================================================
    /// Determines whether the given object at the given [BlockPos] can be made a portable by the player.
    /// @param object The object to pick up
    /// @param player The player trying to pick up the object
    /// @param pos    The position of the object to pick up
    boolean canTake(T object, Player player, BlockPos pos);
    
    //==================================================================================================================
    /// Tries to pick up the object at the given [BlockPos].
    /// @param object The object to pick up
    /// @param player The player trying to pick up the object
    /// @param pos    The position of the object to pick up
    /// @return The serialised representation of the picked up object, or `null` if it couldn't be picked up for some
    ///         reason
   @Nullable S pickup(T object, Player player, BlockPos pos);
    
    /// Tries to place the object at the given location.
    /// @param object   The object to place
    /// @param player   The player trying to place the object
    /// @param clickPos The position of the block that was clicked (this might not be the position the portable should
    ///                 be placed on)
    /// @param context  The block placing context.
    ///                 Use [BlockPlaceContext#getClickedPos()] to determine where the portable should be placed
    /// @return [InteractionResult#SUCCESS] and [InteractionResult#CONSUME] meaning the action was successful,
    ///         [InteractionResult#PASS] means the object could not be placed down but should be kept and
    ///         [InteractionResult#FAIL] something went wrong during placing and the object should be discarded
    ///         (not [#discard(S, Player)]). If this is [InteractionResult#TRY_WITH_EMPTY_HAND], the default
    ///         event handling will be processed instead.
    InteractionResult place(S object, Player player, BlockPos clickPos, BlockPlaceContext context);
    
    /// If the object has been lost by the player (e.g. upon death), this determines how to fare with the portable.
    /// If this does nothing, the portable will be discarded entirely.
    /// @param object The object to discard
    /// @param player The player which carries the portable
    void discard(S object, Player player);
    
    //==================================================================================================================
    /// Can be used to determine what should happen after the player has successfully picked up a portable object.
    /// @param player The player who picked the object up
    @SemanticContract.Server
    default void begin(final ServerPlayer player)
    {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.BEEHIVE_EXIT,
            SoundSource.PLAYERS,
            1.0F, 1.0F);
        
        if (player.isCreative())
        {
            return;
        }
        
        IPortableType.DEFAULT_MODIFIERS.forEach((att, mod) ->
        {
            final AttributeInstance inst = player.getAttribute(att);
            
            if (inst != null)
            {
                inst.addPermanentModifier(mod);
            }
        });
    }
    
    /// Can be used to determine what should happen after the player has successfully placed or lost a portable object.
    /// @param player    The player who placed/discarded the object
    /// @param discarded `true` if the player lost the portable, `false` if it was placed instead
    @SemanticContract.Server
    default void end(final ServerPlayer player, final boolean discarded)
    {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.BEEHIVE_EXIT,
            SoundSource.PLAYERS,
            1.0F, 1.0F);
        player.getAttributes().removeAttributeModifiers(IPortableType.DEFAULT_MODIFIERS);
    }
}
