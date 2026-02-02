/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.multimining.integration;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Function3;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.config.ModClientConfig;
import xyz.lumian.constructeer.multimining.config.ModServerConfig;

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
                    ((builder1, title, val) ->
                         builder1.startEnumSelector(title, ModClientConfig.PouchContentRenderMode.class, val)),
                    ModLang.CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE,
                    ModLang.CONFIG_SCREEN_OPTION_POUCH_CONTENT_RENDER_MODE_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.renderToolbeltModel(),
                    ((builder1, title, val) ->
                         builder1.startEnumSelector(title, ModClientConfig.ToolbeltRenderMode.class, val)),
                    ModLang.CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL,
                    ModLang.CONFIG_SCREEN_OPTION_RENDER_TOOLBELT_MODEL_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.shouldRenderHammerOutline(),
                    ConfigEntryBuilder::startBooleanToggle,
                    ModLang.CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER,
                    ModLang.CONFIG_SCREEN_OPTION_HAMMER_SHOULD_RENDER_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.shouldRenderPlowOutline(),
                    ConfigEntryBuilder::startBooleanToggle,
                    ModLang.CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER,
                    ModLang.CONFIG_SCREEN_OPTION_PLOW_SHOULD_RENDER_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.hammerOutlineColour(),
                    ((builder1, title, val) -> builder1.startColorField(title, (val & 0xFFFFFF))),
                    ModLang.CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR,
                    ModLang.CONFIG_SCREEN_OPTION_HAMMER_OUTLINE_COLOUR_TOOLTIP))
                .addEntry(ClothConfig.createConfigEntry(
                    ModClientConfig.SPEC,
                    ModClientConfig.INSTANCE.plowOutlineColour(),
                    ((builder1, title, val) -> builder1.startColorField(title, (val & 0xFFFFFF))),
                    ModLang.CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR,
                    ModLang.CONFIG_SCREEN_OPTION_PLOW_OUTLINE_COLOUR_TOOLTIP));
            
            return builder.build();
        });
    }
}
