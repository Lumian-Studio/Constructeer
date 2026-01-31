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
package xyz.lumian.constructeer.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.predicate.ToolPredicate;
import xyz.lumian.constructeer.registry.RegistryId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;



//**********************************************************************************************************************
public final class ConfigHelper
{
    //******************************************************************************************************************
    public static final Supplier<String> DEFAULT_ID_SUPPLIER = Suppliers.memoize(() -> "namespace:path");
    
    //******************************************************************************************************************
    public static HolderSet<Block> resolveIDs(final Stream<RegistryId<Block>> ids)
    {
        final Registry<Block> registry = BuiltInRegistries.BLOCK;
        return HolderSet.direct(ids
            .flatMap(id ->
            {
                final Optional<HolderSet<Block>> blocks_opt = id.resolveOptional(registry);
                
                if (blocks_opt.isEmpty())
                {
                    ModDefine.LOGGER.warn("the given id \"{}\" cannot be found in the block registry", id);
                    return Stream.empty();
                }
                
                return blocks_opt.orElseThrow().stream();
            })
            .distinct()
            .toList());
    }
    
    public static ToolPredicate.BlockPredicate resolveBlockPredicate(final Object object)
    {
        return new ToolPredicate.BlockPredicate(switch (object)
        {
            case List<?> list -> Either.right(list.stream()
                .map(str -> RegistryId.parse(Registries.BLOCK, (String) str))
                .collect(ImmutableList.toImmutableList()));
            case String  str  -> Either.left(RegistryId.parse(Registries.BLOCK, str));
            
            default ->
                throw new IllegalArgumentException("object is neither a registry id nor a list of registry IDs");
        });
    }
    
    //==================================================================================================================
    public static boolean validateRegistryId(final Object object)
    {
        return (object instanceof String str && RegistryId.isValidRegistryId(str));
    }
    
    public static boolean validateListOfRegistryIds(final Object object)
    {
        if (!(object instanceof List<?> list))
        {
            return false;
        }
        
        return list.stream().allMatch(ConfigHelper::validateRegistryId);
    }
    
    public static boolean validateBlockPredicate(final Object object)
        {
            if (object instanceof List<?> list)
            {
                for (final var element : list)
                {
                    if (!(element instanceof String str) || !RegistryId.isValidRegistryId(str))
                    {
                        return false;
                    }
                }
                
                return true;
            }
            else if (object instanceof String str)
            {
                return RegistryId.isValidRegistryId(str);
            }
            
            return false;
        }
    
    //******************************************************************************************************************
    private ConfigHelper() {}
}
