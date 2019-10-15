package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Map;

public class Events extends AbstractModel{

    private String sDate = "";
    private String sName = "";
    private int iTimDur = 0;
    private int iPtsVal = 0;
    private int iImp = 0;
    private String sDtTimStr = "";
    private String sTimEnd = "";

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("sDate", sDate);
        contentValues.put("sName", sName);
        contentValues.put("iTimDur", iTimDur);
        contentValues.put("iPtsVal", iPtsVal);
        contentValues.put("iImp", iImp);
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
        sDtTimStr = fetchData(data, "sDtTimStr");
        sTimEnd = fetchData(data, "sTimEnd");
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public int getiTimDur() {
        return iTimDur; 
    }

    public void setiTimDur(int iTimDur) {
        this.iTimDur = iTimDur;
    }

    public int getiPtsVal() {
        return iPtsVal; 
    }

    public void setiPtsVal(int iPtsVal) {
        this.iPtsVal = iPtsVal;
    }

    public int getiImp() {
        return iImp; 
    }

    public void setiImp(int iImp) {
        this.iImp = iImp;
    }

    public String getsDtTimStr() {
        return sDtTimStr;
    }

    public void setsDtTimStr(String sDtTimStr) {
        this.sDtTimStr = sDtTimStr;
    }

    public String getsTimEnd() {
        return sTimEnd;
    }

    public void setsTimEnd(String sTimEnd) {
        this.sTimEnd = sTimEnd;
    }


}
