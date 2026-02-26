package xyz.lumian.constructeer.registry;

import org.intellij.lang.annotations.PrintFormat;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;



//**********************************************************************************************************************
public abstract class BootstrapReport
{
    //******************************************************************************************************************
    public static final class DebugReport
        extends BootstrapReport
    {
        //**************************************************************************************************************
        private final Logger logger;
        
        //**************************************************************************************************************
        public DebugReport(final Logger logger) { this.logger = Objects.requireNonNull(logger); }
        
        //==============================================================================================================
        @Override public void report(final Supplier<String> message) { this.logger.debug("-- {}", message.get()); }
    }
    
    public static final class EmptyReport
        extends BootstrapReport
    {
        //**************************************************************************************************************
        public static final EmptyReport INSTANCE = new EmptyReport();
        
        //**************************************************************************************************************
        private EmptyReport() {}
        
        //==============================================================================================================
        @Override public void report(final Supplier<String> message) {}
    }
    
    //******************************************************************************************************************
    public abstract void report(Supplier<String> message);
    
    public void report(@PrintFormat final String message, final Object... args)
    {
        this.report(() -> message.formatted(args));
    }
}
