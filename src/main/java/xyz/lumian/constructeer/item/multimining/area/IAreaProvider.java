package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.*;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
public interface IAreaProvider
{
    //******************************************************************************************************************
    @FunctionalInterface
    interface Output
    {
        //**************************************************************************************************************
        boolean acceptAndTest(final BlockContext block);
        default void accept(final BlockContext block) { this.acceptAndTest(block); }
    }
    
    enum Result
    {
        /// The action was successful, break all the block.
        SUCCESS,
        
        /// The action failed, break only the mined block.
        FAILED,
        
        /// The action failed, but the mined block should not be destroyed.
        PASS
    }
    
    //******************************************************************************************************************
    MapCodec<IAreaProvider> MAP_CODEC = ModRegistries.BuiltIn.AREA_PROVIDER_TYPE.byNameCodec()
        .dispatchMap(IAreaProvider::type, AreaProviderType::codec);
    
    StreamCodec<RegistryFriendlyByteBuf, IAreaProvider> STREAM_CODEC = ByteBufCodecs
        .registry(ModRegistries.AREA_PROVIDER_TYPE)
        .dispatch(IAreaProvider::type, (type -> ByteBufCodecs.fromCodecWithRegistries(type.codec().codec())));
    
    //******************************************************************************************************************
    AreaProviderType<? extends IAreaProvider> type();
    
    //==================================================================================================================
    /// Does the actual area calculation.
    /// @param face     The block face the player was looking at
    /// @param player   The player doing the action
    /// @param stack    The item stack in the main hand of the player with which the action was executed
    /// @param block    The block that was acted upon to trigger this provider
    /// @param modifier An optional modifier that should modify the output in some way;
    ///                 e.g. for multi-mining instances, this is the Constructeer penetration enchantment level
    /// @param output   The output where provided [BlockContext] objects will be sent too; optionally returns a
    ///                 boolean that can be used to determine whether any further collection of blocks should be
    ///                 canceled
    /// @return The action [Result]
    Result provide(Direction face, Player player, ItemStack stack, BlockContext block, int modifier, Output output);
}
