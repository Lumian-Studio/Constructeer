package xyz.lumian.constructeer.network.server;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import xyz.lumian.constructeer.ModDefine;
import xyz.lumian.constructeer.item.multimining.area.AreaProviderType;
import xyz.lumian.constructeer.network.ModPayloads;
import xyz.lumian.constructeer.registry.ModRegistries;

import java.util.Map;
import java.util.function.Consumer;



//**********************************************************************************************************************
public record ConfigS2CFeatureSync(
    String               modVersion,
    Map<String, Integer> areaProviderApiVersion
) implements CustomPacketPayload
{
    //******************************************************************************************************************
    public static Map<String, Integer> collectAreaProviderTypes(final Registry<AreaProviderType<?>> registry)
    {
        return registry.entrySet().stream().collect(ImmutableMap.toImmutableMap(
            (e -> e.getKey().identifier().toString()),
            (e -> e.getValue().apiVersion())));
    }
    
    //******************************************************************************************************************
    public record Task()
        implements ConfigurationTask
    {
        //**************************************************************************************************************
        public static final ConfigurationTask.Type TYPE
            = new ConfigurationTask.Type(ModDefine.formatId("%s_feature_sync"));
        
        //**************************************************************************************************************
        @Override public Type type() { return Task.TYPE; }
        
        //==============================================================================================================
        @Override
        public void start(final Consumer<Packet<?>> task)
        {
            task.accept(ServerConfigurationNetworking.createS2CPacket(new ConfigS2CFeatureSync(
                ModDefine.MOD_VERSION,
                ConfigS2CFeatureSync.collectAreaProviderTypes(ModRegistries.BuiltIn.AREA_PROVIDER_TYPE))));
        }
    }
    
    //******************************************************************************************************************
    public static final Identifier ID = ModDefine.id("feature_sync");
    
    public static final StreamCodec<FriendlyByteBuf, ConfigS2CFeatureSync> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ConfigS2CFeatureSync::modVersion,
        ByteBufCodecs.map(Object2ObjectArrayMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT),
            ConfigS2CFeatureSync::areaProviderApiVersion,
        ConfigS2CFeatureSync::new);
    
    //******************************************************************************************************************
    @Override public Type<ConfigS2CFeatureSync> type() { return ModPayloads.CONFIGURE_FEATURE_SYNC; }
}
