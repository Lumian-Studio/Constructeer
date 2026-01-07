package xyz.lumian.constructeer.client.data;

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
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.item.recipe.ShapelessPouchRecipe;
import xyz.lumian.constructeer.item.recipe.ToolbeltWithPouchRecipe;
import xyz.lumian.constructeer.item.ModItems;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class ModRecipeProvider
    extends FabricRecipeProvider
{
    //******************************************************************************************************************
    public ModRecipeProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> future)
    {
        super(output, future);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Recipes"; }
    
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
                        BuiltInRegistries.ITEM.getKey(ModItems.TOOLBELT));
                    
                    this.output.accept(key, new ToolbeltWithPouchRecipe(RecipeBuilder.determineBookCategory(cat)),
                        this.output.advancement()
                            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                            .rewards(AdvancementRewards.Builder.recipe(key))
                            .requirements(AdvancementRequirements.Strategy.OR)
                            .addCriterion("has_pouches", this.has(ModItemTags.POUCHES))
                            .build(key.identifier().withPrefix("recipes/" + cat.getFolderName() + "/")));
                }
                
                this.addPouchRecipe(ModItems.POUCH, Items.BUNDLE, null);
                Arrays.stream(DyeColor.values()).forEach(dye -> this.addPouchRecipe(
                    ModItems.POUCH_BY_DYE.get(dye),
                    BundleItem.getByColor(dye), dye));
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
                
                final Identifier base_id  = BuiltInRegistries.ITEM.getKey(pouch);
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
                        .getOrThrow(ModItemTags.POUCHES);
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
