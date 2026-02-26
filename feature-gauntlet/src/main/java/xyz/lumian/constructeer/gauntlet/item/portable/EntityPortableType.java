package xyz.lumian.constructeer.gauntlet.item.portable;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.integration.accessory.AccessorySlot;
import xyz.lumian.constructeer.integration.accessory.IAccessory;

import java.util.Optional;



//**********************************************************************************************************************
public class EntityPortableType
    extends IPortableType<Mob>
{
    //******************************************************************************************************************
    
    
    //******************************************************************************************************************
    EntityPortableType()
    {
        UseEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) ->
        {
            if (player.isSpectator())
            {
                return InteractionResult.PASS;
            }
            
            final ItemStack stack = AccessorySlot
                .findEquipmentStack(IAccessory.SlotConstants.HAND, player, CteerGauntletItems.GAUNTLET_OF_POWER)
                .orElse(null);
            
            if (stack == null)
            {
                return InteractionResult.PASS;
            }
            
            return Optional
                .ofNullable(Portable.tryPickup(
                    stack,
                    player,
                    entity.blockPosition(),
                    this,
                    (() -> new EntityData(entity.getUUID(), entity))))
                .orElse(InteractionResult.PASS);
        }));
    }
    
    //==================================================================================================================
    @Override public MapCodec<Entity>                             codec()       { return Entity.MAP_CODEC; }
    @Override public StreamCodec<RegistryFriendlyByteBuf, Entity> streamCodec() { return Entity.STREAM_CODEC; }
    
    //==================================================================================================================
    @Override
    public boolean canPlace(final Entity object, final Player player, final BlockPos pos)
    {
        return true;
    }
    
    @Override
    public boolean canTake(final Entity object, final Player player, final BlockPos pos)
    {
        return true;
    }
    
    //==================================================================================================================
    @Override
    public @Nullable Entity pickup(final Entity entity, final Player player, final BlockPos pos)
    {
        if (player.isPassenger())
        {
            return null;
        }
        
        
        if (!entity.startRiding(player))
        {
            return null;
        }
        
        return entity;
    }
    
    @Override
    public boolean place(final Entity object, Player player, BlockPos clickPos, BlockPlaceContext context)
    {
        if (!player.isVehicle())
        {
            return true;
        }
        
        return true;
    }
    
    //==================================================================================================================
    @Override
    public void discard(final Entity object, final Player player)
    {
    
    }
}
