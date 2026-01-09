package xyz.lumian.constructeer.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.PouchContent;
import xyz.lumian.constructeer.item.component.ToolbeltSettings;
import xyz.lumian.constructeer.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.item.tab.ModTabs;
import xyz.lumian.constructeer.util.FreezableMap;
import xyz.lumian.constructeer.util.ItemFactory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;



//**********************************************************************************************************************
public final class ModItems
{
    //******************************************************************************************************************
    /// The pseudo registry for all the mod's registered items, including block items.
    public static final Map<Identifier, Item> BY_ID = new FreezableMap<>(new Object2ReferenceOpenHashMap<>());
    
    public static final Map<DyeColor, Item> POUCH_BY_DYE = new FreezableMap<>(new EnumMap<>(DyeColor.class));
    
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
    private static @Nullable Map<ResourceKey<CreativeModeTab>, Set<Item>> CREATIVE_TABS = new Object2ObjectArrayMap<>();
    private static @Nullable ResourceKey<CreativeModeTab>                 CURRENT_TAB;
    
    //==================================================================================================================
    static
    {
        CURRENT_TAB = ModTabs.TOOLS_KEY;
        
        TOOLBELT = ModItems.register("toolbelt", ToolbeltItem::new, (new Item.Properties())
            .stacksTo(1)
            .component(DataComponents.EQUIPPABLE, Equippable
                .builder(EquipmentSlot.LEGS)
                .setEquipSound(ArmorMaterials.LEATHER.equipSound())
                .setAsset(ModEquipmentAssets.TOOLBELT)
                .setEquipOnInteract(false)
                .build())
            .component(ModComponents.TOOLBELT_STORAGE,  ToolbeltStorage .EMPTY)
            .component(ModComponents.TOOLBELT_SETTINGS, ToolbeltSettings.DEFAULT));
        
        POUCH            = ModItems.createPouchItem(null);
        WHITE_POUCH      = ModItems.createPouchItem(DyeColor.WHITE);
        ORANGE_POUCH     = ModItems.createPouchItem(DyeColor.ORANGE);
        MAGENTA_POUCH    = ModItems.createPouchItem(DyeColor.MAGENTA);
        LIGHT_BLUE_POUCH = ModItems.createPouchItem(DyeColor.LIGHT_BLUE);
        YELLOW_POUCH     = ModItems.createPouchItem(DyeColor.YELLOW);
        LIME_POUCH       = ModItems.createPouchItem(DyeColor.LIME);
        PINK_POUCH       = ModItems.createPouchItem(DyeColor.PINK);
        GRAY_POUCH       = ModItems.createPouchItem(DyeColor.GRAY);
        LIGHT_GRAY_POUCH = ModItems.createPouchItem(DyeColor.LIGHT_GRAY);
        CYAN_POUCH       = ModItems.createPouchItem(DyeColor.CYAN);
        PURPLE_POUCH     = ModItems.createPouchItem(DyeColor.PURPLE);
        BLUE_POUCH       = ModItems.createPouchItem(DyeColor.BLUE);
        BROWN_POUCH      = ModItems.createPouchItem(DyeColor.BROWN);
        GREEN_POUCH      = ModItems.createPouchItem(DyeColor.GREEN);
        RED_POUCH        = ModItems.createPouchItem(DyeColor.RED);
        BLACK_POUCH      = ModItems.createPouchItem(DyeColor.BLACK);
        
        CURRENT_TAB = null;
        ((FreezableMap<Identifier, Item>) ModItems.BY_ID)       .freeze();
        ((FreezableMap<DyeColor,   Item>) ModItems.POUCH_BY_DYE).freeze();
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        Objects.requireNonNull(ModItems.CREATIVE_TABS).forEach((key, set) ->
            ItemGroupEvents.modifyEntriesEvent(key).register(group ->
                group.acceptAll(set.stream().map(ItemStack::new).toList())));
        ModItems.CREATIVE_TABS = null;
    }
    
    //==================================================================================================================
    /// Registers an [Item] for this mod.
    /// @param key            The [ResourceKey]
    /// @param factory        The [ItemFactory]
    /// @param initProperties The initial [Item.Properties] the item generator should start with
    /// @return The registered [Item] instance
    public static <T extends Item> T register(final ResourceKey<Item> key,
                                              final ItemFactory<T>    factory,
                                              final Item.Properties   initProperties)
    {
        final T item = factory.generate(initProperties.setId(key));
        ModItems.BY_ID.put(key.identifier(), item);
        ModItems.addToItemGroup(ModItems.CURRENT_TAB, item);
        
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
    
    /// Registers an [Item] for this mod.
    /// @param id             The [Identifier]
    /// @param factory        The [ItemFactory]
    /// @param initProperties The initial [Item.Properties] the item generator should start with
    /// @return The registered [Item] instance
    public static <T extends Item> T register(final Identifier      id,
                                              final ItemFactory<T>  factory,
                                              final Item.Properties initProperties)
    {
        return ModItems.register(ResourceKey.create(Registries.ITEM, id), factory, initProperties);
    }
    
    /// Registers an [Item] with this mod's namespace and the given item name.
    /// @param name           The name of the item without namespace
    /// @param factory        The [ItemFactory]
    /// @param initProperties The initial [Item.Properties] the item generator should start with
    /// @return The registered [Item] instance
    public static <T extends Item> T register(final String          name,
                                              final ItemFactory<T>  factory,
                                              final Item.Properties initProperties)
    {
        return ModItems.register(ModDefine.id(name), factory, initProperties);
    }
    
    //==================================================================================================================
    private static Item createPouchItem(final @Nullable DyeColor dye)
    {
        final String name = ((dye != null ? (dye.getName() + '_') : "") + "pouch");
        final Item   item = ModItems.register(name, PouchItem::new, (new Item.Properties())
            .stacksTo(1)
            .component(ModComponents.POUCH_CONTENT, PouchContent.EMPTY));
        
        if (dye != null)
        {
            ModItems.POUCH_BY_DYE.put(dye, item);
        }
        
        return item;
    }
    
    private static void addToItemGroup(final @Nullable ResourceKey<CreativeModeTab> tabKey, final Item item)
    {
        if (tabKey != null)
        {
            Objects.requireNonNull(ModItems.CREATIVE_TABS)
                .computeIfAbsent(tabKey, (k -> new ObjectArraySet<>()))
                .add(item);
        }
    }
    
    //******************************************************************************************************************
    private ModItems() {}
}
