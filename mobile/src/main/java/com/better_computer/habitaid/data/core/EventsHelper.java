package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.SearchEntry;

import java.util.ArrayList;
import java.util.List;

public class EventsHelper extends AbstractHelper<Events>{

    public EventsHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_events";
        this.columns.add("sDate VARCHAR(20)");
        this.columns.add("iLongDatetime INTEGER");
        this.columns.add("sName VARCHAR(50)");
        this.columns.add("iTimDur INTEGER");
        this.columns.add("iPtsVal INTEGER");
        this.columns.add("iImp INTEGER");
        this.columns.add("sDtTimStr VARCHAR(20)");
        this.columns.add("sTimEnd VARCHAR(20)");
    }

    @Override
    protected Events getModelInstance() {
        return new Events();
    }

}
