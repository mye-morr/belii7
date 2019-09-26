package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;

public class LibraryData implements Serializable {

    private String sDelimCat;
    private String sDelimElements;
    private String sDelimPoints;
    private String sDelimReplies;

    public String getDelimCat() {
        return sDelimCat;
    }

    public void setDelimCat(String sInput) {
        this.sDelimCat = sInput;
    }

    public String getDelimElements() {
        return sDelimElements;
    }

    public void setDelimElements(String sInput) {
        this.sDelimElements = sInput;
    }

    public String getDelimPoints() {
        return sDelimPoints;
    }

    public void setDelimPoints(String sInput) {
        this.sDelimPoints = sInput;
    }

    public String getDelimReplies() {
        return sDelimReplies;
    }

    public void setDelimReplies(String sInput) {
        this.sDelimReplies = sInput;
    }

    public String toJsonString() {
        try {
            String jsonString = new JSONStringer().object()
                    .key("sDelimCat").value(sDelimCat)
                    .key("sDelimElements").value(sDelimElements)
                    .key("sDelimPoints").value(sDelimPoints)
                    .key("sDelimReplies").value(sDelimReplies)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static LibraryData toLibraryData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            LibraryData libraryData = new LibraryData();
            libraryData.setDelimCat(jsonObject.getString("sDelimCat"));
            libraryData.setDelimElements(jsonObject.getString("sDelimElements"));
            libraryData.setDelimPoints(jsonObject.getString("sDelimPoints"));
            libraryData.setDelimReplies(jsonObject.getString("sDelimReplies"));

            return libraryData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}