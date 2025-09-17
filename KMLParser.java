package com.fastkml;

import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.symbology.SolidStrokeSymbolLayer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.??.MainController;
import javafx.stage.FileChooser;
import org.checkerframework.checker.units.qual.K;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class KMLParser {
    
    /*
    * KML requires a Document with Folders
    *
    * Folders contain Placemarks created with the KML Builder
    *
    * Placemarks have types and contain Style and 'cooridinates' tags
    *
    * Coordinates are done in 'x,y,z x,y,z' format, they are seperated by a space as shown
    */

   /* GeoJSON parser implementation included, 5 implementations removed due to copyright concerns */

    public static void parseGeoJSON(JsonObject geoJSON){
        //TODO visibility parsing from poperties??
        KMLBuilder kmlBuilder;
        if(geoJSON.has("_id")){
             kmlBuilder= new KMLBuilder(
                    geoJSON.get("_id").getAsJsonObject().get("$id").getAsString()
            );
        } else {
            kmlBuilder = new KMLBuilder();
        }

        String featureType = geoJSON.get("type").getAsString();

        ArrayList<KMLObject> placemarks = new ArrayList<>();

        // A FeatureCollections is most similar to a Folder
        if(featureType.equals("FeatureCollection")){

            JsonArray features = geoJSON.get("features").getAsJsonArray();

            // A Feature is most similar to a Placemark
            features.forEach((element) -> {

                JsonObject feature = element.getAsJsonObject();
                JsonObject geometry = feature.get("geometry").getAsJsonObject();
                String type = geometry.get("type").getAsString();
                
                //KML Geometry conversion
                GeometryType geoType = GeometryType.getFromString(type);

                JsonObject properties = feature.get("properties").getAsJsonObject();

                double upperAlt = properties.get("altitude_upper_wgs84").getAsDouble();
                double lowerAlt = properties.get("altitude_lower_wgs84").getAsDouble();
                double centerAlt = lowerAlt + ((upperAlt - lowerAlt) / 2);

                JsonArray coords = geometry.get("coordinates").getAsJsonArray();

                StringBuilder coordsStringBuilder = new StringBuilder();

                //forEach LineString
                coords.forEach((array) -> {
                    JsonArray pointsArray = array.getAsJsonArray();

                    if(pointsArray.get(0).getAsJsonArray().size() == 2){
                        //forEach 2d Point
                        pointsArray.forEach((pointsArrayElement) -> {
                            JsonArray xyz = pointsArrayElement.getAsJsonArray();
                            if(coordsStringBuilder.length() != 0){
                                coordsStringBuilder.append(' ');
                            }
                            coordsStringBuilder.append(xyz.get(0));
                            coordsStringBuilder.append(',');
                            coordsStringBuilder.append(xyz.get(1));
                            coordsStringBuilder.append(',');
                            coordsStringBuilder.append(centerAlt);
                            coordsStringBuilder.append(' ');
                        });
                    } else if (pointsArray.get(0).getAsJsonArray().size() == 3) {
                        //forEach 3d Point
                        pointsArray.forEach((pointsArrayElement) -> {
                            JsonArray xyz = pointsArrayElement.getAsJsonArray();
                            if (coordsStringBuilder.length() != 0) {
                                coordsStringBuilder.append(' ');
                            }
                            coordsStringBuilder.append(xyz.get(0));
                            coordsStringBuilder.append(',');
                            coordsStringBuilder.append(xyz.get(1));
                            coordsStringBuilder.append(',');
                            coordsStringBuilder.append(xyz.get(2));
                            coordsStringBuilder.append(' ');
                        });
                    }
                });

                //Create and Add Placemarks to a list for Folder Creation
                placemarks.add(
                        kmlBuilder.createPlacemark(geoType, coordsStringBuilder.toString()));
                });
                // add all lines to Folder
                String name = "";
                if(geoJSON.has("id")){
                    name = geoJSON.get("id").getAsString();
                }
                kmlBuilder.createFolder(name, "", placemarks);
            }
        kmlBuilder.exportDocument("GeoJsonTest");
    }
}
