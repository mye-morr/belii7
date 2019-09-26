package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.SearchEntry;

import java.util.ArrayList;
import java.util.List;

public class ContentLogHelper extends AbstractHelper<ContentLog> {

    public ContentLogHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_content_log";
        this.columns.add("playerid TEXT");
        this.columns.add("content TEXT");
        this.columns.add("wt DOUBLE");
        this.columns.add("wtnew DOUBLE");
        this.columns.add("wtarray DOUBLE");
        this.columns.add("wtarraynew DOUBLE");
    }

    @Override
    protected ContentLog getModelInstance() {
        return new ContentLog();
    }

    public boolean deleteByPlayerId(String playerid) {
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "playerid", SearchEntry.Search.EQUAL, playerid));
        return delete(keys);
    }

}