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
package xyz.lumian.constructeer.toolbelt.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.lumian.constructeer.registry.IBootstrap;
import xyz.lumian.constructeer.toolbelt.network.serverbound.PlayC2SOpenToolbeltConfig;
import xyz.lumian.constructeer.toolbelt.network.serverbound.PlayC2SUpdateHeldTool;
import xyz.lumian.constructeer.network.CteerNetworkRegistry;



//**********************************************************************************************************************
public final class CteerToolbeltPayloads
    implements IBootstrap
{
    //******************************************************************************************************************
    // Play client -> server
    public static final CustomPacketPayload.Type<PlayC2SOpenToolbeltConfig> OPEN_TOOLBELT_SCREEN = CteerNetworkRegistry
        .registerPlayC2S(PlayC2SOpenToolbeltConfig.ID, PlayC2SOpenToolbeltConfig.CODEC);
    public static final CustomPacketPayload.Type<PlayC2SUpdateHeldTool> UPDATE_HELD_TOOL = CteerNetworkRegistry
        .registerPlayC2S(PlayC2SUpdateHeldTool.ID, PlayC2SUpdateHeldTool.CODEC);
}
