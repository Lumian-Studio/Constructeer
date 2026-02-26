package xyz.lumian.constructeer.integration.accessory;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;



//**********************************************************************************************************************
@ApiStatus.Internal
public final class Accessory_trinkets
    implements IAccessory
{
    //******************************************************************************************************************
    @Override
    public Optional<Container> getContainer(final Player player, final SlotReference slot)
    {
        final TrinketComponent comp = TrinketsApi.getTrinketComponent(player).orElse(null);
        
        if (comp == null)
        {
            return Optional.empty();
        }
        
        final String name      = slot.id();
        final int    delimiter = name.indexOf('/');
        
        if (delimiter == -1)
        {
            throw new IllegalArgumentException("Invalid trinkets slot name: " + slot);
        }
        
        final Map<String, TrinketInventory> containers = comp.getInventory().get(name.substring(0, delimiter));
        return (containers != null
            ? Optional.ofNullable(containers.get(name.substring(delimiter + 1)))
            : Optional.empty());
    }
    
    @Override
    public boolean isEquipped(final Player player, final Predicate<ItemStack> predicate,
                              final @Nullable EquipmentSlot fallback)
    {
        final TrinketComponent comp = TrinketsApi.getTrinketComponent(player).orElse(null);
        return (
            (comp != null && comp.isEquipped(predicate))
            || (fallback != null && predicate.test(player.getItemBySlot(fallback)))
        );
    }
    
    @Override
    public boolean isEquipped(final SlotReference ref, final Player player, final Predicate<ItemStack> predicate,
                              final boolean tryVanilla)
    {
        final Container container = this.getContainer(player, ref).orElse(null);
        
        if (container != null && container.hasAnyMatching(predicate))
        {
            return true;
        }
        
        return (tryVanilla && predicate.test(player.getItemBySlot(ref.vanillaPendant())));
    }
}
