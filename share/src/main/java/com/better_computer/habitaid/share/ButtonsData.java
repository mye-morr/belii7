package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;

public class ButtonsData implements Serializable {

    private String sActiveFace = "";
    private String sCat = "";
    private String sDelimCaptions = "";
    private String sDelimReplies = "";
    private String sDelimPoints = "";

    public String getActiveFace() {
        return sActiveFace;
    }

    public void setActiveFace(String sInput) {
        this.sActiveFace = sInput;
    }

    public String getCat() {
        return sCat;
    }

    public void setCat(String sInput) {
        this.sCat = sInput;
    }

    public String getDelimCaptions() {
        return sDelimCaptions;
    }

    public void setDelimCaptions(String sInput) {
        this.sDelimCaptions = sInput;
    }

    public String getDelimReplies() {
        return sDelimReplies;
    }

    public void setDelimReplies(String sInput) {
        this.sDelimReplies = sInput;
    }

    public String getDelimPoints() {
        return sDelimPoints;
    }

    public void setDelimPoints(String sInput) {
        this.sDelimPoints = sInput;
    }

    public String toJsonString() {
        try {
            String jsonString = new JSONStringer().object()
                    .key("sActiveFace").value(sActiveFace)
                    .key("sCat").value(sCat)
                    .key("sDelimCaptions").value(sDelimCaptions)
                    .key("sDelimReplies").value(sDelimReplies)
                    .key("sDelimPoints").value(sDelimPoints)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ButtonsData toButtonsData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            ButtonsData buttonsData = new ButtonsData();

            buttonsData.setActiveFace(jsonObject.getString("sActiveFace"));
            buttonsData.setCat(jsonObject.getString("sCat"));
            buttonsData.setDelimCaptions(jsonObject.getString("sDelimCaptions"));
            buttonsData.setDelimReplies(jsonObject.getString("sDelimReplies"));
            buttonsData.setDelimPoints(jsonObject.getString("sDelimPoints"));

            return buttonsData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
