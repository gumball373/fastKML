package com.??;

import com.google.gson.JsonParser;

public class Example{
    //example usage
    public void exportKML(JSONObject geoJSON){
        JsonElement element = JsonParser.parseString(geoJSON.toString());
        JsonObject geoJSON = element.getAsJsonObject();
        KMLParser.parsePsuJSON(geJson);
    }
}
