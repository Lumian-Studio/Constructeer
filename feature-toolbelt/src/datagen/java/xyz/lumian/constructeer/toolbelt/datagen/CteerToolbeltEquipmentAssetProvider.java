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

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltEquipmentAssets;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;



//**********************************************************************************************************************
public class CteerToolbeltEquipmentAssetProvider
    extends FabricCodecDataProvider<EquipmentClientInfo>
{
    //******************************************************************************************************************
    public CteerToolbeltEquipmentAssetProvider(final FabricDataOutput output,
                                               final CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup, PackOutput.Target.RESOURCE_PACK, "equipment", EquipmentClientInfo.CODEC);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Toolbelt Equipment Asset Provider"; }
    
    //==================================================================================================================
    @Override
    protected void configure(final BiConsumer<Identifier, EquipmentClientInfo> output,
                             final HolderLookup.Provider                       lookup)
    {
        output.accept(
            CteerToolbeltEquipmentAssets.TOOLBELT.identifier(),
            EquipmentClientInfo.builder()
                .addLayers(
                    EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                    new EquipmentClientInfo.Layer(
                        CteerToolbeltEquipmentAssets.TOOLBELT.identifier(),
                        Optional.empty(), false))
                .build());
    }
}
