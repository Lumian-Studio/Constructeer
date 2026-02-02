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
package xyz.lumian.constructeer.multimining.data.lang;

import com.google.common.collect.Streams;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.text.WordUtils;
import xyz.lumian.constructeer.multimining.ModLang;
import xyz.lumian.constructeer.multimining.item.ModItems;
import xyz.lumian.constructeer.multimining.stat.ModStats;
import xyz.lumian.constructeer.multimining.tag.ModItemTags;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class ModEnUsProvider
    extends FabricLanguageProvider
{
    //******************************************************************************************************************
    public ModEnUsProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, "en_us", lookup);
    }
    
    //==================================================================================================================
    @SuppressWarnings("deprecation")
    @Override
    public void generateTranslations(final HolderLookup.Provider lookup, final TranslationBuilder builder)
    {
        // Tags
        builder.add(ModItemTags.POUCHES,            "Pouch Items");
        builder.add(ModItemTags.MULTI_MINING_TOOLS, "Multi-Block Mining Tools");
        builder.add(ModItemTags.HAMMERS,            "Hammer Items");
        builder.add(ModItemTags.PLOWS,              "Plow Items");
        builder.add(ModItemTags.SAWS,               "Saw Items");
        
        // Items
        builder.add(ModItems.TOOLBELT, "Toolbelt");
        
        builder.add(ModItems.POUCH, "Pouch");
        Arrays.stream(DyeColor.values()).forEach(dye -> builder.add(
            ModItems.POUCH_BY_DYE.get(dye),
            (WordUtils.capitalizeFully(dye.getName().replace('_', ' ')) + " Pouch")));
        
        Streams
            .concat(
                ModItems.HAMMER_BY_MATERIAL.values().stream(),
                ModItems.PLOW_BY_MATERIAL  .values().stream(),
                ModItems.SAW_BY_MATERIAL   .values().stream()
            )
            .forEach(item -> builder.add(
                item,
                WordUtils.capitalizeFully(BuiltInRegistries.ITEM.getKey(item).getPath().replace('_', ' '))));
        
        // Stats
        builder.add(("stat." + ModStats.HAMMER_USED.toLanguageKey()), "Hammers used");
        builder.add(("stat." + ModStats.PLOW_USED  .toLanguageKey()), "Plows used");
        
        // Static keys
        Arrays.stream(ModLang.values())
            .filter(lang -> lang.shouldGenerate)
            .forEach(comp -> builder.add(comp.getKey(), comp.englishDefault.get()));
    }
}
