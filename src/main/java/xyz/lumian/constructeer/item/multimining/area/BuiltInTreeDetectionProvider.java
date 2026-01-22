package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.registry.RegistryId;
import xyz.lumian.constructeer.util.BlockContext;

import java.util.concurrent.atomic.AtomicReference;



//**********************************************************************************************************************
public class BuiltInTreeDetectionProvider
    implements IAreaProvider
{
    //******************************************************************************************************************
    public static final BuiltInTreeDetectionProvider           INSTANCE;
    public static final MapCodec<BuiltInTreeDetectionProvider> MAP_CODEC;
    
    //==================================================================================================================
    static
    {
        final HolderGetter<Block> blocks = BuiltInRegistries
            .acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        INSTANCE  = new BuiltInTreeDetectionProvider(new TreeDetectionProvider(blocks));
        MAP_CODEC = MapCodec.unit(() -> INSTANCE);
        
        ConstructeerMain.addServerReloadListener(config -> INSTANCE.providerProxy.set(new TreeDetectionProvider(
            ModServerConfig.resolveIDs(config.sawValidStemBlocks().get().stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, str))),
            config.sawMaxLeafDistance().getAsInt(),
            config.sawMaxBlockCount()  .getAsInt(),
            config.sawMinLeavesCount() .getAsInt(),
            config.sawChopBelowCut()   .getAsBoolean())));
    }
    
    //******************************************************************************************************************
    private final AtomicReference<TreeDetectionProvider> providerProxy;
    
    //******************************************************************************************************************
    public BuiltInTreeDetectionProvider(final TreeDetectionProvider provider)
    {
        this.providerProxy = new AtomicReference<>(provider);
    }
    
    //==================================================================================================================
    @Override public AreaProviderType<BuiltInTreeDetectionProvider> type() { return AreaProviderType.SAW; }
    
    //==================================================================================================================
    @Override
    public boolean provide(final Direction face, final Player player, final ItemStack stack, final BlockContext block,
                           final int modifier, final Output output)
    {
        return this.providerProxy.get().provide(face, player, stack, block, modifier, output);
    }
}
