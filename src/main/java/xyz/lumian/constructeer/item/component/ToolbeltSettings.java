package xyz.lumian.constructeer.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.ArrayUtils;
import xyz.lumian.constructeer.item.ToolbeltItem;

import java.util.Arrays;
import java.util.List;



//**********************************************************************************************************************
public final class ToolbeltSettings
{
    //******************************************************************************************************************
    public static final ToolbeltSettings DEFAULT = new ToolbeltSettings(new LockMode[0]);
    public static final Codec<ToolbeltSettings> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolbeltSettings> STREAM_CODEC;
    
    //==================================================================================================================
    static
    {
        CODEC        = RecordCodecBuilder.create(instance -> instance
            .group(
                LockMode.CODEC.sizeLimitedListOf(ToolbeltItem.COUNT_POUCHES)
                    .xmap((list -> list.toArray(LockMode[]::new)), List::of)
                    .fieldOf("modes")
                    .forGetter((settings -> settings.modes)))
            .apply(instance, ToolbeltSettings::new));
        STREAM_CODEC = StreamCodec.composite(
            LockMode.STREAM_CODEC
                .apply(ByteBufCodecs.list(ToolbeltItem.COUNT_POUCHES))
                .map((list -> list.toArray(LockMode[]::new)), List::of), (settings -> settings.modes),
            ToolbeltSettings::new);
    }
    
    //******************************************************************************************************************
    private final LockMode[] modes = new LockMode[ToolbeltItem.COUNT_POUCHES];
    
    //******************************************************************************************************************
    public ToolbeltSettings(final LockMode[] modes)
    {
        Arrays.fill(this.modes, LockMode.UNLOCKED);
        ArrayUtils.arraycopy(modes, 0, this.modes, 0, Math.min(modes.length, this.modes.length));
    }
    
    //==================================================================================================================
    public LockMode getMode(final int id)
    {
        if (id < 0 || id >= this.modes.length)
        {
            throw new IndexOutOfBoundsException("pouch id " + id + " is out of bounds (of max. "
                                                + (ToolbeltItem.COUNT_POUCHES - 1) + ")");
        }
        
        return this.modes[id];
    }
    
    //==================================================================================================================
    public List<LockMode> getModes() { return ImmutableList.copyOf(this.modes); }
    
    //==================================================================================================================
    public ToolbeltSettings withModes(final LockMode[] modes)
    {
        return new ToolbeltSettings(modes);
    }
    
    public ToolbeltSettings withMode(final int id, final LockMode mode)
    {
        if (id < 0 || id >= this.modes.length)
        {
            throw new IndexOutOfBoundsException("pouch id " + id + " is out of bounds (of max. "
                                                + (ToolbeltItem.COUNT_POUCHES - 1) + ")");
        }
        
        final LockMode[] temp_arr = Arrays.copyOf(this.modes, this.modes.length);
        temp_arr[id] = mode;
        return this.withModes(temp_arr);
    }
}
