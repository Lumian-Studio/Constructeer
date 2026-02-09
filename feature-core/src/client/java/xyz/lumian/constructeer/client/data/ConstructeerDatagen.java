package xyz.lumian.constructeer.client.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.registry.CteerItemRegistry;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;



//**********************************************************************************************************************
public class ConstructeerDatagen
    implements DataGeneratorEntrypoint
{
    //******************************************************************************************************************
    private static final class LangProvider
        extends FabricLanguageProvider
    {
        //**************************************************************************************************************
        public LangProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup)
        {
            super(output, "en_us", lookup);
        }
        
        //==============================================================================================================
        @Override
        protected Path getLangFilePath(final String code)
        {
            return this.dataOutput
                .createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang")
                .json(CteerDefine.id(code));
        }
        
        //==============================================================================================================
        @Override
        public void generateTranslations(final HolderLookup.Provider provider, final TranslationBuilder builder)
        {
            builder.add(CteerItemRegistry.MAIN_TAB_KEY, CteerDefine.MOD_NAME);
            builder.add(CteerItemRegistry.TROPHY,       (CteerDefine.MOD_NAME + " Trophy"));
        }
    }
    
    //******************************************************************************************************************
    @Override
    public void onInitializeDataGenerator(final FabricDataGenerator generator)
    {
        final FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(LangProvider::new);
    }
}
