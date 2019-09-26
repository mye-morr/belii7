package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Calendar;
import java.util.Map;

public class Session extends AbstractModel{

    private String sDate = "";
    private String sName = "";
    private int iTimDur = 0;
    private String sTask = "";
    private int iTaskUnst = 0;
    private int iTaskPlan = 0;
    private int iImp = 0;
    private String sImpDets = "";
    private int iPts = 0;
    private String sPtsDets = "";
    private String sDtTimStr = "";
    private String sTimEnd = "";

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("sDate", sDate);
        contentValues.put("sName", sName);
        contentValues.put("iTimDur", iTimDur);
        contentValues.put("sTask", sTask);
        contentValues.put("iTaskUnst", iTaskUnst);
        contentValues.put("iTaskPlan", iTaskPlan);
        contentValues.put("iImp", iImp);
        contentValues.put("sImpDets", sImpDets);
        contentValues.put("iPts", iPts);
        contentValues.put("sPtsDets", sPtsDets);
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
        sTask = fetchData(data, "sTask");
        iTaskUnst = fetchDataInteger(data, "iTaskUnst");
        iTaskPlan = fetchDataInteger(data, "iTaskPlan");
        iImp = fetchDataInteger(data, "iImp");
        sImpDets = fetchData(data, "sImpDets");
        iPts = fetchDataInteger(data, "iPts");
        sPtsDets = fetchData(data, "sPtsDets");
        sDtTimStr = fetchData(data, "sDtTimStr");
        sTimEnd = fetchData(data, "sTimEnd");
    }

    public String getDate() { return sDate; }

    public void setDate(String sDate) { this.sDate = sDate; }

    public String getName() {
        return sName;
    }

    public void setName(String sName) {
        this.sName = sName;
    }

    public int getTimDur() {
        return iTimDur;
    }

    public void setTimDur(int iTimDur) {
        this.iTimDur = iTimDur;
    }

    public String getTask() {
        return sTask;
    }

    public void setTask(String sTask) {
        this.sTask = sTask;
    }

    public int getTaskUnst() {
        return iTaskUnst;
    }

    public void setTaskUnst(int iTaskUnst) {
        this.iTaskUnst = iTaskUnst;
    }

    public int getTaskPlan() {
        return iTaskPlan;
    }

    public void setTaskPlan(int iTaskPlan) {
        this.iTaskPlan = iTaskPlan;
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

    public int getPts() {
        return iPts;
    }

    public void setPts(int iPts) { this.iPts = iPts; }

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

}
