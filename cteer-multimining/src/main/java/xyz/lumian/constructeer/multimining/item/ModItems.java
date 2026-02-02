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
package xyz.lumian.constructeer.multimining.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.MultiMining;
import xyz.lumian.constructeer.multimining.item.component.PouchContent;
import xyz.lumian.constructeer.multimining.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.multimining.item.multimining.area.BuiltInToolProvider;
import xyz.lumian.constructeer.multimining.item.multimining.area.BuiltInTreeDetectionProvider;
import xyz.lumian.constructeer.multimining.item.multimining.damage.BuiltInDamageType;
import xyz.lumian.constructeer.multimining.item.multimining.timber.BuiltInTimberMode;
import xyz.lumian.constructeer.multimining.item.tab.ModTabs;
import xyz.lumian.constructeer.multimining.stat.ModStats;
import xyz.lumian.constructeer.multimining.util.FreezableMap;
import xyz.lumian.constructeer.multimining.util.ItemFactory;

import java.util.*;



//**********************************************************************************************************************
public final class ModItems
{
    //******************************************************************************************************************
    /// The pseudo registry for all the mod's registered items, including block items.
    public static final Map<Identifier, Item> BY_ID
        = new FreezableMap<>(new Object2ReferenceOpenHashMap<>());
    
    public static final Map<DyeColor, Item> POUCH_BY_DYE
        = new FreezableMap<>(new EnumMap<>(DyeColor.class));
    
    public static final Map<ToolMaterial, Item> HAMMER_BY_MATERIAL
        = new FreezableMap<>(new Reference2ObjectArrayMap<>());
    
    public static final Map<ToolMaterial, Item> PLOW_BY_MATERIAL
        = new FreezableMap<>(new Reference2ObjectArrayMap<>());
    
    public static final Map<ToolMaterial, Item> SAW_BY_MATERIAL
        = new FreezableMap<>(new Reference2ObjectArrayMap<>());
    
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
    
    public static final Item WOODEN_HAMMER;
    public static final Item STONE_HAMMER;
    public static final Item COPPER_HAMMER;
    public static final Item IRON_HAMMER;
    public static final Item GOLDEN_HAMMER;
    public static final Item DIAMOND_HAMMER;
    public static final Item NETHERITE_HAMMER;
    
    public static final Item WOODEN_PLOW;
    public static final Item STONE_PLOW;
    public static final Item COPPER_PLOW;
    public static final Item IRON_PLOW;
    public static final Item GOLDEN_PLOW;
    public static final Item DIAMOND_PLOW;
    public static final Item NETHERITE_PLOW;
    
    public static final Item WOODEN_SAW;
    public static final Item STONE_SAW;
    public static final Item COPPER_SAW;
    public static final Item IRON_SAW;
    public static final Item GOLDEN_SAW;
    public static final Item DIAMOND_SAW;
    public static final Item NETHERITE_SAW;
    
    public static final @Nullable Item DEBUG_ITEM;
    
