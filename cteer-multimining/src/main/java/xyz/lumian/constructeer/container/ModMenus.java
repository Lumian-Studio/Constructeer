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
package xyz.lumian.constructeer.container;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModMenus
{
    //******************************************************************************************************************
    public static final MenuType<ToolbeltMenu> TOOLBELT = ModMenus.register("toolbelt", ToolbeltMenu::client);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    public static <T extends AbstractContainerMenu> MenuType<T> register(final String                   name,
                                                                         final MenuType.MenuSupplier<T> supplier)
    {
        final ResourceKey<MenuType<?>> key = ResourceKey.create(Registries.MENU, ModDefine.id(name));
        return Registry.register(BuiltInRegistries.MENU, key, new MenuType<>(supplier, FeatureFlagSet.of()));
    }
    
    //******************************************************************************************************************
    private ModMenus() {}
}
