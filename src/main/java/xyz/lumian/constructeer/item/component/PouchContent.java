package xyz.lumian.constructeer.item.component;

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
import xyz.lumian.constructeer.ModLang;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;



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
            consumer.accept(ModLang.POUCH_TOOLTIP_CONTENT.withArgs(this.toComponent()).withStyle(ChatFormatting.GRAY));
        }
    }
}
