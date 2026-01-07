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
    
    @Override public MapCodec<? extends ConditionalItemModelProperty> type() { return MenuComplies.MAP_CODEC; }
}
