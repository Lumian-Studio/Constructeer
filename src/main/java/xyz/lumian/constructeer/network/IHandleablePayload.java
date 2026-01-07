package xyz.lumian.constructeer.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;



//**********************************************************************************************************************
public interface IHandleablePayload<Context>
    extends CustomPacketPayload
{
    //******************************************************************************************************************
    void handle(Context ctx);
}
