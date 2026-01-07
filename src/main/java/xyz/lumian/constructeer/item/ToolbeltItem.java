package xyz.lumian.constructeer.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.container.ToolbeltMenu;
import xyz.lumian.constructeer.container.slot.ExtendedEquipmentSlot;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.ToolbeltStorage;



//**********************************************************************************************************************
public class ToolbeltItem
    extends Item
{
    //******************************************************************************************************************
    public record MenuProvider(ExtendedEquipmentSlot slot, ItemStack stack)
        implements net.minecraft.world.MenuProvider
    {
        //**************************************************************************************************************
        @Override public Component getDisplayName() { return this.stack.getHoverName(); }
        
        @Override
        public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inv,
                                                          final Player player)
        {
            if (!this.stack.is(ModItems.TOOLBELT))
            {
                ModDefine.LOGGER.error(
                    "could not open toolbelt configuration menu as the item in the given slot was not a toolbelt: {}",
                    this.stack);
                return null;
            }
            
            return ToolbeltMenu.server(this.slot, containerId, inv, new SimpleContainer(this.stack
                .getOrDefault(ModComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY)
                .asItems()
                .stream()
                .map(ItemStack::copy)
                .toArray(ItemStack[]::new)));
        }
    }
    
    //******************************************************************************************************************
    public static final int COUNT_POUCHES = 9;
    
    //******************************************************************************************************************
    public ToolbeltItem(final Properties properties) { super(properties); }
    
    //==================================================================================================================
    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand)
    {
        if (hand != InteractionHand.MAIN_HAND)
        {
            return InteractionResult.PASS;
        }
        
        if (player.isCrouching())
        {
            return super.use(level, player, hand);
        }
        
        if (!level.isClientSide())
        {
            player.openMenu(new MenuProvider(new ExtendedEquipmentSlot(hand), player.getItemInHand(hand)));
        }
        
        return InteractionResult.SUCCESS;
    }
}
