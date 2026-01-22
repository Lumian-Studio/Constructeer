package xyz.lumian.constructeer.item.multimining.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.registry.ModRegistries;
import xyz.lumian.constructeer.util.BlockContext;



//**********************************************************************************************************************
public class DynamicToolPredicate
    implements IMultiMiningPredicate
{
    //******************************************************************************************************************
    public static final MapCodec<DynamicToolPredicate> MAP_CODEC = RegistryFileCodec
        .create(ModRegistries.TOOL_PREDICATE, ToolPredicate.CODEC)
        .xmap(DynamicToolPredicate::new, (dyn -> dyn.predicate))
        .fieldOf("dataKey");
    
    //******************************************************************************************************************
    private final Holder<ToolPredicate> predicate;
    
    //******************************************************************************************************************
    public DynamicToolPredicate(final Holder<ToolPredicate> predicate) { this.predicate = predicate; }
    
    //==================================================================================================================
    @Override
    public MultiMiningPredicateType<DynamicToolPredicate> type()
    {
        return MultiMiningPredicateType.DATA_TOOL;
    }
    
    @Override
    public boolean canExecute(final Player player, final BlockContext main, final ItemStack stack)
    {
        return this.predicate.value().canExecute(player, main, stack);
    }
    
    @Override
    public boolean test(final Player player, final BlockContext mainBlock, final BlockContext testBlock)
    {
        return this.predicate.value().test(player, mainBlock, testBlock);
    }
}
