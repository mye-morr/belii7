package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Map;

public class Event extends AbstractModel{

    private String sDate = "";
    private String sName = "";
    private int iTimDur = 0;
    private int iPtsVal = 0;
    private int iImp = 0;
    private String sImpDets = "";
    private String sDtTimStr = "";
    private String sTimEnd = "";

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("sDate", sDate);
        contentValues.put("sName", sName);
        contentValues.put("iTimDur", iTimDur);
        contentValues.put("iPtsVal", iPtsVal);
        contentValues.put("iImp", iImp);
        contentValues.put("sImpDets", sImpDets);
        contentValues.put("sDtTimStr", sDtTimStr);
        contentValues.put("sTimEnd", sTimEnd);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        sDate = fetchData(data, "sDate");
        sName = fetchData(data, "sName");
        iTimDur = fetchDataInteger(data, "iTimDur");
        iPtsVal = fetchDataInteger(data, "iPtsVal");
        iImp = fetchDataInteger(data, "iImp");
        sImpDets = fetchData(data, "sImpDets");
        sDtTimStr = fetchData(data, "sDtTimStr");
        sTimEnd = fetchData(data, "sTimEnd");
    }

    public String getDate() { return sDate; }

    public void setDate(String sDate) { this.sDate = sDate; }

    public String getName() { return sName; }

    public void setName(String sName) { this.sName = sName; }

    public int getTimDur() {
        return iTimDur;
    }

    public void setTimDur(int iTimDur) {
        this.iTimDur = iTimDur;
    }

    public int getPtsVal() {
        return iPtsVal;
    }

    public void setPtsVal(int iPtVal) {
        this.iPtsVal = iPtVal;
    }

    public int getImp() {
        return iImp;
    }

    public void setImp(int iImp) {
        this.iImp = iImp;
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

}
