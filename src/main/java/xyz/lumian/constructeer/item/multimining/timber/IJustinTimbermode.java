package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.util.BlockContext;
import xyz.lumian.constructeer.util.SemanticContract;

import java.util.List;
import java.util.function.Function;



//**********************************************************************************************************************
/// What??? A [Function] would've worked just as well instead of this interface? well... what goes around, comes around
@FunctionalInterface
public interface IJustinTimbermode
{
    //******************************************************************************************************************
    /// First cries you a river and then gives the blocks off to the level to deal with it in some way.
    /// @return `true` if the block should be removed
    @SemanticContract.Server
    boolean cryMeARiver(Direction face, Player player, ItemStack stack, BlockContext mainBlock,
                        List<BlockContext> blocks, boolean doDropsIfEligible);
}
