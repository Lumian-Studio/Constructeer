package xyz.lumian.constructeer.util;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;



//**********************************************************************************************************************
public abstract class Freezable<Self extends Freezable<Self>>
{
    //******************************************************************************************************************
    public record Scoped<T extends Freezable<T>>(T object)
        implements Closeable
    {
        //**************************************************************************************************************
        @Override public void close() { this.object.freeze(); }
    }
    
    //******************************************************************************************************************
    public static <T extends Freezable<T>> Scoped<T> using(final T object)
    {
        return new Scoped<>(object);
    }
    
    //******************************************************************************************************************
    private boolean frozen = false;
    
    //******************************************************************************************************************
    public boolean isFrozen() { return this.frozen; }
    
    //==================================================================================================================
    public void ifFrozen(final Consumer<Self> consumer)
    {
        if (this.frozen)
        {
            //noinspection unchecked
            consumer.accept((Self) this);
        }
    }
    
    public void ifMutable(final Consumer<Self> consumer)
    {
        if (!this.frozen)
        {
            //noinspection unchecked
            consumer.accept((Self) this);
        }
    }
    
    public <T> T map(final Function<Self, T> ifMutable, final Function<Self, T> ifFrozen)
    {
        //noinspection unchecked
        return (this.frozen ? ifFrozen : ifMutable).apply((Self) this);
    }
    
    //==================================================================================================================
    public void freeze()
    {
        this.frozen = true;
    }
    
    //==================================================================================================================
    protected void assertFrozen()
    {
        if (this.frozen)
        {
            throw new UnsupportedOperationException("object is already frozen");
        }
    }
}