    public static final ToolMaterial SAW_MATERIAL_WOOD;
	public static final ToolMaterial SAW_MATERIAL_STONE;
	public static final ToolMaterial SAW_MATERIAL_COPPER;
	public static final ToolMaterial SAW_MATERIAL_IRON;
	public static final ToolMaterial SAW_MATERIAL_DIAMOND;
	public static final ToolMaterial SAW_MATERIAL_GOLD;
	public static final ToolMaterial SAW_MATERIAL_NETHERITE;
    
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
            .component(ModComponents.TOOLBELT_STORAGE, ToolbeltStorage .EMPTY));
        
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
        ((FreezableMap<DyeColor, Item>) ModItems.POUCH_BY_DYE).freeze();
        
        WOODEN_HAMMER    = ModItems.createHammer("wooden",    ToolMaterial.WOOD);
        STONE_HAMMER     = ModItems.createHammer("stone",     ToolMaterial.STONE);
        COPPER_HAMMER    = ModItems.createHammer("copper",    ToolMaterial.COPPER);
        IRON_HAMMER      = ModItems.createHammer("iron",      ToolMaterial.IRON);
        GOLDEN_HAMMER    = ModItems.createHammer("golden",    ToolMaterial.GOLD);
        DIAMOND_HAMMER   = ModItems.createHammer("diamond",   ToolMaterial.DIAMOND);
        NETHERITE_HAMMER = ModItems.createHammer("netherite", ToolMaterial.NETHERITE);
        ((FreezableMap<ToolMaterial, Item>) ModItems.HAMMER_BY_MATERIAL).freeze();
        
        WOODEN_PLOW    = ModItems.createPlow("wooden",    ToolMaterial.WOOD);
        STONE_PLOW     = ModItems.createPlow("stone",     ToolMaterial.STONE);
        COPPER_PLOW    = ModItems.createPlow("copper",    ToolMaterial.COPPER);
        IRON_PLOW      = ModItems.createPlow("iron",      ToolMaterial.IRON);
        GOLDEN_PLOW    = ModItems.createPlow("golden",    ToolMaterial.GOLD);
        DIAMOND_PLOW   = ModItems.createPlow("diamond",   ToolMaterial.DIAMOND);
        NETHERITE_PLOW = ModItems.createPlow("netherite", ToolMaterial.NETHERITE);
        ((FreezableMap<ToolMaterial, Item>) ModItems.PLOW_BY_MATERIAL).freeze();
        
        SAW_MATERIAL_WOOD      = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 1.0F, 0.0F, 15,
                                                  ItemTags.WOODEN_TOOL_MATERIALS);
        SAW_MATERIAL_STONE     = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 2.0F, 0.5F, 5,
                                                  ItemTags.STONE_TOOL_MATERIALS);
        SAW_MATERIAL_COPPER    = new ToolMaterial(BlockTags.INCORRECT_FOR_COPPER_TOOL, 190, 2.5F, 0.5F, 13,
                                                  ItemTags.COPPER_TOOL_MATERIALS);
        SAW_MATERIAL_IRON      = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 3.0F, 1.0F, 14,
                                                  ItemTags.IRON_TOOL_MATERIALS);
        SAW_MATERIAL_DIAMOND   = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 4.0F, 1.5F, 10,
                                                  ItemTags.DIAMOND_TOOL_MATERIALS);
        SAW_MATERIAL_GOLD      = new ToolMaterial(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 6.0F, 0.0F, 22,
                                                  ItemTags.GOLD_TOOL_MATERIALS);
        SAW_MATERIAL_NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 4.5F, 2.0F, 15,
                                                  ItemTags.NETHERITE_TOOL_MATERIALS);
        
        WOODEN_SAW    = ModItems.createSaw("wooden",    ModItems.SAW_MATERIAL_WOOD,      5.5F, -3.2F);
        STONE_SAW     = ModItems.createSaw("stone",     ModItems.SAW_MATERIAL_STONE,     6.5F, -3.2F);
        COPPER_SAW    = ModItems.createSaw("copper",    ModItems.SAW_MATERIAL_COPPER,    6.5F, -3.2F);
        IRON_SAW      = ModItems.createSaw("iron",      ModItems.SAW_MATERIAL_IRON,      5.5F, -3.1F);
        GOLDEN_SAW    = ModItems.createSaw("golden",    ModItems.SAW_MATERIAL_GOLD,      5.5F, -3.0F);
        DIAMOND_SAW   = ModItems.createSaw("diamond",   ModItems.SAW_MATERIAL_DIAMOND,   4.5F, -3.0F);
        NETHERITE_SAW = ModItems.createSaw("netherite", ModItems.SAW_MATERIAL_NETHERITE, 4.5F, -3.0F);
        ((FreezableMap<ToolMaterial, Item>) ModItems.SAW_BY_MATERIAL).freeze();
        
        CURRENT_TAB = null;
        
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
        {
            DEBUG_ITEM = ModItems.register("debug_stick", DebugItem::new, new Item.Properties());
        } else DEBUG_ITEM = null;
        
        ((FreezableMap<Identifier, Item>) ModItems.BY_ID).freeze();
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
    
    private static Item createHammer(final String name, final ToolMaterial material)
    {
        final Item item = ModItems.register((name + "_hammer"), Item::new, (new Item.Properties())
            .pickaxe(material, 1.0f, -2.8f)
            .component(
                ModComponents.MULTI_MINING,
                new MultiMining(BuiltInToolProvider.HAMMER, ModStats.HAMMER_USED, BuiltInTimberMode.HAMMER, 0,
                                BuiltInDamageType.BUILTIN_HAMMER)));
        ModItems.HAMMER_BY_MATERIAL.put(material, item);
        return item;
    }
    
    private static Item createPlow(final String name, final ToolMaterial material)
    {
        final Item item = ModItems.register((name + "_plow"), Item::new, (new Item.Properties())
            .shovel(material, 1.5f, -3.0f)
            .component(
                ModComponents.MULTI_MINING,
                new MultiMining(BuiltInToolProvider.PLOW, ModStats.PLOW_USED, BuiltInTimberMode.PLOW, 0,
                                BuiltInDamageType.BUILTIN_PLOW)));
        ModItems.PLOW_BY_MATERIAL.put(material, item);
        return item;
    }
    
    private static Item createSaw(final String name, final ToolMaterial material, final float attackDamage,
                                  final float attackSpeed)
    {
        final Item item = ModItems.register((name + "_saw"), Item::new, (new Item.Properties())
            .axe(material, attackDamage, attackSpeed)
            .component(
                ModComponents.MULTI_MINING,
                new MultiMining(BuiltInTreeDetectionProvider.INSTANCE, ModStats.SAW_USED, BuiltInTimberMode.SAW, 0,
                                BuiltInDamageType.BUILTIN_SAW)));
        ModItems.SAW_BY_MATERIAL.put(material, item);
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
