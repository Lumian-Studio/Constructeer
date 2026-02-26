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

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.level.BlockContext;
import xyz.lumian.constructeer.multimining.CteerMultiMiningDictionary;
import xyz.lumian.constructeer.player.CteerPlayerAttachments;
import xyz.lumian.constructeer.multimining.item.component.CteerMultiMiningDataComponents;
import xyz.lumian.constructeer.multimining.item.multimining.BuiltInMultiMining;
import xyz.lumian.constructeer.multimining.item.multimining.IMultiMining;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.CteerItemRegistry;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.util.Freezable;
import xyz.lumian.constructeer.util.FreezableMap;
import xyz.lumian.constructeer.util.ItemFactory;

import java.util.*;
import java.util.function.Supplier;



//**********************************************************************************************************************
public final class CteerMultiMiningItems
    implements IBootstrap
{
    //******************************************************************************************************************
    public static final Map<ToolMaterial, Item> HAMMER_BY_MATERIAL;
    public static final Map<ToolMaterial, Item> PLOW_BY_MATERIAL;
    public static final Map<ToolMaterial, Item> SAW_BY_MATERIAL;
    
    //==================================================================================================================
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
    
    public static final ToolMaterial SAW_MATERIAL_WOOD;
	public static final ToolMaterial SAW_MATERIAL_STONE;
	public static final ToolMaterial SAW_MATERIAL_COPPER;
	public static final ToolMaterial SAW_MATERIAL_IRON;
	public static final ToolMaterial SAW_MATERIAL_DIAMOND;
	public static final ToolMaterial SAW_MATERIAL_GOLD;
	public static final ToolMaterial SAW_MATERIAL_NETHERITE;
    
    //==================================================================================================================
    static
    {
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
        
        final Supplier<FreezableMap<ToolMaterial, Item>> map_maker
            = (() -> new FreezableMap<>(new Reference2ObjectArrayMap<>()));
        
        try (var ignored = CteerItemRegistry.usingTab(CteerItemRegistry.MAIN_TAB_KEY))
        {
            try (var map = Freezable.using(map_maker.get()))
            {
                HAMMER_BY_MATERIAL = map.object();
                
                WOODEN_HAMMER    = createModHammer("wooden",    ToolMaterial.WOOD);
                STONE_HAMMER     = createModHammer("stone",     ToolMaterial.STONE);
                COPPER_HAMMER    = createModHammer("copper",    ToolMaterial.COPPER);
                IRON_HAMMER      = createModHammer("iron",      ToolMaterial.IRON);
                GOLDEN_HAMMER    = createModHammer("golden",    ToolMaterial.GOLD);
                DIAMOND_HAMMER   = createModHammer("diamond",   ToolMaterial.DIAMOND);
                NETHERITE_HAMMER = createModHammer("netherite", ToolMaterial.NETHERITE);
            }
            
            try (var map = Freezable.using(map_maker.get()))
            {
                PLOW_BY_MATERIAL = map.object();
                
                WOODEN_PLOW    = createModPlow("wooden",    ToolMaterial.WOOD);
                STONE_PLOW     = createModPlow("stone",     ToolMaterial.STONE);
                COPPER_PLOW    = createModPlow("copper",    ToolMaterial.COPPER);
                IRON_PLOW      = createModPlow("iron",      ToolMaterial.IRON);
                GOLDEN_PLOW    = createModPlow("golden",    ToolMaterial.GOLD);
                DIAMOND_PLOW   = createModPlow("diamond",   ToolMaterial.DIAMOND);
                NETHERITE_PLOW = createModPlow("netherite", ToolMaterial.NETHERITE);
            }
            
            try (var map = Freezable.using(map_maker.get()))
            {
                SAW_BY_MATERIAL = map.object();
                
                WOODEN_SAW    = createModSaw("wooden",    CteerMultiMiningItems.SAW_MATERIAL_WOOD,      5.5F, -3.2F);
                STONE_SAW     = createModSaw("stone",     CteerMultiMiningItems.SAW_MATERIAL_STONE,     6.5F, -3.2F);
                COPPER_SAW    = createModSaw("copper",    CteerMultiMiningItems.SAW_MATERIAL_COPPER,    6.5F, -3.2F);
                IRON_SAW      = createModSaw("iron",      CteerMultiMiningItems.SAW_MATERIAL_IRON,      5.5F, -3.1F);
                GOLDEN_SAW    = createModSaw("golden",    CteerMultiMiningItems.SAW_MATERIAL_GOLD,      5.5F, -3.0F);
                DIAMOND_SAW   = createModSaw("diamond",   CteerMultiMiningItems.SAW_MATERIAL_DIAMOND,   4.5F, -3.0F);
                NETHERITE_SAW = createModSaw("netherite", CteerMultiMiningItems.SAW_MATERIAL_NETHERITE, 4.5F, -3.0F);
            }
        }
    }
    
    //******************************************************************************************************************
    /// Utility function that allows adding custom hammer items.
    /// @param id           The [Identifier] of the item
    /// @param attackDamage The attack damage this item causes
    /// @param attackSpeed  The speed at which this item attacks
    /// @param material     The material of the item
    /// @param factory      The item generator function
    /// @throws IllegalStateException If an item with the given tool material was already registered.
    public static <T extends Item> T createHammer(final Identifier     id,
                                                  final float          attackDamage,
                                                  final float          attackSpeed,
                                                  final ToolMaterial   material,
                                                  final ItemFactory<T> factory)
    {
        return CteerItemRegistry.register(id, factory, (new Item.Properties())
            .pickaxe(material, attackDamage, attackSpeed)
            .component(CteerMultiMiningDataComponents.MULTI_MINING, BuiltInMultiMining.HAMMER));
    }
    
    /// Utility function that allows adding custom plow items.
    /// @param id           The [Identifier] of the item
    /// @param attackDamage The attack damage this item causes
    /// @param attackSpeed  The speed at which this item attacks
    /// @param material     The material of the item
    /// @param factory      The item generator function
    /// @throws IllegalStateException If an item with the given tool material was already registered.
    private static <T extends Item> T createPlow(final Identifier     id,
                                                 final float          attackDamage,
                                                 final float          attackSpeed,
                                                 final ToolMaterial   material,
                                                 final ItemFactory<T> factory)
    {
        return CteerItemRegistry.register(id, factory, (new Item.Properties())
            .shovel(material, attackDamage, attackSpeed)
            .component(CteerMultiMiningDataComponents.MULTI_MINING, BuiltInMultiMining.PLOW));
    }
    
    /// Utility function that allows adding custom saw items.
    /// @param id           The [Identifier] of the item
    /// @param attackDamage The attack damage this item causes
    /// @param attackSpeed  The speed at which this item attacks
    /// @param material     The material of the item
    /// @param factory      The item generator function
    /// @throws IllegalStateException If an item with the given tool material was already registered.
    private static <T extends Item> T createSaw(final Identifier     id,
                                                final float          attackDamage,
                                                final float          attackSpeed,
                                                final ToolMaterial   material,
                                                final ItemFactory<T> factory)
    {
        return CteerItemRegistry.register(id, factory, (new Item.Properties())
            .axe(material, attackDamage, attackSpeed)
            .component(CteerMultiMiningDataComponents.MULTI_MINING, BuiltInMultiMining.SAW));
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static Item createModHammer(final String name, final ToolMaterial material)
    {
        final Item item = CteerMultiMiningItems
            .createHammer(CteerDefine.id(name + "_hammer"), 1.0f, -2.8f, material, Item::new);
        
        if (CteerMultiMiningItems.HAMMER_BY_MATERIAL.put(material, item) != null)
        {
            throw new IllegalStateException("hammer with material '" + material + "' already registered");
        }
        
        return item;
    }
    
    private static Item createModPlow(final String name, final ToolMaterial material)
    {
        final Item item = CteerMultiMiningItems
            .createPlow(CteerDefine.id(name + "_plow"), 1.5f, -3.0f, material, Item::new);
        
        if (CteerMultiMiningItems.PLOW_BY_MATERIAL.put(material, item) != null)
        {
            throw new IllegalStateException("plow with material '" + material + "' already registered");
        }
        
        return item;
    }
    
    private static Item createModSaw(final String name, final ToolMaterial material, final float attackDamage,
                                     final float attackSpeed)
    {
        final Item item = CteerMultiMiningItems
            .createSaw(CteerDefine.id(name + "_saw"), attackDamage, attackSpeed, material, Item::new);
        
        if (CteerMultiMiningItems.SAW_BY_MATERIAL.put(material, item) != null)
        {
            throw new IllegalStateException("saw with material '" + material + "' already registered");
        }
        
        return item;
    }
    
    //******************************************************************************************************************
    @Override
    public void bootstrap(final BootstrapReport report)
    {
        PlayerBlockBreakEvents.BEFORE.register(((level, player, pos, state, blockEntity) ->
        {
            final ItemStack    stack = player.getMainHandItem();
            final IMultiMining mm    = stack.get(CteerMultiMiningDataComponents.MULTI_MINING);
            
            if (mm != null && !player.isCreative())
            {
                @SuppressWarnings("UnstableApiUsage")
                final Direction look_dir = Objects
                    .requireNonNull(player)
                    .getAttached(CteerPlayerAttachments.DESTROY_BLOCK_FACE);
                
                if (look_dir != null)
                {
                    final IMultiMining.Result result = mm.mine(look_dir, player, stack,
                                                               new BlockContext(level, state, pos),
                                                               IMultiMining.MiningFlag.DEFAULT_FLAGS);
                    
                    if (result == IMultiMining.Result.CAPPED)
                    {
                        player.displayClientMessage(CteerMultiMiningDictionary.MULTI_MINING_STRUCTURE_TOO_BIG, true);
                    }
                    
                    return result.shouldBreakMined;
                }
            }
            
            return true;
        }));
    }
}
