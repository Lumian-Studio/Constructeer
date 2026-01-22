package xyz.lumian.constructeer.item.multimining.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.registry.ModRegistries;



//**********************************************************************************************************************
@FunctionalInterface
public interface MultiMiningPredicateType<T extends IMultiMiningPredicate>
{
    //******************************************************************************************************************
    MultiMiningPredicateType<ToolPredicate>        TOOL
        = register(ModDefine.id("tool"),           ToolPredicate.MAP_CODEC);
    MultiMiningPredicateType<DynamicToolPredicate> DATA_TOOL
        = register(ModDefine.id("data_tool"),      DynamicToolPredicate.MAP_CODEC);
    MultiMiningPredicateType<IToolPredicate>       HAMMER
        = register(ModDefine.id("builtin_hammer"), BuiltInToolPredicate.HammerType.MAP_CODEC);
    MultiMiningPredicateType<IToolPredicate>       PLOW
        = register(ModDefine.id("builtin_plow"),   BuiltInToolPredicate.PlowType.MAP_CODEC);
    
    //******************************************************************************************************************
    static void initialise() {}
    
    //******************************************************************************************************************
    static <T extends IMultiMiningPredicate> MultiMiningPredicateType<T> register(final Identifier  id,
                                                                                  final MapCodec<T> codec)
    {
        return Registry.register(ModRegistries.BuiltIn.MULTI_MINING_PREDICATE_TYPE, id, (() -> codec));
    }
    
    //******************************************************************************************************************
    MapCodec<T> codec();
}
