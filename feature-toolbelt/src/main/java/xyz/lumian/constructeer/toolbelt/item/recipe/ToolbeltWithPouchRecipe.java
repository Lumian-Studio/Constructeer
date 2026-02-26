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
package xyz.lumian.constructeer.toolbelt.item.recipe;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import xyz.lumian.constructeer.toolbelt.registry.CteerToolbeltTags;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.util.FreezableObject;

import java.util.List;



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
                    BuiltInRegistries.ITEM.byNameCodec()
                        .fieldOf("result")
                        .forGetter(recipe -> recipe.result),
                    Codec.BOOL
                        .optionalFieldOf("show_notification", true)
                        .forGetter(recipe -> recipe.showNotification))
				.apply(instance, ToolbeltWithPouchRecipe::new));
            STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT.map((i -> CraftingBookCategory.values()[i]), Enum::ordinal),
                    ToolbeltWithPouchRecipe::category,
                ByteBufCodecs.registry(Registries.ITEM), (recipe -> recipe.result),
                ByteBufCodecs.BOOL,                      ToolbeltWithPouchRecipe::showNotification,
                ToolbeltWithPouchRecipe::new);
            
            ToolbeltWithPouchRecipe.initialise();
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
    private static final List<Ingredient> INGREDIENTS;
    
    //==================================================================================================================
    static
    {
        final Ingredient      leather_ingredient = Ingredient.of(Items.LEATHER);
        final HolderSet<Item> pouches            = BuiltInRegistries
            .acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM)
            .getOrThrow(CteerToolbeltTags.POUCHES);
        
        INGREDIENTS = ImmutableList.<Ingredient>builder()
            .add(leather_ingredient, Ingredient.of(Items.IRON_INGOT), leather_ingredient)
            .add(leather_ingredient, Ingredient.of(pouches),          leather_ingredient)
            .add(leather_ingredient, leather_ingredient,              leather_ingredient)
            .build();
    }
    
    //******************************************************************************************************************
    private static void initialise() {}
    
    //******************************************************************************************************************
    private final FreezableObject.Deferred<PlacementInfo> placementInfo = new FreezableObject.Deferred<>();
    private final Item                                    result;
    private final boolean                                 showNotification;
    
    //******************************************************************************************************************
    public ToolbeltWithPouchRecipe(final CraftingBookCategory category, final Item result,
                                   final boolean showNotification)
    {
        super(category);
        
        this.showNotification = showNotification;
        this.result           = result;
    }
    
    public ToolbeltWithPouchRecipe(final CraftingBookCategory category, final Item result)
    {
        this(category, result, true);
    }
    
    //==================================================================================================================
    @Override
    public RecipeSerializer<ToolbeltWithPouchRecipe> getSerializer()
    {
        return CteerToolbeltRecipeSerialisers.TOOLBELT_WITH_POUCH;
    }
    
    @Override
    public PlacementInfo placementInfo()
    {
        if (!this.placementInfo.isSet())
        {
            this.placementInfo.set(PlacementInfo.create(ToolbeltWithPouchRecipe.INGREDIENTS));
        }
        
        return this.placementInfo.get();
    }
    
    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(new ShapedCraftingRecipeDisplay(
            3, 3,
            ToolbeltWithPouchRecipe.INGREDIENTS.stream().map(Ingredient::display).toList(),
            new SlotDisplay.ItemSlotDisplay(this.result),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }
    
    @Override public boolean showNotification() { return this.showNotification; }
    
    //==================================================================================================================
    @Override
    public boolean matches(final CraftingInput input, final Level level)
    {
        if (input.width() != 3 || input.height() != 3 || input.size() != 9)
        {
            return false;
        }
        
        final List<Ingredient> ingredients = ToolbeltWithPouchRecipe.INGREDIENTS;
        
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
        final ItemStack toolbelt = new ItemStack(this.result);
        toolbelt.update(
            CteerToolbeltDataComponents.TOOLBELT_STORAGE,
            ToolbeltStorage.EMPTY,
            ToolbeltStorage.updateOp(0, pouch));
        return toolbelt;
    }
}
