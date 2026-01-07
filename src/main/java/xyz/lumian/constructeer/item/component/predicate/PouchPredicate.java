package xyz.lumian.constructeer.item.component.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.PouchContent;

import java.util.Optional;



//**********************************************************************************************************************
public record PouchPredicate(Optional<ItemPredicate> predicate)
    implements SingleComponentItemPredicate<PouchContent>
{
    //******************************************************************************************************************
	public static final Codec<PouchPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(
            ItemPredicate.CODEC
                .optionalFieldOf("predicate")
                .forGetter(PouchPredicate::predicate))
        .apply(instance, PouchPredicate::new));

    //******************************************************************************************************************
	@Override public DataComponentType<PouchContent> componentType() { return ModComponents.POUCH_CONTENT; }

    @Override
	public boolean matches(final PouchContent content)
    {
		return (this.predicate.isEmpty() || this.predicate.orElseThrow().test(content.content()));
	}
}
