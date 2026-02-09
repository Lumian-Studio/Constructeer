package xyz.lumian.constructeer.config;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;



//**********************************************************************************************************************
public abstract class ReloadableConfig<Self extends ReloadableConfig<Self>>
{
    //******************************************************************************************************************
    private final Set<BiConsumer<Self, HolderLookup.Provider>> listeners = new HashSet<>();
    
    //******************************************************************************************************************
    public void addListener(final BiConsumer<Self, HolderLookup.Provider> listener)
    {
        this.listeners.add(listener);
    }
    
    public void addListener(final Consumer<Self> listener)
    {
        this.listeners.add((self, lookup) -> listener.accept(self));
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    void invoke(final HolderLookup.Provider lookup)
    {
        this.listeners.forEach(listener -> listener.accept((Self) this, lookup));
    }
    
    //==================================================================================================================
    public abstract ModConfigSpec spec();
}
