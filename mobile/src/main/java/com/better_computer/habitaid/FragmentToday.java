package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.util.BaseItemTouchHelperCallback;
import com.better_computer.habitaid.util.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentToday extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;
    protected String sCat = "0tread";

    RecyclerView listViewPrj;
    TodayRecyclerViewAdapter listViewPrjAdapter;
    RecyclerView listViewSmTas;
    TodayRecyclerViewAdapter listViewSmTasAdapter;
    RecyclerView listViewLibrary;
    TodayRecyclerViewAdapter listViewLibraryAdapter;

    public FragmentToday() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_today, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_today, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.databaseHelper = DatabaseHelper.getInstance();
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        final View dialog = rootView;

        final ListView listViewCat = ((ListView) dialog.findViewById(R.id.schedule_cat));
        List<String> listCat = new ArrayList<String>();
        listCat.add("0tread");
        listCat.add("0brthr");
        listCat.add("0motiva");
        listCat.add("0prj");
        listCat.add("0smtas");
        ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCat);
        listViewCat.setAdapter(adapterSubcat);

        listViewCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sCat = listViewCat.getItemAtPosition(i).toString();
                ((MainActivity) context).sSelectedLibraryCat = sCat;

                List<SearchEntry> keys3 = new ArrayList<SearchEntry>();
                keys3.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));
                List<NonSched> listNsTread = (List<NonSched>)(List<?>)nonSchedHelper.find(keys3, "ORDER BY iprio");
                listViewLibraryAdapter.setList(listNsTread);
            }
        });

        listViewPrj = ((RecyclerView) dialog.findViewById(R.id.schedule_prj));
        listViewPrjAdapter = new TodayRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelperPrj = listViewPrjAdapter.getItemTouchHelper();
        itemTouchHelperPrj.attachToRecyclerView(listViewPrj);
        listViewPrj.setAdapter(listViewPrjAdapter);

        listViewSmTas = ((RecyclerView) dialog.findViewById(R.id.schedule_smtas));
        listViewSmTasAdapter = new TodayRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelperSmTas = listViewSmTasAdapter.getItemTouchHelper();
        itemTouchHelperSmTas.attachToRecyclerView(listViewSmTas);
        listViewSmTas.setAdapter(listViewSmTasAdapter);

        listViewLibrary = ((RecyclerView) dialog.findViewById(R.id.schedule_library_list));
        listViewLibraryAdapter = new TodayRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = listViewLibraryAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(listViewLibraryAdapter);

        refresh();
   }

    @Override
    public void refresh() {
        List<SearchEntry> keys1 = new ArrayList<SearchEntry>();
        keys1.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0prj"));
        List<NonSched> listNsPrj = (List<NonSched>)(List<?>)nonSchedHelper.find(keys1, "ORDER BY iprio");
        listViewPrjAdapter.setList(listNsPrj);

        List<SearchEntry> keys2 = new ArrayList<SearchEntry>();
        keys2.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0smtas"));
        List<NonSched> listNsSmTas = (List<NonSched>)(List<?>)nonSchedHelper.find(keys2, "ORDER BY iprio");
        listViewSmTasAdapter.setList(listNsSmTas);

        List<SearchEntry> keys3 = new ArrayList<SearchEntry>();
        keys3.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));
        List<NonSched> listNsTread = (List<NonSched>)(List<?>)nonSchedHelper.find(keys3, "ORDER BY iprio");
        listViewLibraryAdapter.setList(listNsTread);
    }

    class TodayRecyclerViewAdapter extends RecyclerView.Adapter<TodayRecyclerViewAdapter.ViewHolder>
            implements ItemTouchHelperAdapter {

        private Context context;
        private ItemTouchHelper itemTouchHelper;
        private List<NonSched> nonSchedList;
        private NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        public TodayRecyclerViewAdapter(Context context) {
            this.context = context;
            ItemTouchHelper.Callback callback = new BaseItemTouchHelperCallback(this);
            this.itemTouchHelper = new ItemTouchHelper(callback);
        }

        public void setList(List<NonSched> nonSchedList) {
            this.nonSchedList = nonSchedList;
            this.notifyDataSetChanged();
        }

        public ItemTouchHelper getItemTouchHelper() {
            return this.itemTouchHelper;
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(nonSchedList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            new SaveOrderTask().execute();
            return true;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_non_schedule_simple, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final NonSched nonSched = nonSchedList.get(position);
            holder.setData(nonSched);

            holder.handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return nonSchedList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private NonSched nsItem;
            private TextView itemSummaryView;
            private ImageView itemIconView;
            private final ImageView handleView;

            private ViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(0x00000000);
                handleView = (ImageView) itemView.findViewById(R.id.handle);
                itemSummaryView = (TextView) itemView.findViewById(R.id.schedule_item_summary);
                itemIconView = (ImageView) itemView.findViewById(R.id.schedule_item_icon);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                        List<String> optsList = new ArrayList<String>();

                        optsList.add("Move To");
                        optsList.add("Delete");

                        final String[] options = optsList.toArray(new String[]{});
                        alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (options[i].equalsIgnoreCase("MOVE TO")) {

                                    AlertDialog.Builder inputInterval = new AlertDialog.Builder(context);
                                    inputInterval.setTitle("Interval");
                                    inputInterval.setMessage("Minutes; varia");
                                    final EditText input = new EditText(context);
                                    inputInterval.setView(input);

                                    inputInterval.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    inputInterval.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    inputInterval.show();
                                }
                                else if (options[i].equalsIgnoreCase("DELETE")) {
                                    Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                                    DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).delete(nsItem.get_id());

                                    ((MainActivity) context).resetup();
                                    dialogInterface.dismiss();
                                }
                            }
                        });

                        alertOptions.setCancelable(true);
                        alertOptions.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertOptions.show();
                    }
                });
            }

            private void setData(NonSched item) {
                this.nsItem = item;

                String sLabel = item.getName();
                itemSummaryView.setText(sLabel);
            }
        }

        class SaveOrderTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NonSched[] array = nonSchedList.toArray(new NonSched[0]);
                for (int i = 0 ; i < array.length ; i++) {
                    NonSched nonSched = array[i];
                    int iprio = i;
                    if (iprio != nonSched.getIprio()) {
                        nonSched.setIprio(iprio);
                        nonSchedHelper.update(nonSched);
                    }
                }
                return null;
            }
        }
    }
}