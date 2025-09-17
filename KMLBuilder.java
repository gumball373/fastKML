package com.fastkml;

import com.??.MainController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

/**Simple lightweight KML builder. Meant to be quick and simple for a small library, hence the limited functionality, and potential iprovements.
 * <p>
 * Objects are meant to be created upfront, and left alone, but are not immutable.
 * <p>
 */
public class KMLBuilder {
    private final KMLObject document = new KMLObject("Document");

    public KMLBuilder(){

    }

    public KMLBuilder(String docName){
        document.putAttribute("id", docName);
    }

    /**
     *  Creates a folder object and places all placemarks into it, then adds to the document.
     *  Folders are meant to contain a single flight or single collection of elements
     *  <p>
     *  Folders are meant to be created here and not be cahnged after for development ease.
     */
    public void createFolder(String name, String description, Collection<KMLObject> placemarks){
        KMLObject folder = new KMLObject("Folder");
        folder.addContainedObject("name", name);
        folder.addContainedObject("description", description);
        // below only uses array type so nothing is copied/created, hence length 0. This improvement was made in java 6
        folder.addAllContainedObjects(placemarks.toArray(new KMLObject[0]));

        document.addContainedObject(folder);
    }

    /**
     * Can Be used to create a line or shape with a default red line.
     * <p>
     * Expecting Hex Code Colors as a string, without a '#'(hashtag).
     */
    public KMLObject createPlacemark(GeometryType geoType, String geoCoordinates){
        return createPlacemark(geoType, geoCoordinates, true, "FF0000FF", "00000000");
    }

    /**
     * Can Be used to create a line or shape with a default red line.
     * <p>
     * Expecting Hex Code Colors as a string, without a '#'(hashtag).
     */
    public KMLObject createPlacemark(GeometryType geoType, String geoCoordinates, boolean visible){
        return createPlacemark(geoType, geoCoordinates, visible, "FF0000FF", "00000000");
    }


    /// Can Be used to create a line or shape depending on the line and polygon colors.
    /// <p>
    /// Expecting Hex Code Colors as a string, without a '#'(hashtag).

    /**
     * Can Be used to create a line or shape depending on the line and polygon colors.
     * <p>
     * Expecting Hex Code Colors as a string, without a '#'(hashtag).
     */
    public KMLObject createPlacemark(GeometryType geoType, String geoCoordinates, boolean visible, String hexLineColor, String hexPolygonFill){
        KMLObject placemark = new KMLObject("Placemark");

        //build style object
        KMLObject style = new KMLObject("Style");

        KMLObject lineStyle = new KMLObject("LineStyle");
        lineStyle.addContainedObject("color", hexLineColor);

        KMLObject polyStyle = new KMLObject("PolyStyle");
        polyStyle.addContainedObject("fill", hexPolygonFill);

        style.addAllContainedObjects(lineStyle, polyStyle);

        //build geometry
        KMLObject geometry = new KMLObject(geoType.name());
        KMLObject coords = new KMLObject("coordinates", geoCoordinates);
        if(geoType.name().equals("Polygon")){
            KMLObject outerBoundaryIs = new KMLObject("outerBoundaryIs");
            KMLObject linearRing = new KMLObject("LinearRing");

            outerBoundaryIs.addContainedObject(linearRing);
            linearRing.addContainedObject(coords);

            geometry.addContainedObject(outerBoundaryIs);
        } else {
            geometry.addContainedObject(coords);
        }

        placemark.addAllContainedObjects(style, geometry);

        return placemark;
    }

    public String buildDocument(){
        StringBuilder docBuilder = new StringBuilder();
        docBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
        docBuilder.append(document);
        docBuilder.append("</kml>");
        return docBuilder.toString();
    }

    /** Exports the KML document to the 'Documents/fastKML' folder using a filestream.
     * */
    public void exportDocument(String fileName) {
        String filepath = System.getProperty("user.home")+"/Documents/fastKML";
        File dir = new File(filepath);
        if(!dir.exists()) {
            // do something
            try {
                Files.createDirectories(Paths.get(filepath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            File actualFile = new File(dir, fileName + ".kml");

            BufferedWriter f_writer
                    = new BufferedWriter(new FileWriter(actualFile));

            f_writer.write(buildDocument());

            f_writer.close();

            MainController.getInstance().alertPopUp("File Export Successful", "KML file save to "+ filepath,false);
        }
        // Catch block to handle if exceptions occurs
        catch (IOException e) {

            // Print the exception on console
            // using getMessage() method
            System.out.print(e.getMessage());
        }
    }
}
