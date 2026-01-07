package xyz.lumian.constructeer.util;

import net.minecraft.world.item.Item;
import java.util.function.Function;



//**********************************************************************************************************************
/// A functional type helper interface, spares the use of complex [Function] tinkering.
/// @param <T> The class extending [Item] that this generator produces
@FunctionalInterface
public interface ItemFactory<T extends Item>
{
    //******************************************************************************************************************
    /// Constructs an item from the given [Item.Properties].
    /// @param properties The properties to supply the item with
    /// @return The newly instanced [Item]
    T generate(Item.Properties properties);
}
