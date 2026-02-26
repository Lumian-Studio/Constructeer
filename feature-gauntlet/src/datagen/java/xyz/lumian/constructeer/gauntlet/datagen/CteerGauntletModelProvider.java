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
package xyz.lumian.constructeer.gauntlet.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.EmptyModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.world.item.ItemDisplayContext;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;

import java.util.List;



//**********************************************************************************************************************
public class CteerGauntletModelProvider
    extends FabricModelProvider
{
    //******************************************************************************************************************
    public CteerGauntletModelProvider(final FabricDataOutput output) { super(output); }
    
    //==================================================================================================================
    @Override public void generateBlockStateModels(final BlockModelGenerators generator) {}
    
    @Override
    public void generateItemModels(final ItemModelGenerators generator)
    {
        final ItemModel.Unbaked flat = ItemModelUtils.plainModel(generator.createFlatItemModel(
            CteerGauntletItems.GAUNTLET_OF_POWER,
            ModelTemplates.FLAT_ITEM));
        generator.itemModelOutput.accept(
            CteerGauntletItems.GAUNTLET_OF_POWER,
            ItemModelUtils.select(new DisplayContext(),
                flat,
                ItemModelUtils.when(
                    List.of(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND),
                    new EmptyModel.Unbaked())));
    }
}
