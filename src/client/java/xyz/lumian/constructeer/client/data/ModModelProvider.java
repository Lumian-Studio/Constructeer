package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import xyz.lumian.constructeer.client.renderer.item.PouchContainedItemSpecialRenderer;
import xyz.lumian.constructeer.client.renderer.item.conditional.MenuComplies;
import xyz.lumian.constructeer.client.renderer.item.conditional.PouchHasContent;
import xyz.lumian.constructeer.client.renderer.item.conditional.predicate.InPouchSlot;
import xyz.lumian.constructeer.container.ModMenus;
import xyz.lumian.constructeer.item.ModItems;

import java.util.Arrays;
import java.util.Optional;



//**********************************************************************************************************************
public class ModModelProvider
    extends FabricModelProvider
{
    //******************************************************************************************************************
    public ModModelProvider(final FabricDataOutput output) { super(output); }
    
    //==================================================================================================================
    @Override
    public void generateBlockStateModels(final BlockModelGenerators generator)
    {
    
    }
    
    @Override
    public void generateItemModels(final ItemModelGenerators generator)
    {
        generator.generateFlatItem(ModItems.TOOLBELT, ModelTemplates.FLAT_ITEM);
        {
        
        }
        
        {
            // Base un-dyed pouch
            this.generatePouchModels(generator, ModItems.POUCH);
            
            // Dyed pouches
            Arrays.stream(DyeColor.values()).forEach(dye -> this.generatePouchModels(
                generator,
                ModItems.POUCH_BY_DYE.get(dye)));
        }
    }
    
    //==================================================================================================================
    private void generatePouchModels(final ItemModelGenerators generator, final Item item)
    {
        final Identifier pouch_model   = ModelTemplates.FLAT_ITEM.create(
            item,
            TextureMapping.layer0(item),
            generator.modelOutput);
        final Identifier full_model = pouch_model.withSuffix("_full");
        ModelTemplates.FLAT_ITEM.create(full_model, TextureMapping.layer0(full_model), generator.modelOutput);
        
        final ItemModel.Unbaked full_unbaked = ItemModelUtils.plainModel(full_model);
        generator.itemModelOutput.accept(item, ItemModelUtils.conditional(new PouchHasContent(),
            // has content
            ItemModelUtils.conditional(MenuComplies.anyMenu(InPouchSlot.INSTANCE),
                // is inside pouch slot
                ItemModelUtils.composite(full_unbaked, new PouchContainedItemSpecialRenderer.Unbaked()),
                
                // is inside any other slot
                full_unbaked),
            
            // has no content
            ItemModelUtils.plainModel(pouch_model)));
    }
}
