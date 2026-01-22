package xyz.lumian.constructeer.client.data.lang;

import com.google.common.collect.Streams;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.text.WordUtils;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.stat.ModStats;
import xyz.lumian.constructeer.tag.ModItemTags;

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
