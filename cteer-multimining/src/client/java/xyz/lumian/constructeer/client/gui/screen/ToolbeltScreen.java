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
package xyz.lumian.constructeer.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.container.ToolbeltMenu;



//**********************************************************************************************************************
public class ToolbeltScreen
    extends AbstractContainerScreen<ToolbeltMenu>
{
    //******************************************************************************************************************
    private static final Identifier CONTAINER_BACKGROUND = ModDefine.id("textures/gui/container/toolbelt.png");
    
    //******************************************************************************************************************
    public ToolbeltScreen(final ToolbeltMenu menu, final Inventory inventory, final Component component)
    {
        super(menu, inventory, component);
        this.imageHeight     = 214;
        this.inventoryLabelY = 121;
    }
    
    //==================================================================================================================
    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float tickDelta)
    {
        super.render(graphics, mouseX, mouseY, tickDelta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderBg(final GuiGraphics g, final float f, final int mouseX, final int mouseY)
    {
        final int x_pos = ((this.width  - this.imageWidth)  / 2);
        final int y_pos = ((this.height - this.imageHeight) / 2);
        g.blit(
            RenderPipelines.GUI_TEXTURED,
            ToolbeltScreen.CONTAINER_BACKGROUND,
            x_pos,           y_pos,
            0.0F,            0.0F,
            this.imageWidth, this.imageHeight,
            256,             256);
    }
    
    @Override
    protected void renderSlots(final GuiGraphics graphics, final int mouseX, final int mouseY)
    {
        for (final var slot : this.menu.slots)
        {
            this.renderSlot(graphics, slot, mouseX, mouseY);
        }
    }
    
    @Override
    protected void renderSlot(final GuiGraphics graphics, final Slot slot, final int mouseX, final int mouseY)
    {
        super.renderSlot(graphics, slot, mouseX, mouseY);
        
        if (!slot.isActive())
        {
            final int x = slot.x;
            final int y = slot.y;
            graphics.fill(x, y, (x + 16), (y + 16), 0x67000001);
        }
    }
}
