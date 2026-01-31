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
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.tag.ModItemTags;
import xyz.lumian.constructeer.util.FreezableObject;

import java.util.List;
import java.util.function.Supplier;



//**********************************************************************************************************************
public class ToolbeltWithPouchRecipe
    extends CustomRecipe
{
    //******************************************************************************************************************
    public static class Serializer
        implements RecipeSerializer<ToolbeltWithPouchRecipe>
    {
        //**************************************************************************************************************
		public static final MapCodec<ToolbeltWithPouchRecipe>                             CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, ToolbeltWithPouchRecipe> STREAM_CODEC;
        
        //==============================================================================================================
        static
        {
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                    CraftingBookCategory.CODEC
                        .fieldOf("category")
                        .orElse(CraftingBookCategory.MISC)
                        .forGetter(ToolbeltWithPouchRecipe::category),
                    Codec.BOOL
                        .optionalFieldOf("show_notification", true)
                        .forGetter(recipe -> recipe.showNotification))
				.apply(instance, ToolbeltWithPouchRecipe::new));
            STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT.map((i -> CraftingBookCategory.values()[i]), Enum::ordinal),
                    ToolbeltWithPouchRecipe::category,
                ByteBufCodecs.BOOL, ToolbeltWithPouchRecipe::showNotification,
                ToolbeltWithPouchRecipe::new);
        }
        
        //**************************************************************************************************************
		@Override public MapCodec<ToolbeltWithPouchRecipe> codec() { return Serializer.CODEC; }

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ToolbeltWithPouchRecipe> streamCodec()
        {
			return Serializer.STREAM_CODEC;
		}
	}
    
    //******************************************************************************************************************
    private static final Supplier<List<Ingredient>> INGREDIENTS_SUPPLIER = (() ->
    {
        final HolderSet<Item> valid_pouches = BuiltInRegistries.ITEM.getOrThrow(ModItemTags.POUCHES);
        return List.of(
            Ingredient.of(Items.LEATHER), Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.LEATHER),
            Ingredient.of(Items.LEATHER), Ingredient.of(valid_pouches),    Ingredient.of(Items.LEATHER),
            Ingredient.of(Items.LEATHER), Ingredient.of(Items.LEATHER),    Ingredient.of(Items.LEATHER));
    });
    
    //******************************************************************************************************************
    private final FreezableObject.Deferred<PlacementInfo>    placementInfo = new FreezableObject.Deferred<>();
    private final FreezableObject.Deferred<List<Ingredient>> ingredients   = new FreezableObject.Deferred<>();
    private final boolean                                    showNotification;
    
    //******************************************************************************************************************
    public ToolbeltWithPouchRecipe(final CraftingBookCategory category, final boolean showNotification)
    {
        super(category);
        this.showNotification = showNotification;
    }
    
    public ToolbeltWithPouchRecipe(final CraftingBookCategory category) { this(category, true); }
    
    //==================================================================================================================
    @Override
    public RecipeSerializer<ToolbeltWithPouchRecipe> getSerializer()
    {
        return ModRecipeSerialisers.TOOLBELT_WITH_POUCH;
    }
    
    @Override
    public PlacementInfo placementInfo()
    {
        if (!this.placementInfo.isSet())
        {
            this.ingredients.setIfUnset(ToolbeltWithPouchRecipe.INGREDIENTS_SUPPLIER);
            this.placementInfo.set(PlacementInfo.create(this.ingredients.get()));
        }
        
        return this.placementInfo.get();
    }
    
    @Override
    public List<RecipeDisplay> display()
    {
        this.ingredients.setIfUnset(ToolbeltWithPouchRecipe.INGREDIENTS_SUPPLIER);
        return List.of(new ShapedCraftingRecipeDisplay(
            3, 3,
            ToolbeltWithPouchRecipe.INGREDIENTS_SUPPLIER.get().stream().map(Ingredient::display).toList(),
            new SlotDisplay.ItemSlotDisplay(ModItems.TOOLBELT),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }
    
    @Override
    public boolean showNotification()
    {
        return super.showNotification();
    }
    
    //==================================================================================================================
    @Override
    public boolean matches(final CraftingInput input, final Level level)
    {
        if (input.width() != 3 || input.height() != 3 || input.size() != 9)
        {
            return false;
        }
        
        final List<Ingredient> ingredients = this.ingredients.setIfUnset(ToolbeltWithPouchRecipe.INGREDIENTS_SUPPLIER);
        
        for (int i = 0; i < ingredients.size(); ++i)
        {
            if (!ingredients.get(i).test(input.getItem(i)))
            {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public ItemStack assemble(final CraftingInput input, final HolderLookup.Provider provider)
    {
        final ItemStack pouch    = input.getItem(1, 1);
        final ItemStack toolbelt = new ItemStack(ModItems.TOOLBELT);
        toolbelt.update(ModComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY, ToolbeltStorage.updateOp(0, pouch));
        return toolbelt;
    }
}
