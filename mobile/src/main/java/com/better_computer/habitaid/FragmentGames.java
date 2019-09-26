package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.schedule.GamesListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.share.ButtonsData;
import com.better_computer.habitaid.share.MessageData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentGames extends AbstractBaseFragment
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = FragmentGames.class.getSimpleName();

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;
    protected GoogleApiClient mGoogleApiClient;
    private NonSched nonSched;
    private boolean bHide;

    //private TextView stopwatchView;

    @Override
    public void refresh() {
        final ListView listViewGames = ((ListView) rootView.findViewById(R.id.schedule_games));
        final ListView listViewLog = ((ListView) rootView.findViewById(R.id.schedule_list));
        final EditText etPtsLos = ((EditText) rootView.findViewById(R.id.pts_los));
        final EditText etPtsWa = ((EditText) rootView.findViewById(R.id.pts_wa));
        final EditText etPtsStru = ((EditText) rootView.findViewById(R.id.pts_stru));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = dateFormat.format(Calendar.getInstance().getTime());

        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "games"));
        List<NonSched> listNsComTas = (List<NonSched>)(List<?>)nonSchedHelper.find(keys, "ORDER BY iprio");
        listViewGames.setAdapter(new NonSchedListAdapter(context, listNsComTas));

        List<Games> games;
        List<SearchEntry> keys2 = new ArrayList<SearchEntry>();
        keys2.add(new SearchEntry(SearchEntry.Type.STRING, "timestamp", SearchEntry.Search.LIKE, sDate + "%"));
        if(bHide) {
            keys2.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.NOT_EQUAL, "00focus"));
            keys2.add(new SearchEntry(SearchEntry.Type.STRING, "content", SearchEntry.Search.NOT_EQUAL, "dun: transition"));
            keys2.add(new SearchEntry(SearchEntry.Type.STRING, "content", SearchEntry.Search.NOT_EQUAL, "dun: engaged"));
            games = (List<Games>) (List<?>) gamesHelper.find(keys2);
        }
        else {
            games = (List<Games>) (List<?>) gamesHelper.find(keys2);
        }
        listViewLog.setAdapter(new GamesListAdapter(context, games));

        /*
        all of this works but alarming to the reptilian brain so.. (hiding)

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        SQLiteStatement s0 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00engaged' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s0 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00task00' AND content='dun: engaged' AND timestamp LIKE '" + sDate + "%'");
        long lSumEnga = s0.simpleQueryForLong();
        SQLiteStatement s1 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00transition' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s1 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00task00' AND content='dun: transition' AND timestamp LIKE '" + sDate + "%'");
        long lSumTrans = s1.simpleQueryForLong();
        //etPtsLos.setText(String.valueOf(lSumEnga) + " / " + String.valueOf(lSumTrans));

        SQLiteStatement s2 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00maint' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s2 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE content='maint' AND timestamp LIKE '" + sDate + "%'");
        long lSumWa = s2.simpleQueryForLong();
        SQLiteStatement s3 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00task' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s3 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00task00' AND content<>'dun: transition' AND content<>'dun: engaged' AND timestamp LIKE '" + sDate + "%'");
        long lSumDun = s3.simpleQueryForLong();
        //etPtsWa.setText(String.valueOf(lSumWa) + " / " + String.valueOf(lSumDun));

        SQLiteStatement s4 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00pos' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s4 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE content<>'maint' AND pts<0 AND timestamp LIKE '" + sDate + "%'");
        long lSumLos = s4.simpleQueryForLong();
        SQLiteStatement s5 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='00neg' AND timestamp LIKE '" + sDate + "%'");
        //SQLiteStatement s5 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat<>'00task00' AND content<>'maint' AND pts>0 AND timestamp LIKE '" + sDate + "%'");
        long lSumStru = s5.simpleQueryForLong();
        //etPtsStru.setText(String.valueOf(lSumLos) + " / " + String.valueOf(lSumStru));
        */
    }

    public FragmentGames() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_games, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_games, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        bHide = true;

        this.databaseHelper = DatabaseHelper.getInstance();
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        this.gamesHelper = DatabaseHelper.getInstance().getHelper(GamesHelper.class);

        final ListView listViewSt = ((ListView) rootView.findViewById(R.id.schedule_list));
        final ListView listViewGames = ((ListView) rootView.findViewById(R.id.schedule_games));
        //stopwatchView = (TextView) rootView.findViewById(R.id.stopwatch);

        final EditText et_name = ((EditText) rootView.findViewById(R.id.et_name));
        final EditText et_text = ((EditText) rootView.findViewById(R.id.et_text));
        final EditText et1_1 = ((EditText) rootView.findViewById(R.id.et1_1));
        final EditText et1_2 = ((EditText) rootView.findViewById(R.id.et1_2));
        final EditText et1_3 = ((EditText) rootView.findViewById(R.id.et1_3));
        final EditText et1_4 = ((EditText) rootView.findViewById(R.id.et1_4));
        final EditText et2_1 = ((EditText) rootView.findViewById(R.id.et2_1));
        final EditText et2_2 = ((EditText) rootView.findViewById(R.id.et2_2));
        final EditText et2_3 = ((EditText) rootView.findViewById(R.id.et2_3));
        final EditText et2_4 = ((EditText) rootView.findViewById(R.id.et2_4));
        final EditText et3_1 = ((EditText) rootView.findViewById(R.id.et3_1));
        final EditText et3_2 = ((EditText) rootView.findViewById(R.id.et3_2));
        final EditText et3_3 = ((EditText) rootView.findViewById(R.id.et3_3));
        final EditText et3_4 = ((EditText) rootView.findViewById(R.id.et3_4));

        listViewGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nonSched = (NonSched)listViewGames.getItemAtPosition(i);
                et_name.setText(nonSched.getName());

                String sContent = nonSched.getContent();
                if(sContent.contains(";")) {

                    String[] sxLabels = new String[12];
                    String[] sxLabelsSrc = nonSched.getContent().split(";");

                    for (int j = 0; j < 12; j++) {
                        sxLabels[j] = "";
                    }

                    for (int j = 0; j < sxLabelsSrc.length; j++) {
                        sxLabels[j] = sxLabelsSrc[j];
                    }

                    et_text.setText("");
                    et1_1.setText(sxLabels[0]);
                    et1_2.setText(sxLabels[1]);
                    et1_3.setText(sxLabels[2]);
                    et1_4.setText(sxLabels[3]);
                    et2_1.setText(sxLabels[4]);
                    et2_2.setText(sxLabels[5]);
                    et2_3.setText(sxLabels[6]);
                    et2_4.setText(sxLabels[7]);
                    et3_1.setText(sxLabels[8]);
                    et3_2.setText(sxLabels[9]);
                    et3_3.setText(sxLabels[10]);
                    et3_4.setText(sxLabels[11]);
                }
                else {
                    et_text.setText(sContent);
                    et1_1.setText("");
                    et1_2.setText("");
                    et1_3.setText("");
                    et1_4.setText("");
                    et2_1.setText("");
                    et2_2.setText("");
                    et2_3.setText("");
                    et2_4.setText("");
                    et3_1.setText("");
                    et3_2.setText("");
                    et3_3.setText("");
                    et3_4.setText("");
                }
            }
        });

        listViewSt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Games st = (Games) listViewSt.getItemAtPosition(i);
                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Delete");

                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (options[i].equalsIgnoreCase("DELETE")) {
                            Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                            gamesHelper.delete(st.get_id());
                            refresh();
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

        final Button bShow = ((Button) rootView.findViewById(R.id.show));
        final Button bSet = ((Button) rootView.findViewById(R.id.set));
        final Button bNew = ((Button) rootView.findViewById(R.id.ne));
        final Button bSav = ((Button) rootView.findViewById(R.id.sav));
        final Button bDel = ((Button) rootView.findViewById(R.id.del));

        bNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nonSched = null;

                et_name.setText("name");
                et_text.setText("");
                et1_1.setText("");
                et1_2.setText("");
                et1_3.setText("");
                et1_4.setText("");
                et2_1.setText("");
                et2_2.setText("");
                et2_3.setText("");
                et2_4.setText("");
                et3_1.setText("");
                et3_2.setText("");
                et3_3.setText("");
                et3_4.setText("");
                refresh();
            }
        });

        bSav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(nonSched == null) {
                nonSched = new NonSched();
            }

            nonSched.setCat("games");

            // get category and name on Sav
            nonSched.setName(et_name.getText().toString());

            String sText = et_text.getText().toString();
            if(sText.length() > 0) {
                nonSched.setSubcat("text");
                nonSched.setContent(sText);
            }
            else {
                nonSched.setSubcat("buttons");
                nonSched.setContent(
                        et1_1.getText().toString()
                + ";" + et1_2.getText().toString()
                + ";" + et1_3.getText().toString()
                + ";" + et1_4.getText().toString()
                + ";" + et2_1.getText().toString()
                + ";" + et2_2.getText().toString()
                + ";" + et2_3.getText().toString()
                + ";" + et2_4.getText().toString()
                + ";" + et3_1.getText().toString()
                + ";" + et3_2.getText().toString()
                + ";" + et3_3.getText().toString()
                + ";" + et3_4.getText().toString() + ";"
                    );
            }
                // returns boolean
            if (DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nonSched)) {
                Toast.makeText(context, "Game saved.", Toast.LENGTH_SHORT).show();
                refresh();
            }
            else {
                Toast.makeText(context, "Game saving failed.", Toast.LENGTH_SHORT).show();
            }
            }
        });

        bDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nonSched != null) {
                    Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                    DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).delete(nonSched.get_id());
                }
            }
        });

        bSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder inputIndex = new AlertDialog.Builder(context);
                inputIndex.setTitle("Position");
                inputIndex.setMessage("index");
                final EditText input = new EditText(context);
                final SeekBar seek = new SeekBar(context);
                seek.setMax(4);
                seek.setProgress(2);
                inputIndex.setView(seek);

                // this fixes the split bug
                // where it auto-truncates for empty elements
                // works bc et3_4 is the last element
                if(et3_4.getText().length() < 1) {
                    et3_4.setText(" ");
                }

                inputIndex.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    if(et_text.length() > 0) {
                        MessageData messageData = new MessageData();
                        messageData.setText1("sText" + Integer.toString(seek.getProgress()));
                        messageData.setText2(et_text.getText().toString());
                        final String messageString = messageData.toJsonString();

                        if (!mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        }

                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                        for (final Node node : getConnectedNodesResult.getNodes()) {
                                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                                                    "/set-pref", messageString.getBytes()).setResultCallback(
                                                    getSendMessageResultCallback());
                                        }
                                    }
                                });
                    }
                    else {
                        // process array
                        String sBuf =
                                et1_1.getText().toString()
                        + ";" + et1_2.getText().toString()
                        + ";" + et1_3.getText().toString()
                        + ";" + et1_4.getText().toString()
                        + ";" + et2_1.getText().toString()
                        + ";" + et2_2.getText().toString()
                        + ";" + et2_3.getText().toString()
                        + ";" + et2_4.getText().toString()
                        + ";" + et3_1.getText().toString()
                        + ";" + et3_2.getText().toString()
                        + ";" + et3_3.getText().toString()
                        + ";" + et3_4.getText().toString() + ";";

                        String[] sxProcess = sBuf.split(";");
                        String sCaptions = "";
                        String sReplies = "";
                        String sPoints = "";

                        int iBuf;
                        int iBuf2;
                        int iBuf3;
                        for (int i = 0; i < 12; i++) {
                            sBuf = sxProcess[i];
                            if(sBuf.contains("|")) {
                                iBuf3 = sBuf.indexOf("|");
                                sReplies += ";" + sBuf.substring(iBuf3 + 1).trim();
                                sBuf = sBuf.substring(0,iBuf3).trim();
                            }
                            else {
                                sReplies += ";";
                            }
                            if (!sBuf.contains("(")) {
                                sCaptions += ";" + sBuf;
                                sPoints += ";0";
                            } else {
                                iBuf = sBuf.indexOf("(");
                                iBuf2 = sBuf.indexOf(")");
                                sCaptions += ";" + sBuf.substring(0, iBuf).trim();
                                sPoints += ";" + sBuf.substring(iBuf + 1, iBuf2).trim();
                            }
                        }

                        sCaptions = sCaptions.substring(1, sCaptions.length()) + ";";
                        sPoints = sPoints.substring(1, sPoints.length()) + ";";
                        sReplies = sReplies.substring(1, sReplies.length()) + ";";

                        if(sReplies.substring(sReplies.length()-2,sReplies.length()).equalsIgnoreCase(";;"))
                        {
                            sReplies = sReplies.substring(0,sReplies.length()-2) + "; ;";
                        }

                        ButtonsData buttonsData = new ButtonsData();
                        buttonsData.setActiveFace(Integer.toString(seek.getProgress()));
                        buttonsData.setCat(et_name.getText().toString());
                        buttonsData.setDelimCaptions(sCaptions);
                        buttonsData.setDelimReplies(sReplies);
                        buttonsData.setDelimPoints(sPoints);

                        final String messageString = buttonsData.toJsonString();

                        if (!mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        }

                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                        for (final Node node : getConnectedNodesResult.getNodes()) {
                                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                                                    "/set-buttons", messageString.getBytes()).setResultCallback(
                                                    getSendMessageResultCallback());
                                        }
                                    }
                                });
                    }
                    }
                });
                inputIndex.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                inputIndex.show();

            }
        });

        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bHide) {
                    bHide = false;
                    bShow.setText("Hide");
                    refresh();
                }
                else {
                    bHide = true;
                    bShow.setText("Show");
                    refresh();
                }
            }
        });

        /*
        bStSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if already started, set new stop time and display
                if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                    long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                    long passedSeconds = passedTime / 1000;
                    String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                    stopwatchView.setText(strPassedTime);
                }
                else {
                    // if not yet started, reset the start time
                    StopwatchUtil.resetStopwatchStartTime(context);
                    stopwatchView.setText("");
                }
            }
        });
        */

        et1_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_1.setSelection(0);
                }
            }
        });
        et1_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_2.setSelection(0);
                }
            }
        });
        et1_3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_3.setSelection(0);
                }
            }
        });
        et1_4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_4.setSelection(0);
                }
            }
        });
        et2_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_1.setSelection(0);
                }
            }
        });
        et2_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_2.setSelection(0);
                }
            }
        });
        et2_3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_3.setSelection(0);
                }
            }
        });
        et2_4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_4.setSelection(0);
                }
            }
        });
        et3_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_1.setSelection(0);
                }
            }
        });
        et3_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_2.setSelection(0);
                }
            }
        });
        et3_3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_3.setSelection(0);
                }
            }
        });
        et3_4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et1_4.setSelection(0);
                }
            }
        });

        refresh();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected : " + bundle);
//        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended : " + i);
//        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Failed to connect to Google Api Client with error code "
                + connectionResult.getErrorCode());
    }

    private ResultCallback<MessageApi.SendMessageResult> getSendMessageResultCallback() {
        return new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e(LOG_TAG, "Failed to connect to Google Api Client with status "
                            + sendMessageResult.getStatus());
                } else {
                    Log.d(LOG_TAG, "Successful to connect to Google Api Client with status "
                            + sendMessageResult.getStatus());
                }
            }
        };
    }
}