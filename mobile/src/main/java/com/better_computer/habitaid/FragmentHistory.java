package com.better_computer.habitaid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Message;
import com.better_computer.habitaid.data.core.MessageHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.data.core.Event;
import com.better_computer.habitaid.data.core.EventHelper;
import com.better_computer.habitaid.form.schedule.MessageListAdapter;
import com.better_computer.habitaid.form.schedule.TransitionListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentHistory extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;

    @Override
    public void refresh() {
    }

    public FragmentHistory() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        final Button btnClear = (Button) rootView.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventHelper eventHelper = DatabaseHelper.getInstance().getHelper(EventHelper.class);
                eventHelper.delete(new ArrayList<SearchEntry>());    // delete all
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        final View dialog = rootView;

        List<Message> messages = (List<Message>)(List<?>) DatabaseHelper.getInstance().getHelper(MessageHelper.class).findAll();
        Collections.reverse(messages);
        ((ListView) dialog.findViewById(R.id.message_list)).setAdapter(new MessageListAdapter(context, messages));

        List<Event> events = (List<Event>)(List<?>) DatabaseHelper.getInstance().getHelper(EventHelper.class).findAll();
        Collections.reverse(events);
        ((ListView) dialog.findViewById(R.id.transition_list)).setAdapter(new TransitionListAdapter(context, events));
    }

}