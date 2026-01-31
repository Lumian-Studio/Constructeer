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
package xyz.lumian.constructeer.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;



//**********************************************************************************************************************
public final class ModEntityDataSerialisers
{
    //******************************************************************************************************************
    public static final EntityDataSerializer<MultiMiningBox> FALLING_OBJECT_RENDER_BOX
        = register("falling_object_render_box", MultiMiningBox.STREAM_CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static <T> EntityDataSerializer<T> register(final String name,
                                                        final StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        final EntityDataSerializer<T> serialiser = EntityDataSerializer.forValueType(codec);
        FabricTrackedDataRegistry.register(ModDefine.id(name), serialiser);
        return serialiser;
    }
    
    //******************************************************************************************************************
    private ModEntityDataSerialisers() {}
}
