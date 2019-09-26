package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SessionData implements Serializable {
    public String sDate;
    public String sName;
    public String sTimDur;
    public String sTimEng;
    public String sTask;
    public String sImp;
    public String sImpDets;
    public String sPts;
    public String sPtsDets;
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

    public String getTimEng() {
        return sTimEng;
    }

    public void setTimEng(String sTimEng) {
        this.sTimEng = sTimEng;
    }

    public String getTask() {
        return sTask;
    }

    public void setTask(String sTask) {
        this.sTask = sTask;
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

    public String getPts() {
        return sPts;
    }

    public void setPts(String sPts) {
        this.sPts = sPts;
    }

    public String getPtsDets() {
        return sPtsDets;
    }

    public void setPtsDets(String sPtsDets) {
        this.sPtsDets = sPtsDets;
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
                    .key("sTimEng").value(sTimEng)
                    .key("sTask").value(sTask)
                    .key("sImpul").value(sImp)
                    .key("sImpulDets").value(sImpDets)
                    .key("sPts").value(sPts)
                    .key("sPtsDets").value(sPtsDets)
                    .key("sDtTimStr").value(sDtTimStr)
                    .key("sTimEnd").value(sTimEnd)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static SessionData toSessionData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            SessionData sessionData = new SessionData();
            sessionData.sDate = jsonObject.getString("sDate");
            sessionData.sName = jsonObject.getString("sName");
            sessionData.sTimDur = jsonObject.getString("sTimDur");
            sessionData.sTimEng = jsonObject.getString("sTimEng");
            sessionData.sTask = jsonObject.getString("sTask");
            sessionData.sImp = jsonObject.getString("sImpul");
            sessionData.sImpDets = jsonObject.getString("sImpulDets");
            sessionData.sPts = jsonObject.getString("sPts");
            sessionData.sPtsDets = jsonObject.getString("sPtsDets");
            sessionData.sDtTimStr = jsonObject.getString("sDtTimStr");
            sessionData.sTimEnd = jsonObject.getString("sTimEnd");

            return sessionData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}