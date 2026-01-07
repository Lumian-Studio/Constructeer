package xyz.lumian.constructeer.client.renderer.item;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.impl.datagen.client.FabricModelProviderDefinitions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.ModDefine;
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
