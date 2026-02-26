package xyz.lumian.constructeer.gauntlet.item.portable;

import com.google.common.collect.*;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;



//**********************************************************************************************************************
final class BlockStateRetentionManager
{
    //******************************************************************************************************************
    private record ResetRule(Collection<Property<?>> properties)
        implements BinaryOperator<BlockState>
    {
        //**************************************************************************************************************
        private static final Interner<ResetRule> INTERNER = Interners.newWeakInterner();
        
        //**************************************************************************************************************
        public static @Nullable ResetRule of(final BlockStateRetentionManager         manager,
                                             final StateDefinition<Block, BlockState> definition)
        {
            final Collection<Property<?>> properties = definition.getProperties();
            
            assert (manager.resetRules != null && manager.retentionRules != null);
            final Collection<Property<?>> exceptions       = manager.retentionRules.get(definition.getOwner());
            final Collection<Property<?>> baked_properties = Stream
                .concat(manager.resetRules.get(null).stream(), manager.resetRules.get(definition.getOwner()).stream())
                .filter(property -> (properties.contains(property) && !exceptions.contains(property)))
                .collect(ImmutableSet.toImmutableSet());
            
            return (!baked_properties.isEmpty() ? ResetRule.INTERNER.intern(new ResetRule(baked_properties)) : null);
        }
        
        //==============================================================================================================
        private static <T extends Comparable<T>> BlockState applyHelper(final BlockState  mutableState,
                                                                        final BlockState  copyState,
                                                                        final Property<T> prop)
        {
            return mutableState.setValue(prop, copyState.getValue(prop));
        }
        
        //**************************************************************************************************************
        @Override
        public BlockState apply(BlockState mutableState, final BlockState copyState)
        {
            if (mutableState.getProperties().isEmpty())
            {
                return mutableState;
            }
            
            for (final var prop : this.properties)
            {
                mutableState = ResetRule.applyHelper(mutableState, copyState, prop);
            }
            
            return mutableState;
        }
    }
    
    //******************************************************************************************************************
    private final Map<Block, ResetRule> rules = new Reference2ObjectOpenHashMap<>();
    
    private @Nullable Multimap<@Nullable Block, Property<?>> resetRules     = HashMultimap.create();
    private @Nullable Multimap<@Nullable Block, Property<?>> retentionRules = HashMultimap.create();
    
    //******************************************************************************************************************
    public BlockState updateState(final BlockState mutableState, final BlockState copyState)
    {
        final ResetRule rule = this.rules.get(mutableState.getBlock());
        return (rule != null ? rule.apply(mutableState, copyState) : mutableState);
    }
    
    //==================================================================================================================
    public void addResetRules(final @Nullable Block block, final Collection<Property<?>> properties)
    {
        if (this.resetRules == null)
        {
            throw new IllegalStateException("BlockStateRetentionManager has already been baked");
        }
        
        this.resetRules.putAll(block, properties);
    }
    
    public void addRetentionRules(final Block block, final Collection<Property<?>> properties)
    {
        if (this.retentionRules == null)
        {
            throw new IllegalStateException("BlockStateRetentionManager has already been baked");
        }
        
        this.retentionRules.putAll(Objects.requireNonNull(block), properties);
    }
    
    //==================================================================================================================
    public void bake()
    {
        if (this.resetRules == null)
        {
            throw new IllegalStateException("BlockStateRetentionManager has already been baked");
        }
        
        final Registry<Block> registry = BuiltInRegistries.BLOCK;
        
        for (int i = 0; i < registry.size(); ++i)
        {
            final Block block = registry.byId(i);
            
            if (!block.getStateDefinition().getProperties().isEmpty())
            {
                final ResetRule rule = ResetRule.of(this, registry.byId(i).getStateDefinition());
                
                if (rule != null)
                {
                    this.rules.put(block, rule);
                }
            }
        }
        
        this.resetRules     = null;
        this.retentionRules = null;
    }
}
