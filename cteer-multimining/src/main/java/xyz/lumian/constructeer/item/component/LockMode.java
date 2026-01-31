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
package xyz.lumian.constructeer.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import xyz.lumian.constructeer.item.ToolbeltItem;

import java.util.Locale;



//**********************************************************************************************************************
public enum LockMode
    implements StringRepresentable
{
    /// Any tool can go in the slot
    UNLOCKED,
    
    /// Only tools that satisfy the filter (and different variants of it like stone or wood pickaxes)
    /// can go in the slot
    LOCKED,
    
    /// Only a specific variant of a specific item can go in the slot
    LOCKED_STRICT,
    ;
    
    //******************************************************************************************************************
    public static final Codec<LockMode> CODEC = StringRepresentable.fromEnum(LockMode::values);
    
    public static final StreamCodec<ByteBuf, LockMode> STREAM_CODEC = ByteBufCodecs.BYTE
        .map((b -> LockMode.values()[(int) b]), (e -> (byte) e.ordinal()));
    
    //******************************************************************************************************************
    @Override public String getSerializedName() { return this.name().toLowerCase(Locale.ROOT); }
}
