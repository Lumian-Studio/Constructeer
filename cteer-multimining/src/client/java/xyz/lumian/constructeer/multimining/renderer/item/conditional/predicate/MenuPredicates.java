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
package xyz.lumian.constructeer.multimining.renderer.item.conditional.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import xyz.lumian.constructeer.ModDefine;

import java.util.function.Function;



//**********************************************************************************************************************
public final class MenuPredicates
{
    //******************************************************************************************************************
    public static final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends MenuPredicate>> ID_MAPPER;
    public static final MapCodec<MenuPredicate>                                                      MAP_CODEC;

    //==================================================================================================================
    static
    {
        ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
        MAP_CODEC = ID_MAPPER
            .codec(Identifier.CODEC)
            .dispatchMap("predicate", MenuPredicate::type, Function.identity());
    }
    
    //******************************************************************************************************************
    public static void initialise()
    {
        MenuPredicates.register(ModDefine.id("toolbelt/in_pouch_slot"), InPouchSlot.MAP_CODEC);
        MenuPredicates.register(ModDefine.id("always_true"),            AlwaysTrue.MAP_CODEC);
    }
    
    //==================================================================================================================
    public static void register(final Identifier id, final MapCodec<? extends MenuPredicate> codec)
    {
        MenuPredicates.ID_MAPPER.put(id, codec);
    }
    
    //******************************************************************************************************************
    private MenuPredicates() {}
}
