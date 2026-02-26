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
package xyz.lumian.constructeer.multimining.registry;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMining;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMiningType;
import xyz.lumian.constructeer.multimining.item.multimining.area.IAreaProvider;
import xyz.lumian.constructeer.multimining.item.multimining.timber.IJustinTimbermode;
import xyz.lumian.constructeer.registry.CteerRegistries;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public final class CteerMultiMiningRegistries
    implements IBootstrap
{
    //******************************************************************************************************************
    // Static registries
    /// See [IAreaProvider.Type].
    public static final Registry<IAreaProvider.Type<?>> AREA_PROVIDER_TYPE;
    
    /// See [IJustinTimbermode].
    public static final Registry<IJustinTimbermode> MULTI_MINING_TIMBER_MODE;
    
    /// See [MultiMiningType].
    public static final Registry<MultiMiningType<?>> MULTI_MINING_TYPE;
    
    // Dynamic registries
    /// See [MultiMining].
    public static final ResourceKey<Registry<MultiMining>> MULTI_MINING_PROVIDER;
    
    //==================================================================================================================
    static
    {
        AREA_PROVIDER_TYPE       = CteerRegistries
            .registerRegistry(CteerDefine.id("area_provider_type"), null, RegistryAttribute.OPTIONAL);
        MULTI_MINING_TIMBER_MODE = CteerRegistries
            .registerRegistry(CteerDefine.id("multi_mining_timber_mode"), null, RegistryAttribute.SYNCED);
        MULTI_MINING_TYPE        = CteerRegistries
            .registerRegistry(CteerDefine.id("multi_mining_type"), null, RegistryAttribute.SYNCED);
        MULTI_MINING_PROVIDER    = CteerRegistries.registerDynamicSyncedRegistry(
            CteerDefine.id("multi_mining_provider"),
            MultiMining.MAP_CODEC.codec());
    }
}
