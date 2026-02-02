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
package xyz.lumian.constructeer.multimining.integration;

import com.mojang.datafixers.util.Either;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import xyz.lumian.constructeer.multimining.integration.impl.ITrinkets;

import java.util.Optional;



//**********************************************************************************************************************
public final class Trinkets
    implements
        ITrinkets
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
