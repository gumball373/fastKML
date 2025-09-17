package com.fastkml;

public enum GeometryType {
    Point,
    LineString,
    Polygon;

    public static GeometryType getFromString(String type){
        switch(type){
            case "Point":
                return Point;
            case "Polygon":
            case "MultiPolygon":
                return Polygon;
            case "LineString":
            case "MultiLineString":
                return LineString;
            default:
                return null;
        }
    }
}
