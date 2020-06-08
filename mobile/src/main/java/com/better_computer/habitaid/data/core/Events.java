package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Map;

public class Events extends AbstractModel{

    private String sDate = "";
    private int iLongDatetime = 0;
    private String sName = "";
    private int iTimDur = 0;
    private int iPtsVal = 0;
    private int iImp = 0;
    private String sDtTimStr = "";
    private String sTimEnd = "";

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("sDate", sDate);
        contentValues.put("iLongDatetime", iLongDatetime);
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
        iLongDatetime = fetchDataInteger(data, "iLongDatetime");
        sName = fetchData(data, "sName");
        iTimDur = fetchDataInteger(data, "iTimDur");
        iPtsVal = fetchDataInteger(data, "iPtsVal");
        iImp = fetchDataInteger(data, "iImp");
        sDtTimStr = fetchData(data, "sDtTimStr");
        sTimEnd = fetchData(data, "sTimEnd");
    }

    public String getSDate() {
        return sDate;
    }

    public void setSDate(String sDate) {
        this.sDate = sDate;
    }

    public int getILongDatetime() {
        return iLongDatetime; 
    }

    public void setILongDatetime(int iLongDatetime) {
        this.iLongDatetime = iLongDatetime;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public int getITimDur() {
        return iTimDur; 
    }

    public void setITimDur(int iTimDur) {
        this.iTimDur = iTimDur;
    }

    public int getIPtsVal() {
        return iPtsVal; 
    }

    public void setIPtsVal(int iPtsVal) {
        this.iPtsVal = iPtsVal;
    }

    public int getIImp() {
        return iImp; 
    }

    public void setIImp(int iImp) {
        this.iImp = iImp;
    }

    public String getSDtTimStr() {
        return sDtTimStr;
    }

    public void setSDtTimStr(String sDtTimStr) {
        this.sDtTimStr = sDtTimStr;
    }

    public String getSTimEnd() {
        return sTimEnd;
    }

    public void setSTimEnd(String sTimEnd) {
        this.sTimEnd = sTimEnd;
    }


}
