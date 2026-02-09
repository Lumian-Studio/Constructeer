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
package xyz.lumian.constructeer.toolbelt.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.client.renderer.item.conditional.MenuComplies;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.predicate.InPouchSlot;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.PouchContainedItemSpecialRenderer;
import xyz.lumian.constructeer.toolbelt.client.renderer.item.conditional.PouchHasContent;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;



//**********************************************************************************************************************
public class CteerToolbeltModelProvider
    extends FabricModelProvider
{
    //******************************************************************************************************************
    public CteerToolbeltModelProvider(final FabricDataOutput output) { super(output); }
    
    //==================================================================================================================
    @Override public void generateBlockStateModels(final BlockModelGenerators generator) {}
    
    @Override
    public void generateItemModels(final ItemModelGenerators generator)
    {
        generator.generateFlatItem(CteerToolbeltItems.TOOLBELT, ModelTemplates.FLAT_ITEM);
        
        this.generatePouchModel(generator, CteerToolbeltItems.POUCH);
        CteerToolbeltItems.POUCH_BY_DYE.values().forEach(item -> this.generatePouchModel(generator, item));
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private void generatePouchModel(final ItemModelGenerators generator, final Item item)
    {
        final Identifier pouch_model = ModelTemplates.FLAT_ITEM
            .create(item, TextureMapping.layer0(item), generator.modelOutput);
        final Identifier full_model  = pouch_model.withSuffix("_full");
        ModelTemplates.FLAT_ITEM.create(full_model, TextureMapping.layer0(full_model), generator.modelOutput);
        
        final ItemModel.Unbaked full_unbaked = ItemModelUtils.plainModel(full_model);
        generator.itemModelOutput.accept(item, ItemModelUtils.conditional(new PouchHasContent(),
            // has content
            ItemModelUtils.conditional(MenuComplies.anyMenu(InPouchSlot.INSTANCE),
                // is inside pouch slot
                ItemModelUtils.composite(full_unbaked, new PouchContainedItemSpecialRenderer.Unbaked()),
                
                // is inside any other slot
                full_unbaked),
            
            // has no content
            ItemModelUtils.plainModel(pouch_model)));
    }
}
