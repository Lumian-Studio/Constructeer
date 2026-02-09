package xyz.lumian.constructeer.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack.RegistryDependentFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;



//**********************************************************************************************************************
public interface IDictionary
    extends Component
{
    //******************************************************************************************************************
    class Provider<E extends Enum<E> & IDictionary>
        extends FabricLanguageProvider
    {
        //**************************************************************************************************************
        private static final class Empty
            extends FabricLanguageProvider
        {
            //**********************************************************************************************************
            public Empty(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup,
                         final String lang)
            {
                super(output, lang, lookup);
            }
            
            //==========================================================================================================
            @Override
            public void generateTranslations(final HolderLookup.Provider lookup, final TranslationBuilder builder)
            {}
        }
        
        //**************************************************************************************************************
        private final EnumMap<E, String> translations;
        
        //**************************************************************************************************************
        public Provider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> lookup,
                        final String lang, final E[] values)
        {
            super(output, lang, lookup);
            
            if (values.length == 0)
            {
                throw new IllegalStateException("The given dictionary has no constants to generate");
            }
            
            this.translations = new EnumMap<>(values[0].getDeclaringClass());
        }
        
        //==============================================================================================================
        public void set   (final E key, final String text)    { this.translations.put(key, text); }
        public void setAll(final Map<E, String> translations) { this.translations.putAll(translations); }
        
        //==============================================================================================================
        @Override
        public void generateTranslations(final HolderLookup.Provider lookup, final TranslationBuilder builder)
        {
            this.translations.forEach((e, v) -> builder.add(e.getKey(), v));
            this.generateAdditional(lookup, builder);
        }
        
        public void generateAdditional(HolderLookup.Provider lookup, TranslationBuilder builder) {}
    }
    
    @FunctionalInterface
    interface CustomFactory<E extends Enum<E> & IDictionary>
    {
        //**************************************************************************************************************
        Provider<E> create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup, String lang,
                           E[] values);
    }
    
    //******************************************************************************************************************
    static <E extends Enum<E> & IDictionary> RegistryDependentFactory<FabricLanguageProvider>
    makeFactory(
        final Consumer<BiConsumer<E, String>>       translationBuilder,
        final RegistryDependentFactory<Provider<E>> customFactory
    )
    {
        return ((output, lookup) ->
        {
            final Provider<E> provider = customFactory.create(output, lookup);
            translationBuilder.accept(provider.translations::put);
            return provider;
        });
    }
    
    static <E extends Enum<E> & IDictionary> RegistryDependentFactory<FabricLanguageProvider>
    makeFactory(
        final E[]                             values,
        final String                          language,
        final Consumer<BiConsumer<E, String>> translationBuilder
    )
    {
        if (values.length == 0)
        {
            return ((output, lookup) -> new Provider.Empty(output, lookup, language));
        }
        
        return IDictionary.makeFactory(
            translationBuilder,
            ((output, lookup) -> new Provider<>(output, lookup, language, values)));
    }
    
    static <E extends Enum<E> & IDictionary> RegistryDependentFactory<FabricLanguageProvider>
    makeCustomFactoryWithDefaults(
        final E[]              values,
        final CustomFactory<E> customFactory
    )
    {
        return IDictionary.makeFactory(
            (appender -> Arrays.stream(values)
                .filter(E::shouldGenerate)
                .forEach(e -> appender.accept(e, Objects.requireNonNull(e.defaultEnUs())))),
            ((output, lookup) -> customFactory.create(output, lookup, "en_us", values)));
    }
    
    static <E extends Enum<E> & IDictionary> RegistryDependentFactory<FabricLanguageProvider>
    makeDefaultsFactory(
        final E[] values
    )
    {
        return IDictionary.makeCustomFactoryWithDefaults(values, Provider::new);
    }
    
    //==================================================================================================================
    static TranslatableContents forKey(final String key)
    {
        return new TranslatableContents(key, null, TranslatableContents.NO_ARGS);
    }
    
    //******************************************************************************************************************
    /// {@return the [TranslatableContents] managed by this dictionary entry}
    TranslatableContents translatable();
    
    /// {@return the default translation key for this dictionary entry, or `null` if no default should be generated}
    @Nullable String defaultEnUs();
    
    //==================================================================================================================
    @Override default Style                 getStyle()           { return Style.EMPTY;                                 }
    @Override default ComponentContents     getContents()        { return this.translatable();                         }
    @Override default List<Component>       getSiblings()        { return List.of();                                   }
    @Override default FormattedCharSequence getVisualOrderText() { return Language.getInstance().getVisualOrder(this); }
    
    /// {@return the translation key for this translatable}
    default String getKey() { return this.translatable().getKey(); }
    
    //==================================================================================================================
    /// {@return whether this dictionary entry has a default that can be generated}
    default boolean shouldGenerate() { return (this.defaultEnUs() != null); }
    
    //==================================================================================================================
    /// Gets this translatable with the given arguments.
    /// @param args The translation arguments
    /// @return The new translatable
    default MutableComponent withArgs(final Object ...args) { return Component.translatable(this.getKey(), args); }
}
