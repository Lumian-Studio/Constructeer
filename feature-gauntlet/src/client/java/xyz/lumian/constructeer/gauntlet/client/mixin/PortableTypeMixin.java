package xyz.lumian.constructeer.gauntlet.client.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.IPortableRenderer;
import xyz.lumian.constructeer.gauntlet.client.renderer.portable.PortableRenderExtension;
import xyz.lumian.constructeer.gauntlet.item.portable.IPortableType;



//**********************************************************************************************************************
@Mixin(IPortableType.class)
public abstract class PortableTypeMixin<T>
    implements PortableRenderExtension<T>
{
    //******************************************************************************************************************
    @Unique private @Nullable IPortableRenderer<T> renderer = null;
    
    //******************************************************************************************************************
    @Unique
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public @Nullable IPortableRenderer<T> getRenderer() { return this.renderer; }
    
    @Unique
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setRenderer(final @Nullable IPortableRenderer<T> renderer) { this.renderer = renderer; }
}
