package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.core.Event;

import java.util.List;

public class TransitionListAdapter extends ArrayAdapter<Event> {

    private int resourceId;
    private List<Event> schedules;
    private Context context;

    public TransitionListAdapter(Context context, List<Event> schedules) {
        super(context, R.layout.list_item_schedule, schedules);
        this.context = context;
        this.schedules = schedules;
        this.resourceId = R.layout.list_item_schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        Event sched = schedules.get(position);
        String sReceiver = "";

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(sched.getDate()
                + "     " + String.valueOf(sched.getImp())
                + "     " + sched.getTimDur()
                );

        ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single_inactive);

        return convertView;
    }

    private View initView(View convertView){
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return vi.inflate(resourceId, null);
        }
        else{
            return convertView;
        }
    }
}