package xyz.lumian.constructeer.integration;

import com.mojang.datafixers.util.Either;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import xyz.lumian.constructeer.integration.impl.ITrinkets;

import java.util.Optional;



//**********************************************************************************************************************
public final class Trinkets
    implements ITrinkets
{
    //******************************************************************************************************************
    private static Optional<Container> resolve(final TrinketComponent component, final String group, final String id)
    {
        return Optional.ofNullable(component.getInventory().get(group)).map(map -> map.get(id));
    }
    
    //******************************************************************************************************************
    @Override
    public Optional<Container> getContainer(final Player player, final Either<DefaultSlot, String> slot)
    {
        return TrinketsApi.getTrinketComponent(player).flatMap(comp -> slot.map(
            (def_slot -> Trinkets.resolve(comp, def_slot.group, def_slot.type)),
            (id_slot ->
            {
                final int separator = id_slot.indexOf("/");
                
                if (separator == -1)
                {
                    return Optional.empty();
                }
                
                return Trinkets.resolve(comp, id_slot.substring(0, separator), id_slot.substring((separator + 1)));
            })
        ));
    }
}
