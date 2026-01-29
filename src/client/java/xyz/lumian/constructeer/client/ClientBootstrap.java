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
package xyz.lumian.constructeer.client;

import xyz.lumian.constructeer.client.gui.screen.ModScreens;
import xyz.lumian.constructeer.client.integration.ClientCompat;
import xyz.lumian.constructeer.client.model.ModModelLayers;
import xyz.lumian.constructeer.client.network.ModClientPayloadReceiver;
import xyz.lumian.constructeer.client.renderer.ModRenderer;



//**********************************************************************************************************************
public final class ClientBootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModScreens              .initialise();
        ModKeybinds             .initialise();
        ModModelLayers          .initialise();
        ClientCompat            .initialise();
        ModClientPayloadReceiver.initialise();
        ModRenderer             .initialise();
    }
    
    //******************************************************************************************************************
    private ClientBootstrap() {}
}
