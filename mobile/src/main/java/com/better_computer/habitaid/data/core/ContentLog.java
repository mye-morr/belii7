package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Map;

public class ContentLog extends AbstractModel {

    private String playerid = "";
    private String content = "";
    private double wt = 0;
    private double wtnew = 0;
    private double wtarray = 0;
    private double wtarraynew = 0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("playerid", playerid);
        contentValues.put("content", content);
        contentValues.put("wt", wt);
        contentValues.put("wtnew", wtnew);
        contentValues.put("wtarray", wtarray);
        contentValues.put("wtarraynew", wtarraynew);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        playerid = fetchData(data, "playerid");
        content = fetchData(data, "content");
        wt = fetchDataDouble(data, "wt");
        wtnew = fetchDataDouble(data, "wtnew");
        wtarray = fetchDataDouble(data, "wtarray");
        wtarraynew = fetchDataDouble(data, "wtarraynew");
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getWt() { return wt; }

    public void setWt(double wt) { this.wt = wt; }

    public double getWtNew() { return wtnew; }

    public void setWtNew(double wtnew) { this.wtnew = wtnew; }

    public double getWtArray() { return wtarray; }

    public void setWtArray(double wtarray) { this.wtarray = wtarray; }

    public double getWtArrayNew() { return wtarraynew; }

    public void setWtArrayNew(double wtarraynew) { this.wtarraynew = wtarraynew; }

}