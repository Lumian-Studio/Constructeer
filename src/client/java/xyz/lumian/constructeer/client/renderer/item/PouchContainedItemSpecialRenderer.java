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
package xyz.lumian.constructeer.client.renderer.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.item.component.ModComponents;
import xyz.lumian.constructeer.item.component.PouchContent;



//**********************************************************************************************************************
public final class PouchContainedItemSpecialRenderer
    implements ItemModel
{
    //******************************************************************************************************************
    public record Unbaked()
        implements ItemModel.Unbaked
    {
        //**************************************************************************************************************
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());
        
        //**************************************************************************************************************
        public MapCodec<Unbaked> type() { return Unbaked.MAP_CODEC; }

        //**************************************************************************************************************
        @Override
        public ItemModel bake(final ItemModel.BakingContext ctx)
        {
            return PouchContainedItemSpecialRenderer.INSTANCE;
        }

        @Override public void resolveDependencies(final ResolvableModel.Resolver resolver) {}
    }
    
    //******************************************************************************************************************
    static final ItemModel INSTANCE = new PouchContainedItemSpecialRenderer();
    
    //******************************************************************************************************************
    @Override
    public void update(final ItemStackRenderState renderState, final ItemStack stack, final ItemModelResolver resolver,
                       final ItemDisplayContext ctx, final @Nullable ClientLevel level, final @Nullable ItemOwner owner,
                       int i)
    {
        final ItemStack content = stack.getOrDefault(ModComponents.POUCH_CONTENT, PouchContent.EMPTY).content();
        renderState.appendModelIdentityElement(this);
        
        if (!content.isEmpty())
        {
            resolver.appendItemLayers(renderState, content, ctx, level, owner, i);
        }
    }
}
