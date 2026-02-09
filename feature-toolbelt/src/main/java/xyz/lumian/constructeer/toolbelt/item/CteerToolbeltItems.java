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
package xyz.lumian.constructeer.toolbelt.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.registry.CteerItemRegistry;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.PouchContent;
import xyz.lumian.constructeer.toolbelt.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.util.Freezable;
import xyz.lumian.constructeer.util.FreezableMap;
import xyz.lumian.constructeer.util.ItemFactory;

import java.util.EnumMap;
import java.util.Map;



//**********************************************************************************************************************
public final class CteerToolbeltItems
{
    //******************************************************************************************************************
    /// The pseudo registry for all the mod's registered items, including block items.
    public static final Map<DyeColor, Item> POUCH_BY_DYE;
    
    //==================================================================================================================
    public static final Item TOOLBELT;
    
    public static final Item POUCH;
    public static final Item WHITE_POUCH;
    public static final Item ORANGE_POUCH;
    public static final Item MAGENTA_POUCH;
    public static final Item LIGHT_BLUE_POUCH;
    public static final Item YELLOW_POUCH;
    public static final Item LIME_POUCH;
    public static final Item PINK_POUCH;
    public static final Item GRAY_POUCH;
    public static final Item LIGHT_GRAY_POUCH;
    public static final Item CYAN_POUCH;
    public static final Item PURPLE_POUCH;
    public static final Item BLUE_POUCH;
    public static final Item BROWN_POUCH;
    public static final Item GREEN_POUCH;
    public static final Item RED_POUCH;
    public static final Item BLACK_POUCH;
    
    //==================================================================================================================
    static
    {
        try (var ignored = CteerItemRegistry.usingTab(CteerItemRegistry.MAIN_TAB_KEY))
        {
            TOOLBELT = CteerItemRegistry.register("toolbelt", ToolbeltItem::new, (new Item.Properties())
                .stacksTo(1)
                .component(DataComponents.EQUIPPABLE, Equippable
                    .builder(EquipmentSlot.LEGS)
                    .setEquipSound(ArmorMaterials.LEATHER.equipSound())
                    .setAsset(CteerToolbeltEquipmentAssets.TOOLBELT)
                    .setEquipOnInteract(false)
                    .build())
                .component(CteerToolbeltDataComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY));
            
            try (var map = Freezable.using(new FreezableMap<DyeColor, Item>(new EnumMap<>(DyeColor.class))))
            {
                POUCH_BY_DYE = map.object();
            
                POUCH            = modPouchItem(null);
                WHITE_POUCH      = modPouchItem(DyeColor.WHITE);
                ORANGE_POUCH     = modPouchItem(DyeColor.ORANGE);
                MAGENTA_POUCH    = modPouchItem(DyeColor.MAGENTA);
                LIGHT_BLUE_POUCH = modPouchItem(DyeColor.LIGHT_BLUE);
                YELLOW_POUCH     = modPouchItem(DyeColor.YELLOW);
                LIME_POUCH       = modPouchItem(DyeColor.LIME);
                PINK_POUCH       = modPouchItem(DyeColor.PINK);
                GRAY_POUCH       = modPouchItem(DyeColor.GRAY);
                LIGHT_GRAY_POUCH = modPouchItem(DyeColor.LIGHT_GRAY);
                CYAN_POUCH       = modPouchItem(DyeColor.CYAN);
                PURPLE_POUCH     = modPouchItem(DyeColor.PURPLE);
                BLUE_POUCH       = modPouchItem(DyeColor.BLUE);
                BROWN_POUCH      = modPouchItem(DyeColor.BROWN);
                GREEN_POUCH      = modPouchItem(DyeColor.GREEN);
                RED_POUCH        = modPouchItem(DyeColor.RED);
                BLACK_POUCH      = modPouchItem(DyeColor.BLACK);
            }
        }
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    /// Utility function that allows adding custom pouch items and registering them.
    /// @param id      The [Identifier] of the item
    /// @param factory The [PouchItem] generator function
    /// @return The newly created pouch item
    /// @throws IllegalStateException If an item with the given tool material was already registered.
    public static <T extends PouchItem> T createPouchItem(final Identifier id, final ItemFactory<T> factory)
    {
        
        return CteerItemRegistry.register(id, factory, (new Item.Properties())
            .stacksTo(1)
            .component(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY));
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static Item modPouchItem(final @Nullable DyeColor dye)
    {
        final String name = ((dye != null ? (dye.getName() + '_') : "") + "pouch");
        final Item   item = CteerToolbeltItems.createPouchItem(CteerDefine.id(name), PouchItem::new);
        
        if (dye != null && CteerToolbeltItems.POUCH_BY_DYE.put(dye, item) != null)
        {
            throw new IllegalStateException("Duplicate Pouch Item for Dye " + dye);
        }
        
        return item;
    }
    
    //******************************************************************************************************************
    private CteerToolbeltItems() {}
}
