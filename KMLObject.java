package com.fastkml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KMLObject {
    private final ArrayList<KMLObject> containedObjects = new ArrayList<>();

    private final HashMap<String, String> attributes = new HashMap<>();
    String tagName = "";
    String internalValue = "";

    public KMLObject(String tagName){
        this.tagName = tagName;
    }

    /**
     * objects with an internal value should not have objects inside them
     */
    public KMLObject(String tagName, String value){
        this.tagName = tagName;
        this.internalValue = value;
    }

    public String getTag(){
        return tagName;
    }

    /**
     * Adds or updates an XML tag's attribute.
     */
    public void putAttribute(String name, String value){
        attributes.put(name, value);
    }

    public void removeAttribute(String name){
        attributes.remove(name);
    }

    public void addContainedObject(String name, String internalValue){
        containedObjects.add(new KMLObject(name, internalValue));
    }
    public void addContainedObject(KMLObject obj){
        containedObjects.add(obj);
    }
    public void addContainedObject(KMLObject obj, int idx){ containedObjects.add(idx, obj); }
    public void addAllContainedObjects(KMLObject ...objects){
        containedObjects.addAll(Arrays.asList(objects));
    }

    public int getContainedObjectCount(){ return containedObjects.size(); }

    private String buildStartTag(){
        StringBuilder output = new StringBuilder();
        output.append("<");
        output.append(tagName);
        if(!this.attributes.isEmpty()){
            for(String key : attributes.keySet()){
                output.append(" "); //puts a space before each attribute, but not after
                //adds `key="value" `
                output.append(key);
                output.append("=\"");
                output.append(attributes.get(key));
                output.append("\"");
            }
        }
        output.append('>');

        return output.toString();
    }
    private String buildEndTag(){
        StringBuilder output = new StringBuilder();
        output.append("</");
        output.append(tagName);
        output.append('>');

        return output.toString();
    }

    @Override
    public String toString(){
        if(!internalValue.isEmpty()){
            //build obj w internal value
            StringBuilder output = new StringBuilder();
            output.append(buildStartTag());
            output.append(internalValue);
            output.append(buildEndTag());

            return output.toString();
        } else {
            //build obj w other objects inside
            StringBuilder output = new StringBuilder();
            output.append(buildStartTag());
            for(KMLObject obj : containedObjects){
                output.append(obj.toString());
            }
            output.append(buildEndTag());

            return output.toString();
        }
    }
}
