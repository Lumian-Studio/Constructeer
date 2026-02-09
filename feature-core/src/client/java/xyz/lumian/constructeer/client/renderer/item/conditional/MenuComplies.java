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
package xyz.lumian.constructeer.client.renderer.item.conditional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.client.mixin.AbstractContainerMenuAccessor;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.AlwaysTrue;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicate;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.MenuPredicates;

import java.util.Optional;



//**********************************************************************************************************************
public record MenuComplies<T extends MenuPredicate>(Optional<MenuType<?>> menuType, T predicate)
    implements ConditionalItemModelProperty
{
    //******************************************************************************************************************
    public static final MapCodec<MenuComplies<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            BuiltInRegistries.MENU.byNameCodec()
                .optionalFieldOf("menuTypes")
                .forGetter(MenuComplies::menuType),
            MenuPredicates.MAP_CODEC
                .forGetter(MenuComplies::predicate))
        .apply(instance, MenuComplies::new));
    
    //******************************************************************************************************************
    public static <T extends MenuPredicate> MenuComplies<T> anyMenu(final T predicate)
    {
        return new MenuComplies<>(Optional.empty(), predicate);
    }
    
    public static MenuComplies<AlwaysTrue> onlyIn(final MenuType<?> menuType)
    {
        return new MenuComplies<>(Optional.of(menuType), AlwaysTrue.INSTANCE);
    }
    
    //******************************************************************************************************************
    @Override
    public boolean get(final ItemStack stack, final @Nullable ClientLevel level, final @Nullable LivingEntity entity,
                       final int seed, final ItemDisplayContext ctx)
    {
        if (ctx != ItemDisplayContext.GUI || !(entity instanceof LocalPlayer player))
        {
            return false;
        }
        
        final AbstractContainerMenu menu = player.containerMenu;
        
        if (this.menuType.map(type -> (((AbstractContainerMenuAccessor) menu).getMenuType() != type)).orElse(false))
        {
            return false;
        }
        
        for (final var slot : menu.slots)
        {
            if (slot.getItem() == stack)
            {
                return this.predicate.test(menu, slot);
            }
        }
        
        return false;
    }
    
    @Override public MapCodec<MenuComplies<?>> type() { return MenuComplies.MAP_CODEC; }
}
