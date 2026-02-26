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
package xyz.lumian.constructeer.toolbelt.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.toolbelt.registry.CteerToolbeltTags;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;
import xyz.lumian.constructeer.toolbelt.item.recipe.ShapelessPouchRecipe;
import xyz.lumian.constructeer.toolbelt.item.recipe.ToolbeltWithPouchRecipe;

import java.util.List;
import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class CteerToolbeltRecipeProvider
    extends FabricRecipeProvider
{
    //******************************************************************************************************************
    public CteerToolbeltRecipeProvider(final FabricDataOutput output,
                                       final CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Toolbelt Recipe Provider"; }
    
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
                {
                    final RecipeCategory         cat = RecipeCategory.TOOLS;
                    final ResourceKey<Recipe<?>> key = ResourceKey.create(
                        Registries.RECIPE,
                        BuiltInRegistries.ITEM.getKey(CteerToolbeltItems.TOOLBELT));
                    
                    this.output.accept(
                        key,
                        new ToolbeltWithPouchRecipe(
                            RecipeBuilder.determineBookCategory(cat),
                            CteerToolbeltItems.TOOLBELT),
                        this.output.advancement()
                            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                            .rewards(AdvancementRewards.Builder.recipe(key))
                            .requirements(AdvancementRequirements.Strategy.OR)
                            .addCriterion("has_pouches", this.has(CteerToolbeltTags.POUCHES))
                            .build(key.identifier().withPrefix("recipes/" + cat.getFolderName() + "/")));
                }
                
                this.addPouchRecipe(CteerToolbeltItems.POUCH, Items.BUNDLE, null);
                CteerToolbeltItems.POUCH_BY_DYE.forEach((dye, item) ->
                    this.addPouchRecipe(item, BundleItem.getByColor(dye), dye));
            }
            
            //==========================================================================================================
            private void buildShapelessPouchRecipe(final String group, final Item pouch, final Item bundle,
                                                   final Identifier id, final String hasName, final Criterion<?> crit,
                                                   final List<Ingredient> additionalIngredients)
            {
                final RecipeCategory         cat = RecipeCategory.TOOLS;
                final ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
                this.output.accept(
                    key,
                    new ShapelessPouchRecipe(group, RecipeBuilder.determineBookCategory(cat), new ItemStack(pouch),
                                             Ingredient.of(bundle), additionalIngredients),
                    this.output.advancement()
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                        .rewards(AdvancementRewards.Builder.recipe(key))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .addCriterion(hasName, crit)
                        .build(id.withPrefix("recipes/" + cat.getFolderName() + "/")));
            }
            
            private void addPouchRecipe(final Item pouch, final Item bundle, final @Nullable DyeColor dye)
            {
                final String       has_name  = RecipeProvider.getHasName(Items.BUNDLE);
                final Criterion<?> criterion = this.has(ItemTags.BUNDLES);
                
                final Identifier base_id = BuiltInRegistries.ITEM.getKey(pouch);
                this.buildShapelessPouchRecipe(base_id.getPath(), pouch, bundle, base_id, has_name, criterion,
                                               List.of(Ingredient.of(Items.GOLD_NUGGET)));
                
                if (dye != null)
                {
                    final DyeItem dye_item = DyeItem.byColor(dye);
                    this.buildShapelessPouchRecipe(base_id.getPath(), pouch, Items.BUNDLE,
                                                   base_id.withSuffix("_from_dye_and_bundle"), has_name, criterion,
                                                   List.of(Ingredient.of(Items.GOLD_NUGGET), Ingredient.of(dye_item)));
                    
                    final HolderSet<Item> pouches = this.registries
                        .lookupOrThrow(Registries.ITEM)
                        .getOrThrow(CteerToolbeltTags.POUCHES);
                    TransmuteRecipeBuilder
                        .transmute(RecipeCategory.TOOLS, Ingredient.of(pouches), Ingredient.of(dye_item), pouch)
                        .group(base_id.getPath())
                        .unlockedBy(has_name, criterion)
                        .save(this.output, base_id.withSuffix("_from_dyeing").toString());
                }
            }
        };
    }
}
