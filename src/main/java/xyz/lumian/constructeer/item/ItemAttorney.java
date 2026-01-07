package xyz.lumian.constructeer.item;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import xyz.lumian.constructeer.ConstructeerMain;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.config.ModServerConfig;
import xyz.lumian.constructeer.util.RegistryId;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;



//**********************************************************************************************************************
@ApiStatus.Internal
public class ItemAttorney
{
    //******************************************************************************************************************
    public static void updateValidTools(final RegistryAccess access)
    {
        final Registry<Item> registry = access.lookupOrThrow(Registries.ITEM);
        PouchItem.VALID_TOOLS.set(HolderSet.direct(ModServerConfig.INSTANCE.pouchAllowedTools().get()
            .stream()
            .flatMap(str ->
            {
                final RegistryId<Item>               id      = RegistryId.parse(Registries.ITEM, str);
                final Optional<Stream<Holder<Item>>> holders = id.resolveOptional(registry)
                    .map(HolderSet::stream);
                
                if (holders.isEmpty())
                {
                    ModDefine.LOGGER.warn("unknown registry entry for {}", id);
                    return Stream.empty();
                }
                
                return holders.orElseThrow();
            })
            .distinct()
            .toList()));
    }
    
    public static AtomicReference<HolderSet<Item>> getValidPouchTools() { return PouchItem.VALID_TOOLS; }
}
