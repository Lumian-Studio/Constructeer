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
package xyz.lumian.constructeer.gauntlet.client.renderer.portable;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.gauntlet.item.portable.CteerPortableTypes;
import xyz.lumian.constructeer.gauntlet.item.portable.IPortableType;
import xyz.lumian.constructeer.gauntlet.registry.CteerGauntletRegistries;
import xyz.lumian.constructeer.registry.BootstrapReport;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.util.FreezableMap;



//**********************************************************************************************************************
public final class PortableRenderRegistry
    implements IBootstrap
{
    //******************************************************************************************************************
    private static final FreezableMap<IPortableType<?>, IPortableRenderer.Factory<?>> FACTORIES
        = new FreezableMap<>(new Reference2ObjectArrayMap<>());
    
    //******************************************************************************************************************
    public static <T> void register(final IPortableType<T> type, final IPortableRenderer.Factory<T> factory)
    {
        final Identifier id = CteerGauntletRegistries.PORTABLE_TYPE.getKey(type);
        
        if (id == null)
        {
            throw new RuntimeException("no portable type for id '%s' registered".formatted(type.getClass().getName()));
        }
        
        if (PortableRenderRegistry.FACTORIES.put(type, factory) != null)
        {
            throw new IllegalStateException("duplicate portable renderer for portable type '" + id + '\'');
        }
        
        Constructeer.sendGlobalBootstrapReport("registered renderer for portable type '%s'", id);
    }
    
    //==================================================================================================================
    private static <T, T2> void createAndSetRenderer(final IPortableRenderer.FactoryContext context,
                                                     final IPortableType<T> type,
                                                     final IPortableRenderer.Factory<T2>    factory)
    {
        //noinspection unchecked
        ((PortableRenderExtension<T>) type).setRenderer((IPortableRenderer<T>) factory.create(context));
    }
    
    //******************************************************************************************************************
    @Override
    public void bootstrap(final BootstrapReport report)
    {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, helper, context) ->
        {
            if (type == EntityType.PLAYER && renderer instanceof AvatarRenderer<?> rend)
            {
                final IPortableRenderer.FactoryContext f_ctx = new IPortableRenderer.FactoryContext(rend, context);
                PortableRenderRegistry.FACTORIES.forEach((p_type, factory) -> PortableRenderRegistry
                    .createAndSetRenderer(f_ctx, p_type, factory));
                helper.register(new PortableRenderLayer(rend));
            }
        });
        
        PortableRenderRegistry.register(CteerPortableTypes.BLOCK, BlockPortableRenderer::new);
    }
    
    @Override public void freeze(final BootstrapReport report) { PortableRenderRegistry.FACTORIES.freeze(); }
}
