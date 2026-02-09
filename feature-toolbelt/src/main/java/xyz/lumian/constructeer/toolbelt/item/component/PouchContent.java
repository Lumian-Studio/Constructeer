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
package xyz.lumian.constructeer.toolbelt.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import xyz.lumian.constructeer.toolbelt.CteerToolbeltDictionary;

import java.util.Objects;
import java.util.function.Consumer;



//**********************************************************************************************************************
public record PouchContent(ItemStack content)
    implements TooltipProvider
{
    //******************************************************************************************************************
    public static final PouchContent EMPTY = new PouchContent(ItemStack.EMPTY.copy());
    
    public static Codec<PouchContent> CODEC = ItemStack.OPTIONAL_CODEC
        .xmap(PouchContent::new, PouchContent::content);
    public static StreamCodec<RegistryFriendlyByteBuf, PouchContent> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
        .map(PouchContent::new, PouchContent::content);
    
    //******************************************************************************************************************
    public boolean isEmpty() { return this.content.isEmpty(); }
    
    //==================================================================================================================
    public MutableComponent toComponent()
    {
        final Identifier id = Objects.requireNonNull(this.content.get(DataComponents.ITEM_MODEL)).withPrefix("item/");
        return Component.object(new AtlasSprite(AtlasIds.ITEMS, id));
    }
    
    //==================================================================================================================
    @Override
    public void addToTooltip(final Item.TooltipContext ctx, final Consumer<Component> consumer,
                             final TooltipFlag flag, final DataComponentGetter components)
    {
        if (!this.content.isEmpty())
        {
            consumer.accept(CteerToolbeltDictionary.POUCH_TOOLTIP_CONTENT
                .withArgs(this.toComponent())
                .withStyle(ChatFormatting.GRAY));
        }
    }
}
