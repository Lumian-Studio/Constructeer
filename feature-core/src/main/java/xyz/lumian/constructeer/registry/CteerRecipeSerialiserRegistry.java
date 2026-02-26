package xyz.lumian.constructeer.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;



//**********************************************************************************************************************
public final class CteerRecipeSerialiserRegistry
{
    //******************************************************************************************************************
    public static <T extends Recipe<?>> RecipeSerializer<T> register(final Identifier          id,
                                                                     final RecipeSerializer<T> serialiser)
    {
        return CteerRecipeSerialiserRegistry.register(ResourceKey.create(Registries.RECIPE_SERIALIZER, id), serialiser);
    }
    
    public static <T extends Recipe<?>> RecipeSerializer<T> register(final ResourceKey<RecipeSerializer<?>> key,
                                                                     final RecipeSerializer<T>              serialiser)
    {
        return CteerRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, key, serialiser);
    }
}
