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
package xyz.lumian.constructeer.toolbelt.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import org.apache.commons.lang3.text.WordUtils;
import xyz.lumian.constructeer.data.IDictionary;
import xyz.lumian.constructeer.toolbelt.CteerToolbeltDictionary;
import xyz.lumian.constructeer.toolbelt.registry.CteerToolbeltTags;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;

import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class EnUs
    extends IDictionary.Provider<CteerToolbeltDictionary>
{
    //******************************************************************************************************************
    public EnUs(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup,
                final String lang, final CteerToolbeltDictionary[] values)
    {
        super(output, lookup, lang, values);
    }
    
    //==================================================================================================================
    @Override
    public void generateAdditional(final HolderLookup.Provider lookup, final TranslationBuilder builder)
    {
        // Tags
        builder.add(CteerToolbeltTags.POUCHES, "Pouches");
        
        // Items
        builder.add(CteerToolbeltItems.TOOLBELT, "Toolbelt");
        builder.add(CteerToolbeltItems.POUCH,    "Pouch");
        
        //noinspection deprecation
        CteerToolbeltItems.POUCH_BY_DYE.forEach((dye, item) ->
            builder.add(item, (WordUtils.capitalizeFully(dye.getName().replace('_', ' ') + " Pouch"))));
    }
}
