package xyz.lumian.constructeer.gauntlet;


import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.data.IDictionary;



//**********************************************************************************************************************
public enum CteerGauntletDictionary
    implements IDictionary
{
    
    ;
    //******************************************************************************************************************
    private final TranslatableContents translatable;
    
    @Nullable
    private final String enUsDefault;
    
    //******************************************************************************************************************
    CteerGauntletDictionary(final String key, final @Nullable String englishDefault)
    {
        this.translatable = IDictionary.forKey(CteerDefine.formatId(key));
        this.enUsDefault  = englishDefault;
    }
    
    CteerGauntletDictionary(final String key) { this(key, null); }
    
    //==================================================================================================================
    @Override public TranslatableContents translatable() { return this.translatable; }
    @Override public @Nullable String     defaultEnUs()  { return this.enUsDefault; }
}
