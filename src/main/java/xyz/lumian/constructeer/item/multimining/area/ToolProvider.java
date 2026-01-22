package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.MapCodec;
import xyz.lumian.constructeer.item.multimining.predicate.IMultiMiningPredicate;
import xyz.lumian.constructeer.item.multimining.predicate.MultiMiningPredicateType;
import xyz.lumian.constructeer.registry.ModRegistries;



//**********************************************************************************************************************
public record ToolProvider(IMultiMiningPredicate predicate)
    implements IToolProvider
{
    //******************************************************************************************************************
    public static final MapCodec<ToolProvider> MAP_CODEC = ModRegistries.BuiltIn.MULTI_MINING_PREDICATE_TYPE
        .byNameCodec()
        .<IMultiMiningPredicate>dispatch(IMultiMiningPredicate::type, MultiMiningPredicateType::codec)
        .xmap(ToolProvider::new, ToolProvider::predicate)
        .fieldOf("predicate");
    
    //******************************************************************************************************************
    @Override public AreaProviderType<? extends IAreaProvider> type() { return AreaProviderType.TOOL; }
}
