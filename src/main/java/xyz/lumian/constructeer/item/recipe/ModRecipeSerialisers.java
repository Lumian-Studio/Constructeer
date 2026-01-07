package xyz.lumian.constructeer.item.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;



//**********************************************************************************************************************
public final class ModRecipeSerialisers
{
    //******************************************************************************************************************
    public static final RecipeSerializer<ToolbeltWithPouchRecipe> TOOLBELT_WITH_POUCH    = RecipeSerializer
        .register("crafting_special_toolbelt_with_pouch", new ToolbeltWithPouchRecipe.Serializer());
    public static final RecipeSerializer<ShapelessRecipe>         SHAPELESS_POUCH_RECIPE = RecipeSerializer
        .register("shapeless_pouch_recipe", new ShapelessPouchRecipe.Serializer());
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //******************************************************************************************************************
    private ModRecipeSerialisers() {}
}
