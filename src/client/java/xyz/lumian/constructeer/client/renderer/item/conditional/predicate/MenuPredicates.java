package xyz.lumian.constructeer.client.renderer.item.conditional.predicate;

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
