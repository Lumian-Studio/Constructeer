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
package xyz.lumian.constructeer.multimining;

import net.fabricmc.api.ModInitializer;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.multimining.config.CteerMultiMiningServerConfig;
import xyz.lumian.constructeer.multimining.entity.CteerMultiMiningDataSerialisers;
import xyz.lumian.constructeer.multimining.entity.CteerMultiMiningEntities;
import xyz.lumian.constructeer.multimining.item.CteerMultiMiningItems;
import xyz.lumian.constructeer.multimining.item.component.CteerMultiMiningDataComponents;
import xyz.lumian.constructeer.multimining.item.multimining.area.CteerAreaProviderTypes;
import xyz.lumian.constructeer.multimining.item.multimining.timber.TimberMode;
import xyz.lumian.constructeer.multimining.registry.CteerMultiMiningRegistries;
import xyz.lumian.constructeer.multimining.sound.CteerMultiMiningSoundEvents;
import xyz.lumian.constructeer.multimining.stat.CteerMultiMiningStats;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public class CteerMultiMining
    implements ModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerMultiMiningRegistries::new)
        .with(CteerMultiMiningDataComponents::new)
        .with(CteerMultiMiningItems::new)
        .with(CteerMultiMiningStats::new)
        .with(CteerMultiMiningEntities::new)
        .with(CteerMultiMiningDataSerialisers::new)
        .with(CteerMultiMiningSoundEvents::new)
        .with(CteerAreaProviderTypes::new)
        .with(IBootstrap.bootstrappable(TimberMode.class, (report -> TimberMode.initialise())));
    
    //******************************************************************************************************************
	@Override
    public void onInitialize()
    {
        Constructeer.registerBootstrapper(CteerMultiMining.LOADER);
        CteerConfigManager.INSTANCE.registerReloadableConfig(
            CteerDefine.id(ModuleDefine.ID),
            CteerMultiMiningServerConfig.INSTANCE);
    }
}
