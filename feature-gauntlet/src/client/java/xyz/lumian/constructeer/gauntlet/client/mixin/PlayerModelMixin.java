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
package xyz.lumian.constructeer.gauntlet.client.mixin;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.IPortableRenderer;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.PortableRenderExtension;
import xyz.lumian.constructeer.gauntlet.client.renderer.state.CteerGauntletRenderDataKeys;
import xyz.lumian.constructeer.gauntlet.item.portable.Portable;



//**********************************************************************************************************************
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
        at     = @At("TAIL")
    )
    public void updatePortableAnimation(final AvatarRenderState renderState, final CallbackInfo ci)
    {
        final Portable<?> portable = renderState.getData(CteerGauntletRenderDataKeys.PORTABLE);
        
        if (portable == null)
        {
            return;
        }
        
        final IPortableRenderer<?> renderer = ((PortableRenderExtension<?>) portable.type()).getRenderer();
        
        if (renderer == null)
        {
            return;
        }
        
        this.animate(portable, renderer, renderState);
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @Unique
    private <T, T2> void animate(final Portable<T> portable, final IPortableRenderer<T2> renderer,
                                 final AvatarRenderState renderState)
    {
        //noinspection unchecked
        renderer.animate((T2) portable.object(), renderState, (PlayerModel) (Object) this);
    }
}
