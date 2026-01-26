package xyz.lumian.constructeer.item.multimining.damage;

import net.minecraft.core.Holder;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.function.Supplier;



//**********************************************************************************************************************
public class BuiltInDamageType
    implements IMultiMiningDamageType
{
    //******************************************************************************************************************
    public static final Holder<IMultiMiningDamageType> BUILTIN_HAMMER = IMultiMiningDamageType.register(
        ModDefine.id("builtin_hammer"),
        new BuiltInDamageType(ModServerConfig.INSTANCE.hammer().damageMultiplier()));
    public static final Holder<IMultiMiningDamageType> BUILTIN_PLOW   = IMultiMiningDamageType.register(
        ModDefine.id("builtin_plow"),
        new BuiltInDamageType(ModServerConfig.INSTANCE.plow().damageMultiplier()));
    public static final Holder<IMultiMiningDamageType> BUILTIN_SAW    = IMultiMiningDamageType.register(
        ModDefine.id("builtin_saw"),
        new BuiltInDamageType(ModServerConfig.INSTANCE.saw().damageMultiplier()));
    
    //******************************************************************************************************************
    private final Supplier<DamageTypes> type;
    
    //******************************************************************************************************************
    BuiltInDamageType(final Supplier<DamageTypes> type) { this.type = type; }
    
    //******************************************************************************************************************
    @Override public boolean shouldDamage(final BlockContext block) { return this.type.get().shouldDamage(block); }
}
