package xyz.lumian.constructeer.multimining.client.registry;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.multimining.client.config.CteerMultiMiningClientConfig;
import xyz.lumian.constructeer.multimining.client.renderer.MultiMiningOutlineRenderer;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMiningType;
import xyz.lumian.constructeer.multimining.registry.CteerMultiMiningRegistries;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.util.FreezableMap;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;



//**********************************************************************************************************************
public class CteerMultiMiningOutlineRenderRegistry
    implements IBootstrap
{
    //******************************************************************************************************************
    private final static class ConfigBackedShapedRenderer
        extends MultiMiningOutlineRenderer.ShapedRenderer
    {
        //**************************************************************************************************************
        private final BooleanSupplier renderSwitch;
        private final IntSupplier     colourGetter;
        
        //**************************************************************************************************************
        public ConfigBackedShapedRenderer(
            final Function<CteerMultiMiningClientConfig, BooleanSupplier> renderSwitch,
            final Function<CteerMultiMiningClientConfig, IntSupplier>     colourGetter)
        {
            this.renderSwitch = renderSwitch.apply(CteerMultiMiningClientConfig.INSTANCE);
            this.colourGetter = colourGetter.apply(CteerMultiMiningClientConfig.INSTANCE);
        }
        
        //==============================================================================================================
        @Override
        public int getOutlineColourRGB()
        {
            if (this.renderSwitch.getAsBoolean())
            {
                return (0xFF000000 | this.colourGetter.getAsInt());
            }
            
            return 0;
        }
    }
    
    //******************************************************************************************************************
    private static final FreezableMap<MultiMiningType<?>, MultiMiningOutlineRenderer.Renderer> RENDERERS
        = new FreezableMap<>(new Reference2ObjectOpenHashMap<>());
    
    //******************************************************************************************************************
    public static void register(final MultiMiningType<?> type, final MultiMiningOutlineRenderer.Renderer renderer)
    {
        final Identifier id = CteerMultiMiningRegistries.MULTI_MINING_TYPE.getKey(type);
        
        if (id == null)
        {
            throw new IllegalArgumentException("multi mining type %s is not registered"
                .formatted(type.getClass().getName()));
        }
        
        if (CteerMultiMiningOutlineRenderRegistry.RENDERERS.put(type, renderer) != null)
        {
            throw new IllegalStateException("renderer for multi mining type '%s' is already registered".formatted(id));
        }
        
        Constructeer.sendGlobalBootstrapReport("registered outline renderer '%s' for type '%s'",
                                               renderer.getClass().getName(), id);
    }
    
    //==================================================================================================================
    public static MultiMiningOutlineRenderer.@Nullable Renderer get(final MultiMiningType<?> type)
    {
        return CteerMultiMiningOutlineRenderRegistry.RENDERERS.get(type);
    }
    
    //******************************************************************************************************************
    @Override
    public void bootstrap(final BootstrapReport report)
    {
        CteerMultiMiningOutlineRenderRegistry.register(MultiMiningType.BUILTIN_HAMMER, new ConfigBackedShapedRenderer(
            (config -> config.shouldRenderHammerOutline),
            (config -> config.hammerOutlineColour)));
        CteerMultiMiningOutlineRenderRegistry.register(MultiMiningType.BUILTIN_PLOW, new ConfigBackedShapedRenderer(
            (config -> config.shouldRenderPlowOutline),
            (config -> config.plowOutlineColour)));
        CteerMultiMiningOutlineRenderRegistry.register(MultiMiningType.BUILTIN_SAW, new ConfigBackedShapedRenderer(
            (config -> config.shouldRenderSawOutline),
            (config -> config.sawOutlineColour)));
        CteerMultiMiningOutlineRenderRegistry.register(
            MultiMiningType.CUSTOM,
            new MultiMiningOutlineRenderer.ShapedRenderer());
        MultiMiningOutlineRenderer.INSTANCE.initialise();
    }
    
    @Override
    public void freeze(final BootstrapReport report)
    {
        CteerMultiMiningOutlineRenderRegistry.RENDERERS.freeze();
    }
}
