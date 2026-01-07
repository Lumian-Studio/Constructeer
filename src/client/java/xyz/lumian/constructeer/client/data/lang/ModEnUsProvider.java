package xyz.lumian.constructeer.client.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.text.WordUtils;
import xyz.lumian.constructeer.ModLang;
import xyz.lumian.constructeer.item.ModItems;
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
        builder.add(ModItemTags.POUCHES, "Pouches");
        
        // Items
        builder.add(ModItems.TOOLBELT, "Toolbelt");
        builder.add(ModItems.POUCH, "Pouch");
        Arrays.stream(DyeColor.values()).forEach(dye -> builder.add(
            ModItems.POUCH_BY_DYE.get(dye),
            (WordUtils.capitalizeFully(dye.getName().replace('_', ' ')) + " Pouch")));
        
        Arrays.stream(ModLang.values())
            .filter(lang -> lang.shouldGenerate)
            .forEach(comp -> builder.add(comp.getKey(), comp.englishDefault.get()));
    }
}
