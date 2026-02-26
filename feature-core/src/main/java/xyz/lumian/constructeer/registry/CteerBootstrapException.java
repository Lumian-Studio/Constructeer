package xyz.lumian.constructeer.registry;



//**********************************************************************************************************************
public class CteerBootstrapException
    extends Exception
{
    //******************************************************************************************************************
    public static CteerBootstrapException downstreamException(final IBootstrap bootstrap, final Throwable cause)
    {
        return new CteerBootstrapException("bootstrapper %s failed unexpectedly".formatted(bootstrap.id()), cause);
    }
    
    //******************************************************************************************************************
    private CteerBootstrapException(final String message)                        { super(message); }
    private CteerBootstrapException(final String message, final Throwable cause) { super(message, cause); }
}
