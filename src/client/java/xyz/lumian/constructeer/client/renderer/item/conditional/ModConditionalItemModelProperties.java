package xyz.lumian.constructeer.client.renderer.item.conditional;

import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import xyz.lumian.constructeer.ModDefine;



//**********************************************************************************************************************
public final class ModConditionalItemModelProperties
{
    //******************************************************************************************************************
    public static void initialise()
    {
        ConditionalItemModelProperties.ID_MAPPER.put(ModDefine.id("pouch/has_content"), PouchHasContent.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(ModDefine.id("menu_complies"),     MenuComplies   .MAP_CODEC);
    }
    
    //******************************************************************************************************************
    private ModConditionalItemModelProperties() {}
}
