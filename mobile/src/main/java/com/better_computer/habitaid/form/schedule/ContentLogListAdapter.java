package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.ContentLog;
import com.better_computer.habitaid.data.core.ContentLogHelper;

import java.util.List;

public class ContentLogListAdapter extends ArrayAdapter<ContentLog> {

    private int resourceId;
    private List<ContentLog> contentlogs;
    private Context context;
    protected ContentLogHelper contentLogHelper;

    public ContentLogListAdapter(Context context, List<ContentLog> contentlogs) {
        super(context, R.layout.list_item_schedule, contentlogs);
        this.context = context;
        this.contentlogs = contentlogs;
        this.resourceId = R.layout.list_item_schedule;
        this.contentLogHelper = DatabaseHelper.getInstance().getHelper(ContentLogHelper.class);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        final ContentLog contentlog = contentlogs.get(position);

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(

/*
                iLenContent = item.content.length();
        retContentLog.setContent(sContent
                + item.content.substring(0,
                iLenContent < 14 ? iLenContent : 14));
*/
                StringUtils.substring(contentlog.getPlayerid(),0,4)
                        + "    " + StringUtils.substring(contentlog.getContent(),0,14)
                        + "    " + round(contentlog.getWt(),1)
                        + "    " + round(contentlog.getWtNew(),1)
                        + "    " + round(contentlog.getWtArray(),1)
                        + "    " + round(contentlog.getWtArrayNew(),1)
        );

        if(contentlog.get_state().equalsIgnoreCase("active")) {
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single);
        }
        else{
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single_inactive);
        }

        convertView.setBackgroundColor(0x00000000);
        return convertView;
    }

    private View initView(View convertView){
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return vi.inflate(resourceId, null);
        }else{
            return convertView;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}