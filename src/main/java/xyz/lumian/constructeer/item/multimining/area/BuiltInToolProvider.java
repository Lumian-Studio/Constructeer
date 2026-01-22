package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.item.multimining.predicate.BuiltInToolPredicate;
import xyz.lumian.constructeer.item.multimining.predicate.IMultiMiningPredicate;

import java.util.function.Supplier;



//**********************************************************************************************************************
@ApiStatus.Internal
public record BuiltInToolProvider(
    Supplier<IMultiMiningPredicate>       predicateGetter,
    AreaProviderType<BuiltInToolProvider> type
) implements IToolProvider
{
    //******************************************************************************************************************
    public static final BuiltInToolProvider HAMMER;
    public static final BuiltInToolProvider PLOW;
    
    public static final MapCodec<BuiltInToolProvider> HAMMER_CODEC;
    public static final MapCodec<BuiltInToolProvider> PLOW_CODEC;
    
    //==================================================================================================================
    static
    {
        HAMMER = new BuiltInToolProvider(BuiltInToolPredicate.HammerType.INSTANCE::get, AreaProviderType.HAMMER);
        PLOW   = new BuiltInToolProvider(BuiltInToolPredicate.PlowType  .INSTANCE::get, AreaProviderType.PLOW);
        
        HAMMER_CODEC = MapCodec.unit(() -> HAMMER);
        PLOW_CODEC   = MapCodec.unit(() -> PLOW);
    }
    
    //******************************************************************************************************************
    @Override public IMultiMiningPredicate predicate() { return this.predicateGetter.get(); }
}
