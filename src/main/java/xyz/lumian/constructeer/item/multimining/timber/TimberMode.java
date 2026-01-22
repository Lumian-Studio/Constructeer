package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;



//**********************************************************************************************************************
public enum TimberMode
    implements IJustinTimbermode
{
    INSTANT("instant", new InstantTimberMode()),
    FALLING("falling", new FallingTimberMode()), // TODO implementation
    ;
    
    //******************************************************************************************************************
    public static void initialise() { BuiltInTimberMode.initialise(); }
    
    //******************************************************************************************************************
    private final IJustinTimbermode         mode;
    private final Holder<IJustinTimbermode> holder;
    
    //******************************************************************************************************************
    TimberMode(final String name, final IJustinTimbermode mode)
    {
        this.mode   = mode;
        this.holder = Registry.registerForHolder(ModRegistries.BuiltIn.TIMBER_MODE, ModDefine.id(name), mode);
    }
    
    //==================================================================================================================
    public Holder<IJustinTimbermode> getHolder() { return this.holder; }
    
    //==================================================================================================================
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        return this.mode.cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
    }
}
