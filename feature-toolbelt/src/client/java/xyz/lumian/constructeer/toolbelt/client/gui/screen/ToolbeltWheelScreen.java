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
package xyz.lumian.constructeer.toolbelt.client.gui.screen;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.integration.accessory.AccessorySlot;
import xyz.lumian.constructeer.toolbelt.client.CteerToolbeltKeybinds;
import xyz.lumian.constructeer.toolbelt.client.gui.screen.widget.ToolEntryWidget;
import xyz.lumian.constructeer.toolbelt.client.player.CteerClientPlayerAttachments;
import xyz.lumian.constructeer.toolbelt.container.ToolbeltMenu;
import xyz.lumian.constructeer.toolbelt.item.component.CteerToolbeltDataComponents;
import xyz.lumian.constructeer.toolbelt.item.component.ToolbeltStorage;
import xyz.lumian.constructeer.toolbelt.network.serverbound.PlayC2SUpdateHeldTool;

import java.util.List;
import java.util.stream.IntStream;



//**********************************************************************************************************************
public class ToolbeltWheelScreen
    extends Screen
    implements ToolEntryWidget.WidgetListener
{
    //******************************************************************************************************************
    private record LineDrawer(
        int            minX,
        int            minY,
        int            maxX,
        int            maxY,
        RenderPipeline pipeline,
        TextureSetup   textureSetup,
        Matrix3x2f     pose,
        
        @Nullable
        ScreenRectangle scissorArea,
        
        @Nullable
        ScreenRectangle bounds
    ) implements GuiElementRenderState
    {
        //**************************************************************************************************************
        private static @Nullable ScreenRectangle makeBounds(final @Nullable ScreenRectangle scissor,
                                                            final Matrix3x2f pose, final int minX, final int minY,
                                                            final int maxX, final int maxY)
        {
            final ScreenRectangle rect = (new ScreenRectangle(
                    Math.min(minX, maxX),  Math.min(minY, maxY),
                    Math.abs(minX - maxX), Math.abs(minY - maxY)))
                .transformMaxBounds(pose);
            return  (scissor != null ? scissor.intersection(rect) : rect);
        }
        
        //**************************************************************************************************************
        public LineDrawer(final RenderPipeline pipeline, final Matrix3x2f pose, final @Nullable ScreenRectangle scissor,
                          final int minX, final int minY, final int maxX, final int maxY)
        {
            this(
                minX, minY, maxX, maxY,
                pipeline,
                TextureSetup.noTexture(),
                pose,
                scissor,
                LineDrawer.makeBounds(scissor, pose, minX, minY, maxX, maxY));
        }
        
        //**************************************************************************************************************
        @Override
        public void buildVertices(final VertexConsumer vertices)
        {
            vertices
                .addVertexWith2DPose(this.pose, this.minX, this.minY)
                .setNormal((this.minX - this.maxX), (this.minY - this.maxY), 0f)
                .setLineWidth(3f)
                .setColor(-1);
            vertices
                .addVertexWith2DPose(this.pose, this.maxX, this.maxY)
                .setNormal((this.minX - this.maxX), (this.minY - this.maxY), 0f)
                .setLineWidth(3f)
                .setColor(0x33FFFFFF);
        }
    }
    
    //******************************************************************************************************************
    private record LookupValue(double sine, double cosine) {}
    
    //******************************************************************************************************************
    public static final Identifier CURSOR_TEXTURE = CteerDefine.id("toolbelt/cursor");
    
    //------------------------------------------------------------------------------------------------------------------
    private static final int    WHEEL_AREA_DIMENSIONS = 96;
    private static final double WHEEL_RADIUS          = (WHEEL_AREA_DIMENSIONS * 0.5f - 10);
    private static final double WHEEL_RADIUS_SQUARED  = (WHEEL_RADIUS * WHEEL_RADIUS);
    
    //******************************************************************************************************************
    private static boolean isPointOutsideCircle(final double relMouseX, final double relMouseY)
    {
        final double x_diff = (relMouseX - ToolbeltWheelScreen.WHEEL_RADIUS);
        final double y_diff = (relMouseY - ToolbeltWheelScreen.WHEEL_RADIUS);
        final double sqrd_x = (x_diff * x_diff);
        final double sqrd_y = (y_diff * y_diff);
        return ((sqrd_x + sqrd_y) > ToolbeltWheelScreen.WHEEL_RADIUS_SQUARED);
    }
    
    private static double unscaleX(final double x)
    {
        final Window window = Minecraft.getInstance().getWindow();
        return (x * window.getScreenWidth() / window.getGuiScaledWidth());
    }
    
    private static double unscaleY(final double y)
    {
        final Window window = Minecraft.getInstance().getWindow();
        return (y * window.getScreenHeight() / window.getGuiScaledHeight());
    }
    
    //******************************************************************************************************************
    private final List<ToolEntryWidget> toolEntries;
    private final ItemStack             toolbelt;
    private final AccessorySlot         slot;
    
    private ScreenRectangle wheelBounds  = ScreenRectangle.empty();
    private boolean         isArrowMode  = false;
    private boolean         ignoreUpdate = false;
    
    @Nullable
    private ToolEntryWidget hovered = null;
    
    private double cursorX;
    private double cursorY;
    private double rotation;
    
    //******************************************************************************************************************
    public ToolbeltWheelScreen(final AccessorySlot slot, final ItemStack toolbelt)
    {
        super(CommonComponents.EMPTY);
        
        this.slot        = slot;
        this.toolbelt    = toolbelt.copy();
        this.toolEntries = toolbelt
            .getOrDefault(CteerToolbeltDataComponents.TOOLBELT_STORAGE, ToolbeltStorage.EMPTY).asEntries().stream()
            .map(entry -> new ToolEntryWidget(this, entry.id(), entry.pouch()))
            .toList();
    }
    
    //==================================================================================================================
    @Override public boolean isPauseScreen()     { return false; }
    @Override public boolean isAllowedInPortal() { return false; }
    @Override public boolean isInGameUi()        { return true;  }
    
    //==================================================================================================================
    @Override
    public void mouseMoved(final double mouseX, final double mouseY)
    {
        if (this.ignoreUpdate)
        {
            this.ignoreUpdate = false;
            return;
        }
        
        final double cx     = (this.width  * 0.5f);
        final double cy     = (this.height * 0.5f);
        final double radius = ToolbeltWheelScreen.WHEEL_RADIUS;
        
        this.rotation = Math.atan2((mouseX - cx), (mouseY - cy));
        
        if (ToolbeltWheelScreen.isPointOutsideCircle((mouseX - (cx - radius)), (mouseY - (cy - radius))))
        {
            this.cursorX = (cx + radius * Mth.sin(this.rotation));
            this.cursorY = (cy + radius * Mth.cos(this.rotation));
            
            this.ignoreUpdate = true;
            GLFW.glfwSetCursorPos(
                this.minecraft.getWindow().handle(),
                ToolbeltWheelScreen.unscaleX(this.cursorX),
                ToolbeltWheelScreen.unscaleY(this.cursorY));
            
            return;
        }
        
        this.cursorX = mouseX;
        this.cursorY = mouseY;
    }
    
    //==================================================================================================================
    @Override
    protected void init()
    {
        this.wheelBounds = new ScreenRectangle(
            ((this.width  - ToolbeltWheelScreen.WHEEL_AREA_DIMENSIONS) / 2),
            ((this.height - ToolbeltWheelScreen.WHEEL_AREA_DIMENSIONS) / 2),
            ToolbeltWheelScreen.WHEEL_AREA_DIMENSIONS,
            ToolbeltWheelScreen.WHEEL_AREA_DIMENSIONS);
        
        IntStream.range(0, this.toolEntries.size()).forEach(i ->
        {
            final Vector2i        pos    = ToolbeltMenu.POUCH_SLOTS[i];
            final ToolEntryWidget widget = this.toolEntries.get(i);
            widget.setPosition((this.wheelBounds.left() + pos.x()), (this.wheelBounds.top() + pos.y()));
            this.addRenderableWidget(widget);
        });
        
        this.cursorX = (this.width  / 2.0);
        this.cursorY = (this.height / 2.0);
        
        GLFW.glfwSetInputMode(this.minecraft.getWindow().handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }
    
    @Override
    public void tick()
    {
        final Player player = Minecraft.getInstance().player;
        
        if (!CteerToolbeltKeybinds.SHOW_TOOLBELT_WHEEL.isDown() || player == null)
        {
            this.onClose();
        }
    }
    
    //==================================================================================================================
    @Override
    public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY,
                                 final float partialTick)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick)
    {
        for (int i = 1; i < this.toolEntries.size(); ++i)
        {
            this.toolEntries.get(i).render(graphics, mouseX, mouseY, partialTick);
        }
        
        if (!this.toolEntries.getFirst().isHovered())
        {
            if (this.isArrowMode)
            {
                this.isArrowMode = false;
                GLFW.glfwSetInputMode(this.minecraft.getWindow().handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            }
            
            final int centre_x = (this.width  / 2);
            final int centre_y = (this.height / 2);
            final int cursor_x = (int) this.cursorX;
            final int cursor_y = (int) this.cursorY;
            graphics.guiRenderState.submitGuiElement(new LineDrawer(
                RenderPipelines.LINES,
                new Matrix3x2f(graphics.pose()),
                graphics.scissorStack.peek(),
                cursor_x, cursor_y, centre_x, centre_y));
            
            final Matrix3x2fStack pose2 = graphics.pose().pushMatrix();
            {
                pose2.translate((int) (this.cursorX - 9), ((int) this.cursorY - 9));
                pose2.rotateAbout(((float) this.rotation * -1f), 8f, 8f);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ToolbeltWheelScreen.CURSOR_TEXTURE, 0, 0, 16, 16);
            }
            pose2.popMatrix();
        }
        else
        {
            graphics.requestCursor(CursorTypes.POINTING_HAND);

            if (!this.isArrowMode)
            {
                this.isArrowMode = true;
                GLFW.glfwSetInputMode(this.minecraft.getWindow().handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        }
        
        this.toolEntries.getFirst().render(graphics, mouseX, mouseY, partialTick);
        
        if (this.hovered != null)
        {
            final Component text = this.hovered.getMessage();
            final int       x    = (this.wheelBounds.getCenterInAxis(ScreenAxis.HORIZONTAL) - this.font.width(text) / 2);
            final int       y    = (this.wheelBounds.top() - this.font.lineHeight - 7);
            graphics.drawString(this.font, this.hovered.getMessage(), x, y, -1);
        }
    }
    //==================================================================================================================
    @Override
    public void onClose()
    {
        if (this.minecraft.screen == this)
        {
            final LocalPlayer player = this.minecraft.player;
        
            if (player != null && player.isAlive() && !player.hasContainerOpen())
            {
                this.handleToolSwap(player);
            }
            
            super.onClose();
            CteerToolbeltKeybinds.SHOW_TOOLBELT_WHEEL.setDown(false);
        }
    }
    
    @Override
    public void added()
    {
        final LocalPlayer player = this.minecraft.player;
        
        if (player != null)
        {
            //noinspection UnstableApiUsage
            player.setAttached(CteerClientPlayerAttachments.WHEEL_SCREEN, this);
        }
    }
    
    @Override
    public void removed()
    {
        final LocalPlayer player = this.minecraft.player;
        
        if (player != null)
        {
            //noinspection UnstableApiUsage
            player.removeAttached(CteerClientPlayerAttachments.WHEEL_SCREEN);
        }
    }
    
    @Override
    public void hoverStateChanged(final ToolEntryWidget widget, final boolean isHovered)
    {
        this.hovered = this.toolEntries.stream().filter(ToolEntryWidget::isHovered).findFirst().orElse(null);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private void handleToolSwap(final LocalPlayer player)
    {
        final ToolEntryWidget selected = this.toolEntries.stream()
            .filter(widget -> (widget.isHoveredOrFocused() && widget.isActive()))
            .findFirst()
            .orElse(null);
        
        if (selected == null)
        {
            return;
        }
        
        final ItemStack toolbelt = this.slot.getEquipmentFromPlayer(player);
        
        if (!ItemStack.isSameItemSameComponents(this.toolbelt, toolbelt))
        {
            CteerDefine.LOGGER.error("item at the given slot has changed and is no longer the initial toolbelt item");
            return;
        }
        
        final ItemStack             hand_stack = player.getMainHandItem();
        final PlayC2SUpdateHeldTool payload
            = new PlayC2SUpdateHeldTool(hand_stack.copy(), this.slot, this.toolbelt.copy(), (short) selected.slotId);
        
        if (payload.handleSwap(player, hand_stack, toolbelt))
        {
            ClientPlayNetworking.send(payload);
        }
    }
}
