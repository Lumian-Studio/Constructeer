package xyz.lumian.constructeer.client.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.integration.accessory.IAccessory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;



//**********************************************************************************************************************
public abstract class AccessoryDataProvider
    extends FabricCodecDataProvider<AccessoryDataProvider.AccessoryInfo>
{
    //******************************************************************************************************************
    public record AccessoryInfo(ImmutableList<EntityType<?>> entityTypes, ImmutableList<IAccessory.SlotReference> slots)
    {
        //**************************************************************************************************************
        public static final Codec<AccessoryInfo> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                EntityType.CODEC.listOf()
                    .fieldOf("entities")
                    .xmap(ImmutableList::copyOf, Function.identity())
                    .forGetter(AccessoryInfo::entityTypes),
                Codec.STRING
                    .xmap(
                        (str -> IAccessory.SlotReference.of(str, EquipmentSlot.HEAD)),
                        IAccessory.SlotReference::id)
                    .listOf()
                    .xmap(ImmutableList::copyOf, Function.identity())
                    .fieldOf("slots")
                    .forGetter(AccessoryInfo::slots))
            .apply(inst, AccessoryInfo::new));
    }
    
    public interface Builder
    {
        //**************************************************************************************************************
        Builder addAll(Collection<EntityType<?>> types);
        
        default Builder addAll(final EntityType<?> ...types) { return this.addAll(Arrays.asList(types)); }
        
        default Builder add(final EntityType<?> type) { return this.addAll(type); }
        
        //==============================================================================================================
        AccessoryInfo build(Collection<IAccessory.SlotReference> slots);
        
        default AccessoryInfo build(final IAccessory.SlotReference ...slots) { return this.build(Arrays.asList(slots)); }
    }
    
    //******************************************************************************************************************
    private final Map<Identifier, AccessoryInfo> accessories = new HashMap<>();
    
    //******************************************************************************************************************
    public AccessoryDataProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup, PackOutput.Target.DATA_PACK, "entities", AccessoryInfo.CODEC);
    }
    
    //==================================================================================================================
    public Builder builder()
    {
        return new Builder()
        {
            //**********************************************************************************************************
            private final ImmutableList.Builder<EntityType<?>> types = ImmutableList.builder();
            
            //**********************************************************************************************************
            @Override
            public Builder addAll(final Collection<EntityType<?>> types)
            {
                this.types.addAll(types);
                return this;
            }
            
            @Override
            public AccessoryInfo build(final Collection<IAccessory.SlotReference> slots)
            {
                return new AccessoryInfo(this.types.build(), ImmutableList.copyOf(slots));
            }
        };
    }
    
    public void addDefinition(final Identifier id, final AccessoryInfo trinket)
    {
        if (this.accessories.put(id, trinket) != null)
        {
            throw new IllegalStateException("Accessory definition with id '%s' already exists".formatted(id));
        }
    }
    
    //==================================================================================================================
    public abstract void addTrinkets();
    
    //==================================================================================================================
    @Override
    protected void configure(final BiConsumer<Identifier, AccessoryInfo> consumer, final HolderLookup.Provider provider)
    {
        this.addTrinkets();
        this.accessories.forEach((id, accessory) -> consumer.accept(
            Identifier.fromNamespaceAndPath(
                CteerDefine.Integrations.ACCESSORY,
                id.toShortLanguageKey().replace('/', '.')),
            accessory));
    }
}
