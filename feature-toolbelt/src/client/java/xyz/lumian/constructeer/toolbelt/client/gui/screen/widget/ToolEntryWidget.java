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
package xyz.lumian.constructeer.toolbelt.client.gui.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.toolbelt.CteerToolbeltDictionary;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.PouchContent;



//**********************************************************************************************************************
public class ToolEntryWidget
    extends AbstractWidget
{
    //******************************************************************************************************************
    public interface WidgetListener
    {
        //**************************************************************************************************************
        void hoverStateChanged(ToolEntryWidget widget, boolean isHovered);
    }
    
    //******************************************************************************************************************
    public static final Identifier SLOT_TEXTURE          = CteerDefine.id("toolbelt/tool_slot");
    public static final Identifier SLOT_TEXTURE_INACTIVE = CteerDefine.id("toolbelt/tool_slot_inactive");
    
    public static final int   TEXTURE_SIZE  = 24;
    public static final float MAGNIFICATION = 1.2f;
    
    //******************************************************************************************************************
    public final int slotId;
    
    //------------------------------------------------------------------------------------------------------------------
    private final ItemStack      content;
    private final WidgetListener listener;
    
    private float   scaleAnimated = 1.0f;
    private boolean hovered       = false;
    
    //******************************************************************************************************************
    public ToolEntryWidget(final WidgetListener listener, final int slotId, final ItemStack pouch)
    {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        
        this.slotId   = slotId;
        this.listener = listener;
        
        if (pouch.isEmpty())
        {
            this.active  = false;
            this.content = ItemStack.EMPTY;
        }
        else
        {
            this.content = pouch
                .getOrDefault(CteerToolbeltDataComponents.POUCH_CONTENT, PouchContent.EMPTY)
                .content()
                .copy();
            this.setMessage(!this.content.isEmpty()
                ? this.content.getHoverName()
                : CteerToolbeltDictionary.TOOLBELT_WHEEL_SCREEN_EMPTY_POUCH);
        }
        
        this.setSize(ToolEntryWidget.TEXTURE_SIZE, ToolEntryWidget.TEXTURE_SIZE);
    }
    
    //==================================================================================================================
    @Override public boolean mouseClicked(final MouseButtonEvent event, final boolean isDoubleClick) { return false; }
    
    //==================================================================================================================
    @Override
    protected void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float tickDelta)
    {
        if (this.hovered != this.isHovered)
        {
            this.hovered = this.isHovered;
            this.listener.hoverStateChanged(this, this.hovered);
        }
        
        if (!this.isActive())
        {
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                ToolEntryWidget.SLOT_TEXTURE_INACTIVE,
                ToolEntryWidget.TEXTURE_SIZE, ToolEntryWidget.TEXTURE_SIZE,
                0,                            0,
                this.getX(),                  this.getY(),
                ToolEntryWidget.TEXTURE_SIZE, ToolEntryWidget.TEXTURE_SIZE,
                0x67FFFFFF);
            return;
        }
        
        final int centre_x  = (this.getX() + this.width  / 2);
        final int centre_y  = (this.getY() + this.height / 2);
        
        final Matrix3x2fStack matrix = graphics.pose().pushMatrix();
        
        if (this.isHovered())
        {
            if (this.scaleAnimated < ToolEntryWidget.MAGNIFICATION)
            {
                this.scaleAnimated = Math.min(ToolEntryWidget.MAGNIFICATION, (this.scaleAnimated + 0.07f));
            }
        }
        else if (this.scaleAnimated > 1.0f)
        {
            this.scaleAnimated -= 0.1f;
        }
        
        matrix.scaleAround(this.scaleAnimated, centre_x, centre_y);
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            ToolEntryWidget.SLOT_TEXTURE,
            ToolEntryWidget.TEXTURE_SIZE, ToolEntryWidget.TEXTURE_SIZE,
            0,                            0,
            this.getX(),                  this.getY(),
            ToolEntryWidget.TEXTURE_SIZE, ToolEntryWidget.TEXTURE_SIZE);
        matrix.popMatrix();
        
        graphics.renderFakeItem(this.content, (centre_x - 8), (centre_y - 8));
    }
    
    @Override
    protected void updateWidgetNarration(final NarrationElementOutput output)
    {
        output.add(NarratedElementType.TITLE, this.message);
    }
}
