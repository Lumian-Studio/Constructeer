package xyz.lumian.constructeer.item.component.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModComponentPredicates
{
    //******************************************************************************************************************
    public static final DataComponentPredicate.Type<PouchPredicate> POUCH_CONTENT = register(
        "pouch_content",
        PouchPredicate.CODEC);
    
    //******************************************************************************************************************
    public static void initialise() {}
    
    //==================================================================================================================
    private static <T extends DataComponentPredicate> DataComponentPredicate.Type<T> register(final String   path,
                                                                                              final Codec<T> codec)
    {
		return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE,
            ResourceKey.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, ModDefine.id(path)),
            new DataComponentPredicate.ConcreteType<>(codec));
	}
    
    //******************************************************************************************************************
    private ModComponentPredicates() {}
}
