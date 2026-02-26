package xyz.lumian.constructeer.gauntlet.client.renderer.portable;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;



//**********************************************************************************************************************
@ApiStatus.Internal
public interface PortableRenderExtension<T>
{
    //******************************************************************************************************************
    @Nullable IPortableRenderer<T> getRenderer();
    void setRenderer(@Nullable IPortableRenderer<T> renderer);
}
