package com.better_computer.habitaid;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.form.schedule.NonSchedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentLibrary extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;

    public FragmentLibrary() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_library, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.databaseHelper = DatabaseHelper.getInstance();
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        final ListView listViewCategory = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewSubcategory = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final RecyclerView listViewLibrary = ((RecyclerView) rootView.findViewById(R.id.schedule_library_list));
        final NonSchedRecyclerViewAdapter libViewAdapter = new NonSchedRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = libViewAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(libViewAdapter);

        listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sCat = listViewCategory.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedLibraryCat = sCat;
                ((MainActivity) context).sSelectedLibrarySubcat = "~NONE";

                String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

                SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
                Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

                List<String> listSubcat = new ArrayList<String>();
                if (cursor2.moveToFirst()) {
                    do {
                        listSubcat.add(cursor2.getString(0));
                    } while (cursor2.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor2.close();
                //fix - android.database.CursorWindowAllocationException End

                ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
                listViewSubcategory.setAdapter(adapterSubcat);

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
                libViewAdapter.setList(listNonSched);

                refresh();
            }
        });

        listViewCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String sCat = listViewCategory.getItemAtPosition(i).toString();

                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Activate");
                optsList.add("Deactivate");
                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (options[i].equalsIgnoreCase("ACTIVATE")) {

                            nonSchedHelper.activate(sCat);

                        } else if (options[i].equalsIgnoreCase("DEACTIVATE")) {

                            nonSchedHelper.deactivate(sCat);

                            //((MainActivity) context).resetup();
                            //dialogInterface.dismiss();
                        }
                        refresh();
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

                return true;
            }
        });

        listViewSubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sSubcat = listViewSubcategory.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedLibrarySubcat = sSubcat;

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, ((MainActivity)context).sSelectedLibraryCat));
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
                libViewAdapter.setList(listNonSched);

                refresh();
            }
        });

        refresh();

        listViewSubcategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String sSubcat = listViewSubcategory.getItemAtPosition(i).toString();
                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Activate");
                optsList.add("Set Weight");
                optsList.add("Add Now");
                optsList.add("Remove Now");
                optsList.add("Deactivate");
                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (options[i].equalsIgnoreCase("ACTIVATE")) {

                            nonSchedHelper.activate(((MainActivity)context).sSelectedLibraryCat,sSubcat);

                        }
                        else if (options[i].equalsIgnoreCase("SET WEIGHT")) {

                            AlertDialog.Builder inputIndex = new AlertDialog.Builder(context);
                            inputIndex.setTitle("Position");
                            inputIndex.setMessage("index");
                            final EditText input = new EditText(context);
                            final SeekBar seek = new SeekBar(context);
                            seek.setMax(99);
                            seek.setProgress(49);
                            inputIndex.setView(seek);

                            inputIndex.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    nonSchedHelper.setwtcat(((MainActivity)context).sSelectedLibraryCat,
                                            sSubcat, Integer.toString(seek.getProgress()));
                                }
                            });
                            inputIndex.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            inputIndex.show();

                            //((MainActivity) context).resetup();
                            //dialogInterface.dismiss();
                        }
                        else if (options[i].equalsIgnoreCase("ADD NOW")) {
                            SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase();
                            final MyApplication myApp = (MyApplication)getActivity().getApplication();

                            String sCat = ((MainActivity)context).sSelectedLibraryCat;

                            int iCount = 0;
                            List<Pair> listWtContent = new ArrayList<Pair>();
                            String s = sCat + ";" + sSubcat + ";2";
                            String sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                                    + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "' AND _state='active'";
                            try {
                                Cursor cursor = database.rawQuery(sql, new String[0]);
                                if (cursor.moveToFirst()) {
                                    do {
                                        listWtContent.add(new Pair(2, cursor.getString(0)));
                                        iCount++;
                                    } while (cursor.moveToNext());
                                }

                                //fix - android.database.CursorWindowAllocationException Start
                                cursor.close();
                                //fix - android.database.CursorWindowAllocationException End
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            myApp.dynaArray.addContributingArrayNew(
                                    listWtContent, s, Double.parseDouble("2") / iCount, 2);

                            //((MainActivity) context).resetup();
                            //dialogInterface.dismiss();
                        }
                        else if (options[i].equalsIgnoreCase("REMOVE NOW")) {
                            SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase();
                            final MyApplication myApp = (MyApplication)getActivity().getApplication();

                            String sCat = ((MainActivity)context).sSelectedLibraryCat;
                            String s = sCat + ";" + sSubcat + ";2";

                            myApp.dynaArray.removeContributingArrayNew(s);
                            nonSchedHelper.deactivate(sCat,sSubcat);
                        }
                        else if (options[i].equalsIgnoreCase("DEACTIVATE")) {

                            nonSchedHelper.deactivate(((MainActivity)context).sSelectedLibraryCat,sSubcat);

                            //((MainActivity) context).resetup();
                            //dialogInterface.dismiss();
                        }
                        refresh();
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

                return true;
            }
        });

   }

    @Override
    public void refresh() {
        String sCat = ((MainActivity) (context)).sSelectedLibraryCat;
        String sSubcat = ((MainActivity) (context)).sSelectedLibrarySubcat;

        final ListView listViewCategory = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewSubcategory = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final RecyclerView listViewLibrary = ((RecyclerView) rootView.findViewById(R.id.schedule_library_list));
        final NonSchedRecyclerViewAdapter libViewAdapter = new NonSchedRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = libViewAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(libViewAdapter);

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT cat FROM core_tbl_nonsched ORDER BY cat";
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listCat = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                listCat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End

        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCat);
        listViewCategory.setAdapter(adapterCat);

        if (sCat.length() > 0) {
            String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

            SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
            Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

            List<String> listSubcat = new ArrayList<String>();
            if (cursor2.moveToFirst()) {
                do {
                    listSubcat.add(cursor2.getString(0));
                } while (cursor2.moveToNext());
            }

            //fix - android.database.CursorWindowAllocationException Start
            cursor2.close();
            //fix - android.database.CursorWindowAllocationException End

            ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
            listViewSubcategory.setAdapter(adapterSubcat);

            ///////////////////////////////////////////
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

            if (!sSubcat.equalsIgnoreCase("~NONE")) {
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));
            }

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }
        else {
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "library"));

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }
    }
}