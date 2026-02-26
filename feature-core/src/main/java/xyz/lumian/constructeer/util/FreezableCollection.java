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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;



//**********************************************************************************************************************
@NullUnmarked
public class FreezableCollection<T>
    extends Freezable<FreezableCollection<T>>
    implements Collection<T>
{
    //******************************************************************************************************************
    private final Collection<T> underlyingCollection;
    
    //******************************************************************************************************************
    public FreezableCollection(final Collection<T> underlyingCollection)
    {
        this.underlyingCollection = Objects.requireNonNull(underlyingCollection);
    }
    
    //==================================================================================================================
    @Override public int size() { return this.underlyingCollection.size(); }
    
    @Override public @NonNull Stream<T> stream()         { return this.underlyingCollection.stream(); }
    @Override public @NonNull Stream<T> parallelStream() { return this.underlyingCollection.parallelStream(); }
    
    //==================================================================================================================
    @Override public boolean isEmpty() { return this.underlyingCollection.isEmpty(); }
    
    @Override public boolean contains(final Object o) { return this.underlyingCollection.contains(o); }
    
    @Override
    public boolean containsAll(final @NonNull Collection<?> c)
    {
        return this.underlyingCollection.containsAll(c);
    }
    
    //==================================================================================================================
    @Override public @NonNull Object[]  toArray()                      { return this.underlyingCollection.toArray(); }
    @Override public @NonNull <T1> T1[] toArray(final T1 @NonNull[] a) { return this.underlyingCollection.toArray(a); }
    
    @Override
    public <T1> T1[] toArray(final @NonNull IntFunction<T1[]> generator)
    {
        return this.underlyingCollection.toArray(generator);
    }
    
    //==================================================================================================================
    @Override
    public boolean add(final T t)
    {
        this.assertFrozen();
        return this.underlyingCollection.add(t);
    }
    
    @Override
    public boolean addAll(final @NonNull Collection<? extends T> c)
    {
        this.assertFrozen();
        return this.underlyingCollection.addAll(c);
    }
    
    @Override
    public boolean remove(final Object o)
    {
        this.assertFrozen();
        return this.underlyingCollection.remove(o);
    }
    
    @Override
    public boolean removeAll(final @NonNull Collection<?> c)
    {
        this.assertFrozen();
        return this.underlyingCollection.removeAll(c);
    }
    
    @Override
    public boolean removeIf(final @NonNull Predicate<? super T> filter)
    {
        this.assertFrozen();
        return this.underlyingCollection.removeIf(filter);
    }
    
    @Override
    public boolean retainAll(final @NonNull Collection<?> c)
    {
        this.assertFrozen();
        return this.underlyingCollection.retainAll(c);
    }
    
    @Override
    public void clear()
    {
        this.assertFrozen();
        this.underlyingCollection.clear();
    }
    
    //==================================================================================================================
    @Override public Iterator<T>             iterator()    { return this.underlyingCollection.iterator(); }
    @Override public @NonNull Spliterator<T> spliterator() { return this.underlyingCollection.spliterator(); }
    
    //==================================================================================================================
    @Override public String toString() { return this.underlyingCollection.toString(); }
    @Override public int    hashCode() { return this.underlyingCollection.hashCode(); }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)                                    return true;
        if (!(obj instanceof FreezableCollection<?> other)) return false;
        return this.underlyingCollection.equals(other.underlyingCollection);
    }
}
