package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;

public class PressedData implements Serializable {

    private String sCat = "";
    private String sCaption = "";
    private String sPoints = "0";

    public String getCat() {
        return sCat;
    }

    public void setCat(String sInput) {
        this.sCat = sInput;
    }

    public String getCaption() {
        return sCaption;
    }

    public void setCaption(String sInput) {
        this.sCaption = sInput;
    }

    public String getPoints() {
        String sReturn = "0";
        if (sPoints.length() > 0) {
            if (!sPoints.equalsIgnoreCase(" ")) {
                sReturn = sPoints;
            }
        }

        return sReturn;
    }

    public void setPoints(String sInput) {
        this.sPoints = sInput;
    }

    public String toJsonString() {
        try {
            String jsonString = new JSONStringer().object()
                    .key("sCat").value(sCat)
                    .key("sCaption").value(sCaption)
                    .key("sPoints").value(sPoints)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static PressedData toPressedData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            PressedData pressedData = new PressedData();

            pressedData.setCat(jsonObject.getString("sCat"));
            pressedData.setCaption(jsonObject.getString("sCaption"));
            pressedData.setPoints(jsonObject.getString("sPoints"));

            return pressedData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
