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

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMining;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMiningType;
import xyz.lumian.constructeer.multimining.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.multimining.item.multimining.timber.IJustinTimbermode;
import xyz.lumian.constructeer.registry.CteerRegistries;



//**********************************************************************************************************************
public final class CteerMultiMiningRegistries
{
    //******************************************************************************************************************
    // Static registry keys
    public static final Registry<AreaProviderType<?>> AREA_PROVIDER_TYPE
        = CteerRegistries.registerOptional("area_provider_type", null);
    
    public static final Registry<IJustinTimbermode> MULTI_MINING_TIMBER_MODE
        = CteerRegistries.registerSynced("multi_mining_timber_mode", null);
    
    public static final Registry<MultiMiningType<?>> MULTI_MINING_TYPE
        = CteerRegistries.registerSynced("multi_mining_type", null);
    
    // Dynamic registry keys
    public static final ResourceKey<Registry<MultiMining>> MULTI_MINING_PROVIDER
        = CteerRegistries.registerDynamicSynced("multi_mining_provider", MultiMining.MAP_CODEC.codec());
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private CteerMultiMiningRegistries() {}
}
