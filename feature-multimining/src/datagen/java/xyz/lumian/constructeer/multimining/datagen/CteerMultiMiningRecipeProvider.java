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
package xyz.lumian.constructeer.multimining.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import xyz.lumian.constructeer.multimining.item.CteerMultiMiningItems;

import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class CteerMultiMiningRecipeProvider
    extends FabricRecipeProvider
{
    //******************************************************************************************************************
    public static void addHammerRecipe(final Item hammer, final ItemLike materialIngredient,
                                       final RecipeProvider provider, final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, hammer)
            .pattern("###")
            .pattern(" * ")
            .pattern(" * ")
            .define('#', materialIngredient)
            .define('*', Items.STICK)
            .unlockedBy(RecipeProvider.getHasName(materialIngredient), provider.has(materialIngredient))
            .save(output);
    }
    
    public static void addHammerRecipe(final Item hammer, final TagKey<Item> materialIngredients, final String hasName,
                                       final RecipeProvider provider, final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, hammer)
            .pattern("###")
            .pattern(" * ")
            .pattern(" * ")
            .define('#', materialIngredients)
            .define('*', Items.STICK)
            .unlockedBy(hasName, provider.has(materialIngredients))
            .save(output);
    }
    
    public static void addSmithingRecipe(final Item multiToolInput, final Item multiToolOutput,
                                         final RecipeProvider provider, final RecipeOutput output)
    {
        SmithingTransformRecipeBuilder
            .smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(multiToolInput),
                Ingredient.of(Items.NETHERITE_BLOCK),
                RecipeCategory.TOOLS,
                multiToolOutput)
            .unlocks(
                RecipeProvider.getHasName(Items.NETHERITE_BLOCK),
                provider.has(ItemTags.NETHERITE_TOOL_MATERIALS))
            .save(output, (RecipeProvider.getItemName(multiToolOutput) + "_smithing"));
    }
    
    public static void addPlowRecipe(final Item hammer, final ItemLike materialIngredient,
                                     final RecipeProvider provider, final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, hammer)
            .pattern("#")
            .pattern("*")
            .pattern("*")
            .define('#', materialIngredient)
            .define('*', Items.STICK)
            .unlockedBy(RecipeProvider.getHasName(materialIngredient), provider.has(materialIngredient))
            .save(output);
    }
    
    public static void addPlowRecipe(final Item hammer, final TagKey<Item> ingredient, final String hasName,
                                      final RecipeProvider provider, final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, hammer)
            .pattern("#")
            .pattern("*")
            .pattern("*")
            .define('#', ingredient)
            .define('*', Items.STICK)
            .unlockedBy(hasName, provider.has(ingredient))
            .save(output);
    }
    
    public static void addSawRecipe(final Item saw, final ItemLike materialIngredient, final RecipeProvider provider,
                                    final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, saw)
            .pattern(" # ")
            .pattern("#*#")
            .pattern(" * ")
            .define('#', materialIngredient)
            .define('*', Items.STICK)
            .unlockedBy(RecipeProvider.getHasName(materialIngredient), provider.has(materialIngredient))
            .save(output);
    }
    
    public static void addSawRecipe(final Item saw, final TagKey<Item> ingredient, final String hasName,
                                    final RecipeProvider provider, final RecipeOutput output)
    {
        provider.shaped(RecipeCategory.TOOLS, saw)
            .pattern(" # ")
            .pattern("#*#")
            .pattern(" * ")
            .define('#', ingredient)
            .define('*', Items.STICK)
            .unlockedBy(hasName, provider.has(ingredient))
            .save(output);
    }
    
    //******************************************************************************************************************
    public CteerMultiMiningRecipeProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> future)
    {
        super(output, future);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Multi-Mining Recipe Provider"; }
    
    //==================================================================================================================
    @Override
    protected RecipeProvider createRecipeProvider(final HolderLookup.Provider lookup, final RecipeOutput output)
    {
        return new RecipeProvider(lookup, output)
        {
            //**********************************************************************************************************
            @Override
            public void buildRecipes()
            {
                this.addHammerRecipe(CteerMultiMiningItems.WOODEN_HAMMER,  ItemTags.LOGS, "has_logs");
                this.addHammerRecipe(CteerMultiMiningItems.STONE_HAMMER,   Blocks.SMOOTH_STONE);
                this.addHammerRecipe(CteerMultiMiningItems.COPPER_HAMMER,  ItemTags.COPPER, "has_copper");
                this.addHammerRecipe(CteerMultiMiningItems.IRON_HAMMER,    Blocks.IRON_BLOCK);
                this.addHammerRecipe(CteerMultiMiningItems.GOLDEN_HAMMER,  Blocks.GOLD_BLOCK);
                this.addHammerRecipe(CteerMultiMiningItems.DIAMOND_HAMMER, Blocks.DIAMOND_BLOCK);
                this.addSmithingRecipe(CteerMultiMiningItems.DIAMOND_HAMMER, CteerMultiMiningItems.NETHERITE_HAMMER);
                
                this.addPlowRecipe(CteerMultiMiningItems.WOODEN_PLOW,  ItemTags.LOGS, "has_logs");
                this.addPlowRecipe(CteerMultiMiningItems.STONE_PLOW,   Blocks.SMOOTH_STONE);
                this.addPlowRecipe(CteerMultiMiningItems.COPPER_PLOW,  ItemTags.COPPER, "has_copper");
                this.addPlowRecipe(CteerMultiMiningItems.IRON_PLOW,    Blocks.IRON_BLOCK);
                this.addPlowRecipe(CteerMultiMiningItems.GOLDEN_PLOW,  Blocks.GOLD_BLOCK);
                this.addPlowRecipe(CteerMultiMiningItems.DIAMOND_PLOW, Blocks.DIAMOND_BLOCK);
                this.addSmithingRecipe(CteerMultiMiningItems.DIAMOND_PLOW, CteerMultiMiningItems.NETHERITE_PLOW);
                
                this.addSawRecipe(CteerMultiMiningItems.WOODEN_SAW,  ItemTags.LOGS, "has_logs");
                this.addSawRecipe(CteerMultiMiningItems.STONE_SAW,   Blocks.SMOOTH_STONE);
                this.addSawRecipe(CteerMultiMiningItems.COPPER_SAW,  ItemTags.COPPER, "has_copper");
                this.addSawRecipe(CteerMultiMiningItems.IRON_SAW,    Blocks.IRON_BLOCK);
                this.addSawRecipe(CteerMultiMiningItems.GOLDEN_SAW,  Blocks.GOLD_BLOCK);
                this.addSawRecipe(CteerMultiMiningItems.DIAMOND_SAW, Blocks.DIAMOND_BLOCK);
                this.addSmithingRecipe(CteerMultiMiningItems.DIAMOND_SAW, CteerMultiMiningItems.NETHERITE_SAW);
            }
            
            //==========================================================================================================
            private void addHammerRecipe(final Item hammer, final ItemLike ingredient)
            {
                CteerMultiMiningRecipeProvider.addHammerRecipe(hammer, ingredient, this, this.output);
            }
            
            private void addHammerRecipe(final Item hammer, final TagKey<Item> ingredient, final String hasName)
            {
                CteerMultiMiningRecipeProvider.addHammerRecipe(hammer, ingredient, hasName, this, this.output);
            }
            
            private void addSmithingRecipe(final Item multiToolInput, final Item multiToolOutput)
            {
                CteerMultiMiningRecipeProvider.addSmithingRecipe(multiToolInput, multiToolOutput, this, this.output);
            }
            
            private void addPlowRecipe(final Item hammer, final Block ingredient)
            {
                CteerMultiMiningRecipeProvider.addPlowRecipe(hammer, ingredient, this, this.output);
            }
            
            private void addPlowRecipe(final Item hammer, final TagKey<Item> ingredient, final String hasName)
            {
                CteerMultiMiningRecipeProvider.addPlowRecipe(hammer, ingredient, hasName, this, this.output);
            }
            
            private void addSawRecipe(final Item saw, final Block ingredient)
            {
                CteerMultiMiningRecipeProvider.addSawRecipe(saw, ingredient, this, this.output);
            }
            
            private void addSawRecipe(final Item saw, final TagKey<Item> ingredient, final String hasName)
            {
                CteerMultiMiningRecipeProvider.addSawRecipe(saw, ingredient, hasName, this, this.output);
            }
        };
    }
}
