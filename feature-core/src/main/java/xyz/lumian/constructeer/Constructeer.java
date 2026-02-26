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
package xyz.lumian.constructeer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import org.intellij.lang.annotations.PrintFormat;
import xyz.lumian.constructeer.config.CteerConfigManager;
import xyz.lumian.constructeer.impl.BootstrapRegistryExtension;
import xyz.lumian.constructeer.network.CteerNetworkRegistry;
import xyz.lumian.constructeer.player.CteerPlayerAttachments;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.CteerItemRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;

import java.util.function.Supplier;



//**********************************************************************************************************************
public class Constructeer
    implements ModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerNetworkRegistry::new)
        .with(CteerItemRegistry::new)
        .with(IBootstrap.freezable(CteerConfigManager.class, CteerConfigManager::freeze))
        .with(CteerPlayerAttachments::new);
    
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("InstantiationOfUtilityClass")
    private static final BootstrapRegistryExtension BOOTSTRAP_REGISTRY
        = ((BootstrapRegistryExtension) new BuiltInRegistries());
    
    //******************************************************************************************************************
    public static IBootstrap.Loader registerBootstrapper(final IBootstrap.Loader loader)
    {
        Constructeer.BOOTSTRAP_REGISTRY.constructeer$registerBootstrapper(loader);
        return loader;
    }
    
    public static void sendGlobalBootstrapReport(final Supplier<String> message)
    {
        final BootstrapReport report = Constructeer.BOOTSTRAP_REGISTRY.constructeer$getCurrentReport();
        
        if (report != null)
        {
            report.report(message);
        }
    }
    
    public static void sendGlobalBootstrapReport(@PrintFormat final String message, final Object ...args)
    {
        final BootstrapReport report = Constructeer.BOOTSTRAP_REGISTRY.constructeer$getCurrentReport();
        
        if (report != null)
        {
            report.report(message, args);
        }
    }
    
    //******************************************************************************************************************
	@Override
	public void onInitialize()
    {
        Constructeer.registerBootstrapper(Constructeer.LOADER);
	}
}
