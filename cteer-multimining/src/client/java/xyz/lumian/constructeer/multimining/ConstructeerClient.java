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
package xyz.lumian.constructeer.multimining;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ModConfig;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.config.ModClientConfig;



//**********************************************************************************************************************
public class ConstructeerClient
    implements ClientModInitializer
{
    //******************************************************************************************************************
	@Override
	public void onInitializeClient()
    {
		ClientBootstrap.initialise();
        ConfigRegistry.INSTANCE.register(ModDefine.MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        
        // Reload whatever can be reloaded once any of our configs reloaded
        ModConfigEvents.reloading(ModDefine.MOD_ID).register(this::reloadConfig);
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> ConstructeerMain.reloadServerConfig()));
	}
    
    //==================================================================================================================
    private void reloadConfig(final ModConfig config)
    {
        if (config.getType() == ModConfig.Type.SERVER)
        {
            this.reloadValidTools(Minecraft.getInstance());
        }
    }
    
    private void reloadValidTools(final Minecraft mc)
    {
        // we are on the physical client and on a dedicated server
        if (mc.level != null && mc.getSingleplayerServer() == null)
        {
            ConstructeerMain.reloadServerConfig();
        }
    }
}
