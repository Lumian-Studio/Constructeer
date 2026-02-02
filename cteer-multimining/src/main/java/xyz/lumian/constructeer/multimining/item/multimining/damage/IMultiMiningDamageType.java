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
package xyz.lumian.constructeer.multimining.item.multimining.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;
import xyz.lumian.constructeer.multimining.util.BlockContext;



//**********************************************************************************************************************
public interface IMultiMiningDamageType
{
    //******************************************************************************************************************
	Holder<IMultiMiningDamageType> SINGLE = register(ModDefine.id("single"), DamageTypes.SINGLE);
	Holder<IMultiMiningDamageType> ALL    = register(ModDefine.id("all"),    DamageTypes.ALL);
    
    //******************************************************************************************************************
    static void initialise() {}
    
    //==================================================================================================================
    static <T extends IMultiMiningDamageType> Holder<T> register(final Identifier id, final T type)
    {
        return Registry.registerForHolder(ModRegistries.BuiltIn.MULTI_MINING_DAMAGE_TYPE, id, type);
    }
    
    //******************************************************************************************************************
    boolean shouldDamage(BlockContext block);
}
