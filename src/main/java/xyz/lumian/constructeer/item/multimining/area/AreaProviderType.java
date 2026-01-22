package xyz.lumian.constructeer.item.multimining.area;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.registry.ModRegistries;



//**********************************************************************************************************************
public record AreaProviderType<T extends IAreaProvider>(MapCodec<T> codec, int apiVersion)
{
    //******************************************************************************************************************
    // hardcoded built-ins
    @ApiStatus.Internal public static final AreaProviderType<BuiltInToolProvider>          HAMMER;
    @ApiStatus.Internal public static final AreaProviderType<BuiltInToolProvider>          PLOW;
    @ApiStatus.Internal public static final AreaProviderType<BuiltInTreeDetectionProvider> SAW;
    
    // data driven
    public static final AreaProviderType<ToolProvider>          TOOL;
    public static final AreaProviderType<TreeDetectionProvider> TREE_DETECTION;
    
    //==================================================================================================================
    static
    {
        HAMMER         = register("builtin_hammer", BuiltInToolProvider.HAMMER_CODEC,       1);
        PLOW           = register("builtin_plow",   BuiltInToolProvider.PLOW_CODEC,         1);
        SAW            = register("builtin_saw",    BuiltInTreeDetectionProvider.MAP_CODEC, 1);
        TOOL           = register("tool",           ToolProvider.MAP_CODEC,                 1);
        TREE_DETECTION = register("tree_detection", TreeDetectionProvider.MAP_CODEC,        1);
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private static <T extends IAreaProvider> AreaProviderType<T> register(final String name, final MapCodec<T> codec,
                                                                          final int apiVersion)
    {
        return Registry.register(
            ModRegistries.BuiltIn.AREA_PROVIDER_TYPE,
            ModDefine.id(name),
            new AreaProviderType<>(codec, apiVersion));
    }
}
