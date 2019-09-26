package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;

public class EventData implements Serializable {
    public String sDate;
    public String sName;
    public String sTimDur;
    public String sPtsVal;
    public String sImp;
    public String sImpDets;
    public String sDtTimStr;
    public String sTimEnd;

    public String getDate() {
        return sDate;
    }

    public void setDate(String sDate) {
        this.sDate = sDate;
    }

    public String getName() {
        return sName;
    }

    public void setName(String sName) {
        this.sName = sName;
    }

    public String getTimDur() {
        return sTimDur;
    }

    public void setTimDur(String sTimDur) {
        this.sTimDur = sTimDur;
    }

    public String getPtsVal() {
        return sPtsVal;
    }

    public void setPtsVal(String sPtsVal) {
        this.sPtsVal = sPtsVal;
    }

    public String getImp() {
        return sImp;
    }

    public void setImp(String sImp) {
        this.sImp = sImp;
    }

    public String getImpDets() {
        return sImpDets;
    }

    public void setImpDets(String sImpDets) {
        this.sImpDets = sImpDets;
    }

    public String getDtTimStr() {
        return sDtTimStr;
    }

    public void setDtTimStr(String sDtTimStr) {
        this.sDtTimStr = sDtTimStr;
    }

    public String getTimEnd() {
        return sTimEnd;
    }

    public void setTimEnd(String sTimEnd) {
        this.sTimEnd = sTimEnd;
    }

    public String toJsonString() {
        try {
            String jsonString = new JSONStringer().object()
                    .key("sDate").value(sDate)
                    .key("sName").value(sName)
                    .key("sTimDur").value(sTimDur)
                    .key("sPtsVal").value(sPtsVal)
                    .key("sImpul").value(sImp)
                    .key("sImpulDets").value(sImpDets)
                    .key("sDtTimStr").value(sDtTimStr)
                    .key("sTimEnd").value(sTimEnd)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static EventData toEventData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            EventData eventData = new EventData();
            eventData.sDate = jsonObject.getString("sDate");
            eventData.sName = jsonObject.getString("sName");
            eventData.sTimDur = jsonObject.getString("sTimDur");
            eventData.sPtsVal = jsonObject.getString("sPtsVal");
            eventData.sImp = jsonObject.getString("sImpul");
            eventData.sImpDets = jsonObject.getString("sImpulDets");
            eventData.sDtTimStr = jsonObject.getString("sDtTimStr");
            eventData.sTimEnd = jsonObject.getString("sTimEnd");

            return eventData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}