package xyz.lumian.constructeer.item.multimining.timber;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;



//**********************************************************************************************************************
public class BuiltInTimberMode
    implements IJustinTimbermode
{
    //******************************************************************************************************************
    public static final Holder<IJustinTimbermode> HAMMER
        = register("builtin_hammer", TimberMode.INSTANT, ModServerConfig::hammerTimberMode);
    public static final Holder<IJustinTimbermode> PLOW
        = register("builtin_plow",   TimberMode.INSTANT, ModServerConfig::plowTimberMode);
    public static final Holder<IJustinTimbermode> SAW
        = register("builtin_saw",    TimberMode.FALLING, ModServerConfig::sawTimberMode);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static Holder<IJustinTimbermode> register(final String name, final TimberMode defaultMode,
                                                      final Function<ModServerConfig, EnumValue<TimberMode>> updater)
    {
        final BuiltInTimberMode proxy = new BuiltInTimberMode(defaultMode);
        ConstructeerMain.addServerReloadListener(config -> proxy.mode.set(updater.apply(config).get()));
        return Registry.registerForHolder(ModRegistries.BuiltIn.TIMBER_MODE, ModDefine.id(name), proxy);
    }
    
    //******************************************************************************************************************
    private final AtomicReference<TimberMode> mode;
    
    //******************************************************************************************************************
    private BuiltInTimberMode(final TimberMode mode) { this.mode = new AtomicReference<>(mode); }
    
    //==================================================================================================================
    @Override
    public boolean cryMeARiver(final Direction face, final Player player, final ItemStack stack,
                               final BlockContext mainBlock, final List<BlockContext> blocks,
                               final boolean doDropsIfEligible)
    {
        return this.mode.get().cryMeARiver(face, player, stack, mainBlock, blocks, doDropsIfEligible);
    }
}
