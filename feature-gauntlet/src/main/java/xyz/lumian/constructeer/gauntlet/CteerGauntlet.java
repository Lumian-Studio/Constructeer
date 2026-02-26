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
package xyz.lumian.constructeer.gauntlet;

import net.fabricmc.api.ModInitializer;
import xyz.lumian.constructeer.Constructeer;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletDataComponents;
import xyz.lumian.constructeer.gauntlet.item.CteerGauntletItems;
import xyz.lumian.constructeer.gauntlet.item.portable.CteerPortableTypes;
import xyz.lumian.constructeer.gauntlet.item.portable.Portable;
import xyz.lumian.constructeer.gauntlet.player.CteerGauntletPlayerAttachments;
import xyz.lumian.constructeer.gauntlet.registry.CteerGauntletRegistries;
import xyz.lumian.constructeer.registry.IBootstrap;



//**********************************************************************************************************************
public class CteerGauntlet
    implements ModInitializer
{
    //******************************************************************************************************************
    public static final IBootstrap.Loader LOADER = IBootstrap.Loader.BEGIN
        .with(CteerGauntletItems::new)
        .with(CteerGauntletRegistries::new)
        .with(CteerGauntletDataComponents::new)
        .with(CteerGauntletPlayerAttachments::new)
        .with(CteerPortableTypes::new)
        .with(IBootstrap.bootstrappable(Portable.class, (report -> Portable.initialise())));
    
    //******************************************************************************************************************
    @Override
    public void onInitialize()
    {
        Constructeer.registerBootstrapper(CteerGauntlet.LOADER);
    }
}
