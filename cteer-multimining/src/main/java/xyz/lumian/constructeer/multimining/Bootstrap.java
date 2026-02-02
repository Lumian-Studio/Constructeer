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

import xyz.lumian.constructeer.multimining.container.ModMenus;
import xyz.lumian.constructeer.multimining.entity.ModEntities;
import xyz.lumian.constructeer.multimining.integration.Compat;
import xyz.lumian.constructeer.multimining.item.ModItems;
import xyz.lumian.constructeer.multimining.item.component.ModComponents;
import xyz.lumian.constructeer.multimining.item.component.MultiMining;
import xyz.lumian.constructeer.multimining.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.multimining.item.multimining.damage.IMultiMiningDamageType;
import xyz.lumian.constructeer.multimining.item.multimining.predicate.MultiMiningPredicateType;
import xyz.lumian.constructeer.multimining.item.multimining.timber.TimberMode;
import xyz.lumian.constructeer.multimining.item.recipe.ModRecipeSerialisers;
import xyz.lumian.constructeer.multimining.network.ModPayloads;
import xyz.lumian.constructeer.multimining.registry.ModRegistries;
import xyz.lumian.constructeer.multimining.sound.ModSoundEvents;
import xyz.lumian.constructeer.multimining.stat.ModStats;



//**********************************************************************************************************************
public class Bootstrap
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ModRegistries           .initialise();
        ModComponents           .initialise();
        ModItems                .initialise();
        ModMenus                .initialise();
        ModPayloads             .initialise();
        ModRecipeSerialisers    .initialise();
        Compat                  .initialise();
        MultiMining             .initialise();
        ModStats                .initialise();
        AreaProviderType        .initialise();
        MultiMiningPredicateType.initialise();
        TimberMode              .initialise();
        ModEntities             .initialise();
        ModSoundEvents          .initialise();
        IMultiMiningDamageType  .initialise();
    }
}
