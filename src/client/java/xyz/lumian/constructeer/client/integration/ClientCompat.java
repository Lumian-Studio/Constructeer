package xyz.lumian.constructeer.client.integration;

import xyz.lumian.constructeer.integration.Compat;



//**********************************************************************************************************************
public class ClientCompat
{
    //******************************************************************************************************************
    static
    {
        Compat.loadIntegrationClass("trinkets", "ToolbeltTrinketRenderer", Object.class, ClientCompat.class);
    }
    
    //******************************************************************************************************************
    public static void initialise() {}
}
