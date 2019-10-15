package com.better_computer.habitaid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.MessageHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.service.PlayerServiceStatic;
import com.better_computer.habitaid.share.LibraryData;
import com.better_computer.habitaid.share.SerializedArray;
import com.better_computer.habitaid.share.WearMessage;
import com.better_computer.habitaid.util.SyncData;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractBaseFragment extends Fragment {

    private static final int SETTING_RESULT = 1;

    public abstract void refresh();

    protected Context context;
    protected View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schedule_new_events:
                new NewWizardDialog(context, "events").show();
                return true;
            case R.id.action_schedule_new_contacts:
                new NewWizardDialog(context, "contacts").show();
                return true;
            case R.id.action_clear_history:
                clearHistory();
                return true;
            case R.id.action_upload_games:
                new SyncData().uploadEvent();
                return true;
            case R.id.action_refresh:
                ((MainActivity)context).resetup();
                return true;
            case R.id.action_library_new:
                new NewWizardDialog(context, "library").show();
                return true;
            case R.id.action_player_strst:
                runPlayer();
                return true;
            case R.id.action_drill_strst:
                runDrill();
                return true;
            case R.id.action_library_sync:
                syncLibrary();
                return true;
            case R.id.action_prjsmtas_sync:
                syncPrjSmTas();
                return true;
            case R.id.action_schedule_new_ontrack:
                new NewWizardDialog(context, "ontrack").show();
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(context, SettingsActivity.class), SETTING_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SETTING_RESULT) {
                // do something here
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clearHistory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear sent message history");
        builder.setMessage("Are you sure that you want to delete the sent message history?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MessageHelper messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);
                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                List<String> states = new ArrayList<String>();
                states.add("sending");
                states.add("delivered");
                states.add("failed");
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "_state", SearchEntry.Search.IN, states));
                messageHelper.delete(keys);
                ((MainActivity) context).resetup();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void syncPrjSmTas() {
        int idx;

        NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0prj"));
        List<NonSched> listNsPrj = (List<NonSched>)(List<?>)nonSchedHelper.find(keys, "ORDER BY iprio");

        String[] sxPrj = new String[listNsPrj.size()];
        idx= 0;
        for (NonSched prj: listNsPrj) {
            sxPrj[idx] = prj.getName();
            idx++;
        }

        List<SearchEntry> keys2 = new ArrayList<SearchEntry>();
        keys2.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0smtas"));
        List<NonSched> listNsSmTas = (List<NonSched>)(List<?>)nonSchedHelper.find(keys2, "ORDER BY iprio");

        String[] sxSmTas = new String[listNsSmTas.size()];
        idx= 0;
        for (NonSched smTas: listNsSmTas) {
            sxSmTas[idx] = smTas.getName();
            idx++;
        }

        SerializedArray saPrj = new SerializedArray(sxPrj);
        SerializedArray saSmTas = new SerializedArray(sxSmTas);

        WearMessage wearMsg = new WearMessage(context);
        wearMsg.sendMessage("/set-prjsmtas", saPrj.getSerialString(";"), saSmTas.getSerialString(";"));
    }

    public void syncSingleLibrary(String sCat) {

        String[] sxElementsReplies;
        String sElements, sReplies;

        NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        sxElementsReplies = getCatElements(nonSchedHelper, sCat);
        sElements = sxElementsReplies[0];
        sReplies = sxElementsReplies[1];

        LibraryData libraryData = new LibraryData();
        libraryData.setDelimCat(sCat);
        libraryData.setDelimElements(sElements);
        libraryData.setDelimReplies(sReplies);

        WearMessage wearMessage = new WearMessage(context);
        wearMessage.sendLibrary("/set-single-library", libraryData);
    }

    public void syncLibrary() {

        int idx;

        String sql = "SELECT DISTINCT cat FROM core_tbl_nonsched WHERE cat LIKE '0%'";

        SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listCat = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                if(!cursor.getString(0).startsWith("00")) {
                    listCat.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }

        String[] sxCat = new String[listCat.size()];
        String[] sxElements = new String[listCat.size()];
        String[] sxPoints = new String[listCat.size()];
        String[] sxReplies = new String[listCat.size()];

        NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        idx=0;
        String[] sxElementsReplies;
        for (String sCat : sxCat) {
            sCat = listCat.get(idx);
            sxCat[idx] = sCat;

            sxElementsReplies = getCatElements(nonSchedHelper, sCat);
            sxElements[idx] = sxElementsReplies[0];
            sxPoints[idx] = sxElementsReplies[1];
            sxReplies[idx] = sxElementsReplies[2];
            idx++;
        }

        SerializedArray saCat = new SerializedArray(sxCat);
        SerializedArray saElements = new SerializedArray(sxElements);
        SerializedArray saPoints = new SerializedArray(sxPoints);
        SerializedArray saReplies = new SerializedArray(sxReplies);

        LibraryData libraryData = new LibraryData();
        libraryData.setDelimCat(saCat.getSerialString("|"));
        libraryData.setDelimElements(saElements.getSerialString("|"));
        libraryData.setDelimPoints(saPoints.getSerialString("|"));
        libraryData.setDelimReplies(saReplies.getSerialString("|"));

        WearMessage wearMessage = new WearMessage(context);
        wearMessage.sendLibrary("/set-library", libraryData);
    }

    public String[] getCatElements(NonSchedHelper nonSchedHelper, String sCat) {
        String[] sxRet = new String[3];

        int idx2;

        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));
        List<NonSched> listNs = (List<NonSched>)(List<?>)nonSchedHelper.find(keys, "ORDER BY iprio");

        String[] sxElements = new String[listNs.size()];
        String[] sxReplies = new String[listNs.size()];
        String[] sxPoints = new String[listNs.size()];

        idx2= 0;
        for (NonSched nonSched: listNs) {
            String sName = nonSched.getName();
            sxElements[idx2] = sName;

            sxPoints[idx2] = "0";

            int iBuf, iBuf2;
            if (sName.contains("(")) {
                iBuf = sName.indexOf("(");
                iBuf2 = sName.indexOf(")");
                sxPoints[idx2] = sName.substring(iBuf + 1, iBuf2).trim();
            }

            sxReplies[idx2] = nonSched.getContent();
            idx2++;
        }

        SerializedArray saBuf = new SerializedArray(sxElements);
        SerializedArray saBuf2 = new SerializedArray(sxPoints);
        SerializedArray saBuf3 = new SerializedArray(sxReplies);

        sxRet[0] = saBuf.getSerialString(";");
        sxRet[1] = saBuf2.getSerialString(";");
        sxRet[2] = saBuf3.getSerialString(";");

        return sxRet;
    }

    public void syncLibraryOld() {
        int idx;
        NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        List<SearchEntry> keys3 = new ArrayList<SearchEntry>();
        keys3.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0tread"));
        List<NonSched> listNsTread = (List<NonSched>)(List<?>)nonSchedHelper.find(keys3, "ORDER BY iprio");

        String[] sxTread = new String[listNsTread.size()];
        String[] sxTreadReplies = new String[listNsTread.size()];
        idx= 0;
        for (NonSched tread: listNsTread) {
            sxTread[idx] = tread.getName();
            sxTreadReplies[idx] = tread.getContent();
            idx++;
        }

        List<SearchEntry> keys4 = new ArrayList<SearchEntry>();
        keys4.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0brthr"));
        List<NonSched> listNsBrThr = (List<NonSched>)(List<?>)nonSchedHelper.find(keys4, "ORDER BY iprio");

        String[] sxBrThr = new String[listNsBrThr.size()];
        idx= 0;
        for (NonSched brThr: listNsBrThr) {
            sxBrThr[idx] = brThr.getName();
            idx++;
        }

        List<SearchEntry> keys5 = new ArrayList<SearchEntry>();
        keys5.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "0motiva"));
        List<NonSched> listNsMotiva = (List<NonSched>)(List<?>)nonSchedHelper.find(keys5, "ORDER BY iprio");

        String[] sxMotiva = new String[listNsMotiva.size()];
        idx= 0;
        for (NonSched motiva: listNsMotiva) {
            sxMotiva[idx] = motiva.getName();
            idx++;
        }

        SerializedArray saTread = new SerializedArray(sxTread);
        SerializedArray saTreadReplies = new SerializedArray(sxTreadReplies);
        SerializedArray saBrThr = new SerializedArray(sxBrThr);
        SerializedArray saMotiva = new SerializedArray(sxMotiva);

        LibraryData libraryData = new LibraryData();

        /*
        libraryData.setDelimTread(saTread.getSerialString(";"));
        libraryData.setDelimTreadReplies(saTreadReplies.getSerialString(";"));
        libraryData.setDelimBrThr(saBrThr.getSerialString(";"));
        libraryData.setDelimMotiva(saMotiva.getSerialString(";"));
        */

        WearMessage wearMsg = new WearMessage(context);
        wearMsg.sendLibrary("/set-library", libraryData);
    }

    public void runDrill() {
        final MyApplication myApp = (MyApplication)getActivity().getApplication();

        if (myApp.bDrillOn) {
            Toast.makeText(context, "thanks for drilling", Toast.LENGTH_SHORT).show();
            myApp.bDrillOn = false;
            PlayerServiceStatic.stopService(context);

            WearMessage wearMsg = new WearMessage(context);
            wearMsg.sendSignal("/start-timer");
        }
        else {

            String sCat = ((MainActivity) context).sSelectedLibraryCat;
            String sSubcat = ((MainActivity) context).sSelectedLibrarySubcat;

            SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();
            String sql;
            if(sSubcat.equalsIgnoreCase("~NONE")) {
                sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                        + "WHERE cat='" + sCat + "'";
            }
            else {
                sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                        + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "'";
            }

            List<String> listItems = new ArrayList<String>();
            try {
                Cursor cursor = database.rawQuery(sql, new String[0]);

                if (cursor.moveToFirst()) {
                    do {
                        listItems.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor.close();
                //fix - android.database.CursorWindowAllocationException End
            } catch (Exception e) {
                e.printStackTrace();
            }

            myApp.bDrillOn = true;
            PlayerServiceStatic.startService(context, listItems.toArray(new String[]{}), 30);
        }
    }

    public void runPlayer() {
        final MyApplication myApp = (MyApplication)getActivity().getApplication();

        if (myApp.bPlayerOn) {
            Toast.makeText(context, "thanks for playing", Toast.LENGTH_SHORT).show();
            myApp.bPlayerOn = false;
            PlayerService.stopService(context);

            WearMessage wearMsg = new WearMessage(context);
            wearMsg.sendSignal("/start-timer");

        } else {

            SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

            String sql = "SELECT DISTINCT (cat || ';' || subcat || ';' || wtcat) as foo FROM core_tbl_nonsched WHERE _state='active' ORDER BY cat,subcat";

            List<String> listCatSubcat = new ArrayList<String>();
            try {
                Cursor cursor = database.rawQuery(sql, new String[0]);
                if (cursor.moveToFirst()) {
                    do {
                        listCatSubcat.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor.close();
                //fix - android.database.CursorWindowAllocationException End
            } catch (Exception e) {
                e.printStackTrace();
            }

            myApp.dynaArray.init();
            int iCount = 0;

            for (String s : listCatSubcat) {
                String sCat = "";
                String sSubcat = "";
                String sWtcat = "";

                String[] sxTokens = s.split(";");
                sCat = sxTokens[0];
                sSubcat = sxTokens[1];
                sWtcat = sxTokens[2];

                iCount = 0;
                List<Pair> listWtContent = new ArrayList<Pair>();

                sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
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
                        listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
            }

            AlertDialog.Builder inputInterval = new AlertDialog.Builder(context);
            inputInterval.setTitle("Interval");
            inputInterval.setMessage("Minutes; varia");
            final EditText input = new EditText(context);
            input.setText("2;1");
            inputInterval.setView(input);

            inputInterval.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myApp.bPlayerOn = true;
                    PlayerService.startService(context, input.getText().toString());
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
    }
}