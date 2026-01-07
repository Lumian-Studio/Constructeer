package xyz.lumian.constructeer.item.component;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.ModDefine;

import java.util.Map;



//**********************************************************************************************************************
public final class ModComponents
{
    //******************************************************************************************************************
    public static final Map<Identifier, DataComponentType<?>> BY_ID = new Object2ObjectArrayMap<>();
    
    //==================================================================================================================
    public static final DataComponentType<ToolbeltStorage> TOOLBELT_STORAGE = ModComponents.register(
        "toolbelt_storage",
        DataComponentType
            .<ToolbeltStorage>builder()
            .persistent(ToolbeltStorage.CODEC)
            .networkSynchronized(ToolbeltStorage.STREAM_CODEC)
            .cacheEncoding()
            .build());
    
    public static final DataComponentType<ToolbeltSettings> TOOLBELT_SETTINGS = ModComponents.register(
        "toolbelt_settings",
        DataComponentType
            .<ToolbeltSettings>builder()
            .persistent(ToolbeltSettings.CODEC)
            .networkSynchronized(ToolbeltSettings.STREAM_CODEC)
            .ignoreSwapAnimation()
            .build());
    
    public static final DataComponentType<PouchContent> POUCH_CONTENT = ModComponents.register(
        "content",
        DataComponentType
            .<PouchContent>builder()
            .persistent(PouchContent.CODEC)
            .networkSynchronized(PouchContent.STREAM_CODEC)
            .cacheEncoding()
            .build());
    
    //******************************************************************************************************************
    public static void initialise()
    {
        ComponentTooltipAppenderRegistry.addFirst(ModComponents.TOOLBELT_STORAGE);
        ComponentTooltipAppenderRegistry.addFirst(ModComponents.POUCH_CONTENT);
    }
    
    //==================================================================================================================
    public static <T> DataComponentType<T> register(final String name, final DataComponentType<T> component)
    {
        final ResourceKey<DataComponentType<?>> key = ResourceKey.create(
            Registries.DATA_COMPONENT_TYPE,
            ModDefine.id(name));
        ModComponents.BY_ID.put(key.identifier(), component);
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, component);
    }
    
    //******************************************************************************************************************
    private ModComponents() {}
}
