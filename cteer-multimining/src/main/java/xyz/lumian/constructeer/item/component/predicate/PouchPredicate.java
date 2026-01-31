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
