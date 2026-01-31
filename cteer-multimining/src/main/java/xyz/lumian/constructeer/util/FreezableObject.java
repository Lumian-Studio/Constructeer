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
package xyz.lumian.constructeer.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;



//**********************************************************************************************************************
@NullUnmarked
public class FreezableObject<T>
{
    //******************************************************************************************************************
    @NullMarked
    public static class Deferred<T>
        extends FreezableObject<@Nullable T>
    {
        //**************************************************************************************************************
        public Deferred() { super(null); }
        
        //==============================================================================================================
        @Override
        public T get()
        {
            final T obj = super.get();
            
            if (obj == null)
            {
                throw new IllegalStateException("value has not been set");
            }
            
            return obj;
        }
        
        public Optional<T> getOptional() { return Optional.ofNullable(super.get()); }
        
        @NullUnmarked
        public T orElse(final T fallback) { return (this.isSet() ? super.get() : fallback); }
        
        @NullUnmarked
        public T orElseGet(final Supplier<T> fallback) { return (this.isSet() ? super.get() : fallback.get()); }
        
        //==============================================================================================================
        public boolean isSet()   { return (super.get() != null); }
        public boolean isUnset() { return (super.get() == null); }
        
        //==============================================================================================================
        public <U> Optional<U> map(final Function<T, U> mapper)
        {
            return (this.isSet() ? Optional.of(mapper.apply(super.get())) : Optional.empty());
        }
        
        public <U> Optional<U> flatMap(final Function<T, Optional<U>> mapper)
        {
            return (this.isSet() ? mapper.apply(super.get()) : Optional.empty());
        }
        
        public Optional<T> filter(final Predicate<T> predicate)
        {
            return this.getOptional().filter(predicate);
        }
        
        public void ifUnset(final Runnable runnable)
        {
            if (!this.isSet())
            {
                runnable.run();
            }
        }
        
        public void ifSet(final Consumer<T> consumer)
        {
            if (this.isSet())
            {
                consumer.accept(super.get());
            }
        }
        
        //==============================================================================================================
        @Override
        public void set(final T value)
        {
            super.set(Objects.requireNonNull(value));
            super.freeze();
        }
        
        public T setIfUnset(final Supplier<T> supplier)
        {
            if (!this.isSet())
            {
                this.setAndFreeze(supplier.get());
            }
            
            return Objects.requireNonNull(super.get());
        }
        
        @Override
        public void setAndFreeze(final T value)
        {
            super.set(Objects.requireNonNull(value));
            super.freeze();
        }
    }
    
    //******************************************************************************************************************
    private T                object;
    private volatile boolean freeze = false;
    
    //******************************************************************************************************************
    public FreezableObject(final T initialValue) { this.object = initialValue; }
    public FreezableObject() {}
    
    //==================================================================================================================
    public T get() { return this.object; }
    
    //==================================================================================================================
    public void set(final T value)
    {
        if (this.freeze)
        {
            throw new IllegalStateException("object has already been frozen");
        }
        
        this.object = value;
    }
    
    public void setAndFreeze(final T value)
    {
        this.set(value);
        this.freeze();
    }
    
    public void freeze() { this.freeze = true; }
}
