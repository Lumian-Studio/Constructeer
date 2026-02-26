package xyz.lumian.constructeer.impl;

import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public interface BootstrapRegistryExtension
{
    //******************************************************************************************************************
    void constructeer$registerBootstrapper(IBootstrap.Loader loader);
    @Nullable BootstrapReport constructeer$getCurrentReport();
}
