package xyz.lumian.constructeer.client.integration;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Function3;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.client.config.ModClientConfig;
import xyz.lumian.constructeer.config.ModServerConfig;

import java.util.Set;



//**********************************************************************************************************************
public class ClothConfig
    implements ModMenuApi
{
    //******************************************************************************************************************
    private static final Set<ModConfigSpec> TO_SAVE = Sets.newIdentityHashSet();
    
    //******************************************************************************************************************
    private static <
        T,
        U extends AbstractConfigListEntry<T>,
        V extends AbstractFieldBuilder<T, U, V>
    > V createBasicConfigEntry(
        final ModConfigSpec.ConfigValue<T>                   value,
        final Function3<ConfigEntryBuilder, Component, T, V> generator,
        final ModLang                                        name,
        final ModLang                                        tooltip
    )
    {
        return generator.apply(ConfigEntryBuilder.create(), name, value.get())
            .setDefaultValue(value.getDefault())
            .setTooltip(Component.translatableWithFallback(tooltip.getKey(), value.getSpec().getComment()));
    }
    
    private static <
        T,
        U extends AbstractConfigListEntry<T>,
        V extends AbstractFieldBuilder<T, U, V>
    > AbstractConfigListEntry<T> createConfigEntry(
        final ModConfigSpec                                  spec,
        final ModConfigSpec.ConfigValue<T>                   value,
        final Function3<ConfigEntryBuilder, Component, T, V> generator,
        final ModLang                                        name,
        final ModLang                                        tooltip
    )
    {
        return ClothConfig
            .createBasicConfigEntry(value, generator, name, tooltip)
            .setSaveConsumer(val ->
            {
                if (!val.equals(value.get()))
                {
                    value.set(val);
                    ClothConfig.TO_SAVE.add(spec);
                }
            })
            .build();
    }
    
    //******************************************************************************************************************
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        final Minecraft mc = Minecraft.getInstance();
        ClothConfig.TO_SAVE.clear();
        
        return (parent ->
        {
            final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(ModDefine.MOD_NAME))
                .setAlwaysShowTabs(false)
                .setSavingRunnable(() -> ClothConfig.TO_SAVE.forEach(spec ->
                {
                    if (spec == ModServerConfig.SPEC && mc.getSingleplayerServer() == null)
                    {
                        ModDefine.LOGGER.error("Could not save server config, user is not in a singleplayer world");
                        return;
                    }
                    
                    spec.save();
                }));
            
            builder
                .getOrCreateCategory(ModLang.CONFIG_SCREEN_CATEGORY_RENDERING)
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.pouchContentRenderMode(),
                    ((builder1, title, val) -> builder1.startEnumSelector(
                        title,
                        ModClientConfig.PouchContentRenderMode.class,
                        val)),
                    ModLang.CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE,
                    ModLang.CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.renderToolbeltModel(),
                    ((builder1, title, val) -> builder1.startEnumSelector(
                        title,
                        ModClientConfig.ToolbeltRenderMode.class,
                        val)),
                    ModLang.CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL,
                    ModLang.CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL_TOOLTIP));
            
            final ConfigCategory behaviour_builder = builder
                .getOrCreateCategory(ModLang.CONFIG_SCREEN_CATEGORY_BEHAVIOUR);
            
            if (mc.getSingleplayerServer() != null)
            {
                behaviour_builder.addEntry(ClothConfig.createConfigEntry(
                    ModServerConfig.SPEC,
                    ModServerConfig.INSTANCE.pouchAllowedTools(),
                    ConfigEntryBuilder::startStrList,
                    ModLang.CONFIG_SCREEN_OPTION_ALLOWED_POUCH_TOOLS,
                    ModLang.CONFIG_SCREEN_OPTION_ALLOWED_POUCH_TOOLS_TOOLTIP));
            }
            
            return builder.build();
        });
    }
}
