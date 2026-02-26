package xyz.lumian.constructeer.gauntlet.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.gauntlet.item.portable.Grabber;
import xyz.lumian.constructeer.integration.accessory.IAccessory;
import xyz.lumian.constructeer.registry.CteerItemRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.util.ItemFactory;



//**********************************************************************************************************************
public class CteerGauntletItems
    implements IBootstrap
{
    //******************************************************************************************************************
    public static final Item GAUNTLET_OF_POWER;
    
    //==================================================================================================================
    static
    {
        try (var ignored = CteerItemRegistry.usingTab(CteerItemRegistry.MAIN_TAB_KEY))
        {
            GAUNTLET_OF_POWER = createGauntletItem(CteerDefine.id("gauntlet_of_power"), GauntletItem::new);
        }
    }
    
    //******************************************************************************************************************
    /// Utility function that allows adding custom gauntlet items and registering them.
    /// @param id      The [Identifier] of the item
    /// @param factory The item generator function
    /// @return The newly created gauntlet item
    public static <T extends GauntletItem> T createGauntletItem(final Identifier id, final ItemFactory<T> factory)
    {
        return CteerItemRegistry.register(id, factory, (new Item.Properties())
            .stacksTo(1)
            .component(DataComponents.EQUIPPABLE, Equippable
                .builder(IAccessory.SlotConstants.HAND.vanillaPendant())
                .setAllowedEntities(EntityType.PLAYER)
                .setEquipSound(SoundEvents.ARMOR_EQUIP_GOLD)
                .build())
            .component(CteerGauntletDataComponents.GRABBER, Grabber.ALL));
    }
}
