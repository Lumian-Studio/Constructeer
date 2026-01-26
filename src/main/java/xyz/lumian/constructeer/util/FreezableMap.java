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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;



//**********************************************************************************************************************
public class FreezableMap<K, V>
    implements Map<K, V>
{
    //******************************************************************************************************************
    private final Map<K, V> underlyingMap;
    
    private volatile boolean frozen = false;
    
    //******************************************************************************************************************
    public FreezableMap(final Map<K, V> underlyingMap)
    {
        this.underlyingMap = underlyingMap;
    }
    
    //==================================================================================================================
    public void freeze() { this.frozen = true; }
    
    //==================================================================================================================
    @Override public V get(final Object key) { return this.underlyingMap.get(key); }
    
    @Override
    public V getOrDefault(final Object key, final V defaultValue)
    {
        return this.underlyingMap.getOrDefault(key, defaultValue);
    }
    
    @Override
    public Set<K> keySet()
    {
        if (this.frozen)
        {
            return ImmutableSet.copyOf(this.underlyingMap.keySet());
        }
        
        return this.underlyingMap.keySet();
    }
    
    @Override
    public Collection<V> values()
    {
        if (this.frozen)
        {
            return ImmutableList.copyOf(this.underlyingMap.values());
        }
        
        return this.underlyingMap.values();
    }
    
    @Override
    public Set<Entry<K, V>> entrySet()
    {
        if (this.frozen)
        {
            return ImmutableSet.copyOf(this.underlyingMap.entrySet());
        }
        
        return this.underlyingMap.entrySet();
    }
    
    //==================================================================================================================
    @Override public int size() { return this.underlyingMap.size(); }
    
    @Override public boolean isEmpty() { return this.underlyingMap.isEmpty(); }
    
    @Override public boolean containsKey(final Object key) { return this.underlyingMap.containsKey(key); }
    
    @Override public boolean containsValue(final Object value) { return this.underlyingMap.containsValue(value); }
    
    //==================================================================================================================
    @Override public void forEach(final BiConsumer<? super K, ? super V> action) { this.underlyingMap.forEach(action); }
    
    //==================================================================================================================
    @Override
    public @Nullable V put(final K key, final V value)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.put(key, value);
    }
    
    @Override
    public V remove(final Object key)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.remove(key);
    }
    
    @Override
    public boolean remove(final Object key, final Object value)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.remove(key, value);
    }
    
    @Override
    public @Nullable V replace(final K key, final V value)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.replace(key, value);
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.replace(key, oldValue, newValue);
    }
    
    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        this.underlyingMap.replaceAll(function);
    }
    
    @Override
    public @Nullable V putIfAbsent(final K key, final V value)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.putIfAbsent(key, value);
    }
    
    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.compute(key, remappingFunction);
    }
    
    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.computeIfAbsent(key, mappingFunction);
    }
    
    @Override
    public @Nullable V computeIfPresent(final K key,
                                        final BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.computeIfPresent(key, remappingFunction);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        this.underlyingMap.putAll(m);
    }
    
    @Override
    public void clear()
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        this.underlyingMap.clear();
    }
    
    @Override
    @SuppressWarnings("NullableProblems")
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("map is already frozen");
        }
        
        return this.underlyingMap.merge(key, value, remappingFunction);
    }
    
    //==================================================================================================================
    @Override public String toString() { return this.underlyingMap.toString(); }
    @Override public int    hashCode() { return this.underlyingMap.hashCode(); }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)                               return true;
        if (!(obj instanceof FreezableMap<?,?> other)) return false;
        return this.underlyingMap.equals(other.underlyingMap);
    }
}
