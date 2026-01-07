package xyz.lumian.constructeer.client.renderer.item.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.PouchContent;



//**********************************************************************************************************************
public record PouchHasContent()
    implements ConditionalItemModelProperty
{
    //******************************************************************************************************************
    public static final MapCodec<PouchHasContent> MAP_CODEC = MapCodec.unit(new PouchHasContent());

    //******************************************************************************************************************
    @Override
    public boolean get(final ItemStack stack, final @Nullable ClientLevel level, final @Nullable LivingEntity entity,
                       final int i, final ItemDisplayContext ctx)
    {
        return !stack.getOrDefault(ModComponents.POUCH_CONTENT, PouchContent.EMPTY).isEmpty();
    }

    @Override public MapCodec<PouchHasContent> type() { return PouchHasContent.MAP_CODEC; }
}
