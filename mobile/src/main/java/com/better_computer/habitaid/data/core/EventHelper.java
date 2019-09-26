package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;

public class EventHelper extends AbstractHelper<Event>{

    public EventHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_event";
        this.columns.add("sDate VARCHAR(10)");
        this.columns.add("sName VARCHAR(20)");
        this.columns.add("iTimDur INTEGER");
        this.columns.add("iPtsVal INTEGER");
        this.columns.add("iImp INTEGER");
        this.columns.add("sImpDets TEXT");
        this.columns.add("sDtTimStr VARCHAR(10)");
        this.columns.add("sTimEnd VARCHAR(10)");
    }

    @Override
    protected Event getModelInstance() {
        return new Event();
    }

}