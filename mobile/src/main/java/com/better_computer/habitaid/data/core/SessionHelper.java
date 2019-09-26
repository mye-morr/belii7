package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;

public class SessionHelper extends AbstractHelper<Session>{

    public SessionHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_session";
        this.columns.add("sDate VARCHAR(10)");
        this.columns.add("sName VARCHAR(20)");
        this.columns.add("iTimDur INTEGER");
        this.columns.add("sTask TEXT");
        this.columns.add("iTaskUnst INTEGER");
        this.columns.add("iTaskPlan INTEGER");
        this.columns.add("iImp INTEGER");
        this.columns.add("sImpDets TEXT");
        this.columns.add("iPts INTEGER");
        this.columns.add("sPtsDets TEXT");
        this.columns.add("sDtTimStr VARCHAR(10)");
        this.columns.add("sTimEnd VARCHAR(10)");
    }

    @Override
    protected Session getModelInstance() {
        return new Session();
    }

}