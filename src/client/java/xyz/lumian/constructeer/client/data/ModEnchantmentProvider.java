package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import xyz.lumian.constructeer.enchantment.ModEnchantments;
import xyz.lumian.constructeer.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class ModEnchantmentProvider
    extends FabricDynamicRegistryProvider
{
    //******************************************************************************************************************
    public ModEnchantmentProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> future)
    {
        super(output, future);
    }
    
    //==================================================================================================================
    @Override public String getName() { return "Constructeer Enchantment Provider"; }
    
    //==================================================================================================================
    @Override
    protected void configure(final HolderLookup.Provider lookup, final Entries entries)
    {
        entries.add(
            ModEnchantments.PENETRATION,
            Enchantment
                .enchantment(Enchantment.definition(
                    lookup.lookupOrThrow(Registries.ITEM).getOrThrow(ModItemTags.MULTI_MINING_TOOLS),
                    10, 2,
                    Enchantment.dynamicCost(20, 30),
                    Enchantment.dynamicCost(20, 50),
                    5,
                    EquipmentSlotGroup.MAINHAND))
                .build(ModEnchantments.PENETRATION.identifier()));
    }
}
