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
package xyz.lumian.constructeer.multimining.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import xyz.lumian.constructeer.multimining.renderer.state.ModRenderDataKeys;
import xyz.lumian.constructeer.multimining.item.ModItems;
import xyz.lumian.constructeer.multimining.item.ToolbeltItem;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.PouchContent;
import xyz.lumian.constructeer.multimining.item.component.ToolbeltStorage;

import java.util.List;
import java.util.stream.IntStream;



//**********************************************************************************************************************
public class ToolbeltModel
    extends EntityModel<HumanoidRenderState>
{
    //******************************************************************************************************************
    public record PouchPart(ModelPart pouch, ModelPart lid, ModelPart cover, ModelPart fastener)
    {
        //**************************************************************************************************************
        private static ModelPart[] getParts(final ModelPart pouch)
        {
            final ModelPart lid = pouch.getChild("Lid");
            return new ModelPart[] { pouch, lid, lid.getChild("Cover"), lid.getChild("Fastener") };
        }
        
        //**************************************************************************************************************
        public PouchPart(final ModelPart pouch) { this(PouchPart.getParts(pouch)); }
        
        //--------------------------------------------------------------------------------------------------------------
        private PouchPart(final ModelPart[] parts) { this(parts[0], parts[1], parts[2], parts[3]); }
    }
    
    //******************************************************************************************************************
    private static final float PI_QUARTER = (float) (Math.PI / 4);
    private static final float PI_HALF    = (float) (Math.PI / 2);
    
    //******************************************************************************************************************
    public static LayerDefinition createLayer()
    {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();

		final PartDefinition toolbelt = root.addOrReplaceChild("Toolbelt", CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -14.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)),
            PartPose.offset(0.0F, 24.0F, 0.0F));
        
        ToolbeltModel.addPouch(toolbelt, 1, PartPose.offset           (-3.0F, -13.0F, -2.1F));
        ToolbeltModel.addPouch(toolbelt, 2, PartPose.offset           ( 0.0F, -13.0F, -2.1F));
        ToolbeltModel.addPouch(toolbelt, 3, PartPose.offset           ( 3.0F, -13.0F, -2.1F));
        ToolbeltModel.addPouch(toolbelt, 4, PartPose.offsetAndRotation( 4.1F, -13.0F,  0.0F,  0.0F,   -1.5708F, 0.0F));
        ToolbeltModel.addPouch(toolbelt, 5, PartPose.offsetAndRotation( 3.0F, -13.0F,  1.8F, -3.1416F, 0.0F,    3.1416F));
        ToolbeltModel.addPouch(toolbelt, 6, PartPose.offsetAndRotation( 0.0F, -13.0F,  1.8F, -3.1416F, 0.0F,    3.1416F));
        ToolbeltModel.addPouch(toolbelt, 7, PartPose.offsetAndRotation(-3.0F, -13.0F,  1.8F, -3.1416F, 0.0F,    3.1416F));
        ToolbeltModel.addPouch(toolbelt, 8, PartPose.offsetAndRotation(-4.1F, -13.0F,  0.0F,  0.0F,    1.5708F, 0.0F));
        
        return LayerDefinition.create(mesh, 32, 32);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static void addPouch(final PartDefinition parent, final int id, final PartPose pose)
    {
        final PartDefinition pouch = parent.addOrReplaceChild(
            ("Pouch" + id),
            CubeListBuilder.create()
                .texOffs(0, 6)
                .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 1.0F),
            pose);
		final PartDefinition lid = pouch.addOrReplaceChild(
            "Lid",
            CubeListBuilder.create()
                .texOffs(6, 6)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.1F, 1.0F),
            PartPose.offset(0.0F, -1.1F, -0.11F));
        lid.addOrReplaceChild(
            "Cover",
            CubeListBuilder.create()
                .texOffs(6, 8)
                .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 0.1F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 1.1F, 0.0F, 1.5708F, 0.0F, 0.0F));
        lid.addOrReplaceChild(
            "Sides",
            CubeListBuilder.create()
                .texOffs(12, 6)
                .addBox(-1.1F, -0.0F, -1.0F, 0.1F, 1.1F, 1.0F)
                .texOffs(12, 6)
                .addBox(1.0F, -0.0F, -1.0F, 0.1F, 1.1F, 1.0F),
            PartPose.ZERO);
		lid.addOrReplaceChild(
            "Fastener",
            CubeListBuilder.create()
		        .texOffs(16, 6)
                .addBox(-0.6F, -1.6F, -1.925F, 1.2F, 1.0F, 1.7F, new CubeDeformation(-0.4F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
    }
    
    //******************************************************************************************************************
    public final ModelPart       toolbelt;
    public final List<PouchPart> pouches;
    
    //******************************************************************************************************************
    public ToolbeltModel(final ModelPart root)
    {
        super(root);
        this.toolbelt = root.getChild("Toolbelt");
        this.pouches  = IntStream.range(1, ToolbeltItem.COUNT_POUCHES)
            .mapToObj(i -> new PouchPart(this.toolbelt.getChild("Pouch" + i)))
            .toList();
    }
    
    //==================================================================================================================
    @Override
    public void setupAnim(final HumanoidRenderState state)
    {
        this.toolbelt.xScale = 1.0f;
        this.toolbelt.zScale = 1.0f;
        
        int   state_mask = 0;
        
        if (state.legsEquipment.is(ItemTags.LEG_ARMOR))
        {
            this.toolbelt.xScale = 1.05f;
            this.toolbelt.zScale = 1.1f;
        }
        
        final ItemStack stack = state.getDataOrDefault(ModRenderDataKeys.HUMANOID_TOOLBELT_EQUIPMENT, ItemStack.EMPTY);
        
        if (stack.is(ModItems.TOOLBELT))
        {
            final ToolbeltStorage storage = stack.get(ModComponents.TOOLBELT_STORAGE);
            
            if (storage != null)
            {
                for (int i = 1; i < ToolbeltItem.COUNT_POUCHES; ++i)
                {
                    final ItemStack pouch = storage.getPouchUnsafe(i);
                    
                    if (!pouch.isEmpty())
                    {
                        final int bit = (1 << (i - 1));
                        state_mask |= bit;
                        
                        final PouchContent content = pouch.get(ModComponents.POUCH_CONTENT);
                        
                        if (content != null && !content.isEmpty())
                        {
                            state_mask |= (bit << 8);
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < this.pouches.size(); ++i)
        {
            final int bit = (1 << i);
            
            final PouchPart part    = this.pouches.get(i);
            final boolean   visible = ((state_mask & bit) == bit);
            part.pouch.visible = visible;
            
            if (visible && (((state_mask >> 8) & bit) != bit))
            {
                final double fall_distance = state.getDataOrDefault(ModRenderDataKeys.LIVING_FALL_DISTANCE, 0.0);
                
                if (fall_distance <= 0.01f)
                {
                    part.lid.xRot = Math.min(0f, (part.lid.xRot + 0.2f));
                }
                else
                {
                    part.lid.xRot = -(float) (ToolbeltModel.PI_QUARTER * Math.min(1.5f, fall_distance));
                }
            }
        }
    }
}
