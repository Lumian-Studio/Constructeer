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
package xyz.lumian.constructeer.item.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xyz.lumian.constructeer.item.PouchItem;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.PouchContent;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;



//**********************************************************************************************************************
public class ShapelessPouchRecipe
    extends ShapelessRecipe
{
    //******************************************************************************************************************
    public static class Serializer
        extends ShapelessRecipe.Serializer
    {
        //**************************************************************************************************************
		private static final MapCodec<ShapelessRecipe>                             CODEC;
		private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC;
        
        //==============================================================================================================
        static
        {
            CODEC = RecordCodecBuilder.<ShapelessPouchRecipe>mapCodec(instance -> instance
                .group(
                    Codec.STRING
                        .optionalFieldOf("group", "")
                        .forGetter(ShapelessPouchRecipe::group),
                    CraftingBookCategory.CODEC
                        .fieldOf("category")
                        .orElse(CraftingBookCategory.EQUIPMENT)
                        .forGetter(ShapelessRecipe::category),
                    ItemStack.CODEC
                        .fieldOf("pouch_result")
                        .forGetter(recipe -> recipe.result),
                    Ingredient.CODEC
                        .fieldOf("ingredient")
                        .forGetter(recipe -> recipe.ingredients.getFirst()),
                    Ingredient.CODEC.sizeLimitedListOf(8)
                        .fieldOf("additional_ingredients")
                        .forGetter(recipe -> recipe.ingredients.subList(1, Math.min(8, recipe.ingredients.size()))))
                    .apply(instance, ShapelessPouchRecipe::new))
                .xmap(Function.identity(), (shapeless -> (ShapelessPouchRecipe) shapeless));
            STREAM_CODEC = StreamCodec.<
                    RegistryFriendlyByteBuf,
                    ShapelessPouchRecipe,
                    String,
                    CraftingBookCategory,
                    ItemStack,
                    List<Ingredient>
                >composite(
                ByteBufCodecs.STRING_UTF8, ShapelessRecipe::group,
                CraftingBookCategory.STREAM_CODEC, ShapelessRecipe::category,
                ItemStack.STREAM_CODEC, (recipe -> recipe.result),
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(9)), (recipe -> recipe.ingredients),
                ShapelessPouchRecipe::new)
                .map(Function.identity(), (shapeless -> (ShapelessPouchRecipe) shapeless));
        }
        
        //**************************************************************************************************************
		@Override public MapCodec<ShapelessRecipe> codec() { return Serializer.CODEC; }

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec()
        {
            return Serializer.STREAM_CODEC;
        }
	}
    
    //******************************************************************************************************************
    private final ItemStack        result;
    private final List<Ingredient> ingredients;
    
    //******************************************************************************************************************
    public ShapelessPouchRecipe(final String               group,
                                final CraftingBookCategory category,
                                final ItemStack            resultPouch,
                                final Ingredient           mainIngredient,
                                final List<Ingredient>     additionalIngredients)
    {
        this(group, category, resultPouch, Stream
            .concat(Stream.of(mainIngredient), additionalIngredients.stream().limit(8))
            .toList());
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private ShapelessPouchRecipe(final String group, final CraftingBookCategory category, final ItemStack resultPouch,
                                 final List<Ingredient> ingredients)
    {
        super(group, category, resultPouch, ingredients);
        this.result      = resultPouch;
        this.ingredients = ingredients;
    }
    
    //==================================================================================================================
    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer()
    {
        return ModRecipeSerialisers.SHAPELESS_POUCH_RECIPE;
    }
    
    //==================================================================================================================
    @Override
    public boolean matches(final CraftingInput craftingInput, final Level level)
    {
        if (!this.result.is(ModItemTags.POUCHES) || !super.matches(craftingInput, level))
        {
            return false;
        }
        
        return craftingInput.items().stream().anyMatch(stack ->
        {
            if (!stack.is(ItemTags.BUNDLES))
            {
                return false;
            }
            
            final BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            
            if (!contents.isEmpty())
            {
                if (contents.size() > 1)
                {
                    return false;
                }
                
                final ItemStack stack1 = contents.getItemUnsafe(0);
                return (PouchItem.isValidToolItem(stack1) && stack1.getCount() < 2);
            }
            
            return true;
        });
    }
    
    @Override
    public ItemStack assemble(final CraftingInput craftingInput, final HolderLookup.Provider provider)
    {
        final ItemStack result = super.assemble(craftingInput, provider);
        craftingInput.items().stream().filter(stack -> stack.is(ItemTags.BUNDLES)).findFirst().ifPresent(bundle ->
        {
            final BundleContents contents = bundle.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            
            if (!contents.isEmpty())
            {
                result.set(ModComponents.POUCH_CONTENT, new PouchContent(contents.getItemUnsafe(0).copy()));
            }
        });
        return result;
    }
}
