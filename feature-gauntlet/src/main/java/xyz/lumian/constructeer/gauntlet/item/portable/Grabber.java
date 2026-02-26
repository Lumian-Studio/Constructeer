package xyz.lumian.constructeer.gauntlet.item.portable;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.gauntlet.registry.CteerGauntletRegistries;
import xyz.lumian.constructeer.registry.CteerRegistryEvents;



//**********************************************************************************************************************
public interface Grabber
{
    //******************************************************************************************************************
    Grabber EMPTY = HolderSet::empty;
    Grabber ALL   = new Grabber()
    {
        //**************************************************************************************************************
        private HolderSet<IPortableType<?>> supported = HolderSet.empty();
        
        //**************************************************************************************************************
        {
            CteerRegistryEvents.MC_REGISTRIES_FROZEN_AFTER.register(() -> this.supported
                = HolderSet.direct(CteerGauntletRegistries.PORTABLE_TYPE.listElements().toList()));
        }
        
        //==============================================================================================================
        @Override public HolderSet<IPortableType<?>> supportedTypes() { return this.supported; }
    };
    
    //==================================================================================================================
    Codec<Grabber>                                CODEC        = RegistryCodecs
        .homogeneousList(CteerGauntletRegistries.PORTABLE_TYPE.key())
        .xmap(Grabber::of, Grabber::supportedTypes);
    StreamCodec<RegistryFriendlyByteBuf, Grabber> STREAM_CODEC = ByteBufCodecs
        .holderSet(CteerGauntletRegistries.PORTABLE_TYPE.key())
        .map(Grabber::of, Grabber::supportedTypes);
    
    //******************************************************************************************************************
    @SafeVarargs
    static Grabber of(final Holder<IPortableType<?>> ...types)
    {
        final HolderSet<IPortableType<?>> supported = HolderSet.direct(types);
        return (() -> supported);
    }
    
    static Grabber of(final IPortableType<?>...types)
    {
        final HolderSet<IPortableType<?>> supported = HolderSet
            .direct(CteerGauntletRegistries.PORTABLE_TYPE::wrapAsHolder, types);
        return (() -> supported);
    }
    
    static Grabber of(final HolderSet<IPortableType<?>> types) { return (() -> types); }
    
    //******************************************************************************************************************
    HolderSet<IPortableType<?>> supportedTypes();
    
    //==================================================================================================================
    @SuppressWarnings("unchecked")
    default boolean supports(final Holder<? extends IPortableType<?>> type)
    {
        return this.supportedTypes().contains((Holder<IPortableType<?>>) type);
    }
    
    default boolean supports(final IPortableType<?> type)
    {
        for (final var supported : this.supportedTypes())
        {
            if (supported.value() == type)
            {
                return true;
            }
        }
        
        return false;
    }
    
    default <T> boolean supports(final Identifier typeId)
    {
        return CteerGauntletRegistries.PORTABLE_TYPE.get(typeId).map(this.supportedTypes()::contains).orElse(false);
    }
}
