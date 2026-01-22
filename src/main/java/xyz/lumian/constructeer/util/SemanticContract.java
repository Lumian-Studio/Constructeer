package xyz.lumian.constructeer.util;


import java.lang.annotation.*;



//**********************************************************************************************************************
public final class SemanticContract
{
    //******************************************************************************************************************
    /// Object should only happen on the logical server, things like ServerLevel, ServerPlayer are guaranteed here.
    /// This annotation is solely for documentation purposes and tells the developer that using it in the wrong
    /// context will lead to exceptions.
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface Server {}
    
    /// Object should only happen on the logical client, things like ClientLevel, LocalPlayer, are guaranteed here.
    /// This annotation is solely for documentation purposes and tells the developer that using it in the wrong
    /// context will lead to exceptions.
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface Client {}
    
    /// Any parameter type that has a deep copy method like [net.minecraft.world.item.ItemStack] expects to be copied
    /// at some point before being passed.
    /// This annotation is solely for documentation purposes and tells the developer that not copying the object
    /// may lead to its modification and in turn also modifying the original object, or that the function does deferred
    /// operations on the object and any modifications to the original might disturb the execution of the method.
    @Documented
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.CLASS)
    public @interface Copied {}
    
    //******************************************************************************************************************
    private SemanticContract() {}
}
