package xyz.lumian.constructeer.toolbelt.container;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import xyz.lumian.constructeer.integration.Compat;
import xyz.lumian.constructeer.integration.trinkets.AbstractExtendedEquipmentSlot;
import xyz.lumian.constructeer.integration.trinkets.ITrinkets;
import xyz.lumian.constructeer.toolbelt.item.CteerToolbeltItems;

import java.util.stream.Stream;



//**********************************************************************************************************************
public class ToolbeltEquipmentSlot
    extends AbstractExtendedEquipmentSlot
{
    //******************************************************************************************************************
    public static final StreamCodec<ByteBuf, ToolbeltEquipmentSlot> STREAM_CODEC = AbstractExtendedEquipmentSlot
        .createStreamCodec(ToolbeltEquipmentSlot::new);
    
    //******************************************************************************************************************
    public static ToolbeltEquipmentSlot findToolbelt(final Player player)
    {
        return Compat.getTrinkets().flatMap(compat -> compat
            .getItems(player, ITrinkets.DefaultSlot.LEGS_BELT)
                .orElse(Stream.empty())
                .filter(stack -> stack.stack().is(CteerToolbeltItems.TOOLBELT))
                .findFirst()
                .map(stack -> new ToolbeltEquipmentSlot(stack.slot())))
            .orElseGet(() -> new ToolbeltEquipmentSlot(EquipmentSlot.LEGS));
    }
    
    //******************************************************************************************************************
    public ToolbeltEquipmentSlot(final EquipmentSlot mcEquipmentSlot) { super(mcEquipmentSlot); }
    public ToolbeltEquipmentSlot(final int trinketSlotId)             { super(trinketSlotId);   }
    
    //==================================================================================================================
    protected ToolbeltEquipmentSlot(final short value) { super(value); }
    
    //==================================================================================================================
    @Override public ITrinkets.SlotReference getSlotReference() { return ITrinkets.DefaultSlot.LEGS_BELT; }
}
