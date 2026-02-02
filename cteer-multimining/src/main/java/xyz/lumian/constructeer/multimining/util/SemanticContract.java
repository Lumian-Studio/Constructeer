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
package xyz.lumian.constructeer.multimining.util;


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
