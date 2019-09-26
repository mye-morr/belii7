/*

package com.better_computer.habitaid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyGridPagerAdapter extends FragmentGridPagerAdapter {

    private List<Row> masterList;
    private Context context;

    public MyGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        masterList = new ArrayList<Row>();

        Fragment fragText0 = new FragmentWearText();
        Fragment fragButtons0 = new FragmentWearButtons();
        Bundle b0a = new Bundle();
        Bundle b0b = new Bundle();
        b0a.putInt("iActiveFace", 0);
        b0b.putInt("iActiveFace", 0);
        fragText0.setArguments(b0a);
        fragButtons0.setArguments(b0b);

        Fragment fragText1 = new FragmentWearText();
        Fragment fragButtons1 = new FragmentWearButtons();
        Bundle b1a = new Bundle();
        Bundle b1b = new Bundle();
        b1a.putInt("iActiveFace", 1);
        b1b.putInt("iActiveFace", 1);
        fragText1.setArguments(b1a);
        fragButtons1.setArguments(b1b);

        Fragment fragText2 = new FragmentWearText();
        Fragment fragButtons2 = new FragmentWearButtons();
        Bundle b2a = new Bundle();
        Bundle b2b = new Bundle();
        b2a.putInt("iActiveFace", 2);
        b2b.putInt("iActiveFace", 2);
        fragText2.setArguments(b2a);
        fragButtons2.setArguments(b2b);

        Fragment fragText3 = new FragmentWearText();
        Fragment fragButtons3 = new FragmentWearButtons();
        Bundle b3a = new Bundle();
        Bundle b3b = new Bundle();
        b3a.putInt("iActiveFace", 3);
        b3b.putInt("iActiveFace", 3);
        fragText3.setArguments(b3a);
        fragButtons3.setArguments(b3b);

        Fragment fragText4 = new FragmentWearText();
        Fragment fragButtons4 = new FragmentWearButtons();
        Bundle b4a = new Bundle();
        Bundle b4b = new Bundle();
        b4a.putInt("iActiveFace", 4);
        b4b.putInt("iActiveFace", 4);
        fragText4.setArguments(b4a);
        fragButtons4.setArguments(b4b);

        masterList.add(
                new Row(fragText0, fragText1, fragText2, fragText3, fragText4));
        masterList.add(
                new Row(fragButtons0, fragButtons1, fragButtons2, fragButtons3, fragButtons4));
    }

    private Fragment cardFragment(String title, String text) {
        CardFragment cardFragment = CardFragment.create(title,text);
        return cardFragment;
    }

    @Override
    public int getColumnCount(int rowNumber) {
        return masterList.get(rowNumber).getColumnCount();
    }

    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        return 2;
    }

    @Override
    public int getRowCount() {
        return masterList.size();
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row rowObject = masterList.get(row);
        return rowObject.getColumn(col);
    }


    @Override
    public Point getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private class Row {
        final List<Fragment> listOfColumns =
                new ArrayList<Fragment>();

        public Row(Fragment... fragments) {
            for (Fragment f : fragments ) {
                add(f);
            }
        }

        public void add(Fragment fragment) {
            listOfColumns.add(fragment);
        }

        Fragment getColumn(int i) {
            return listOfColumns.get(i);
        }

        public int getColumnCount() {
            return listOfColumns.size();
        }
    }
}

*/