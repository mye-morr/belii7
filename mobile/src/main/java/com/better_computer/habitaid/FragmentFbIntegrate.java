package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.ContentLog;
import com.better_computer.habitaid.data.core.ContentLogHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.schedule.ContentLogListAdapter;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.share.WearMessage;
import com.better_computer.habitaid.util.DynaArray;
import com.better_computer.habitaid.util.MarginDecoration;
import com.better_computer.habitaid.util.StopwatchUtil;
import com.better_computer.habitaid.util.SyncData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


grade-mobile

    compile 'com.facebook.android:facebook-android-sdk:[4,5)'


manifest

        <!-- For Facebook SDK -->
        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />

        <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:label="@string/app_name" />

        <provider
            android:name="com.facebook.FacebookContentProvider"
            android:authorities="com.facebook.app.FacebookContentProvider163987540740548"
            android:exported="true" />

        <activity
            android:name="com.facebook.CustomTabActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="@string/fb_login_protocol_scheme" />
            </intent-filter>
        </activity>



ui

    <com.facebook.login.widget.LoginButton
        android:id="@+id/login_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="30dp"
        android:layout_marginBottom="30dp" />

*/

public class FragmentFbIntegrate extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;
    protected ContentLogHelper contentLogHelper;
    protected DatabaseHelper databaseHelper;
    private Handler uiHander;

    private TextView stopwatchView;
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private DynaArray dynaArray;

    public FragmentFbIntegrate() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void refresh() {
        final ListView listViewContentLog = ((ListView) rootView.findViewById(R.id.schedule_content_log));

        List<ContentLog> contentlogs;
        contentlogs = (List<ContentLog>)(List<?>) contentLogHelper.findAll();
        listViewContentLog.setAdapter(new ContentLogListAdapter(context, contentlogs));

        /*
        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        String sql = "SELECT content,wt,wtnew,wtarray,wtarraynew FROM core_tbl_content_log";
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listContentLog = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                listContentLog.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End

        listViewContentLog.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listContentLog));
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fb_integrate, container, false);
        this.rootView = view;

        stopwatchView = (TextView) rootView.findViewById(R.id.stopwatch);

        final Button btnTestPlayer = (Button) rootView.findViewById(R.id.btnTestPlayer);
        final Button btnPretendBoot = (Button) rootView.findViewById(R.id.btnPretendBoot);
        final Button btnNoTime = (Button) rootView.findViewById(R.id.btnNoTime);
        final SeekBar fSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        final SeekBar fSeekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);
        final ListView listViewContentLog = (ListView) rootView.findViewById(R.id.schedule_content_log);

        btnTestPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    inputInterval.setTitle("Draws");
                    inputInterval.setMessage("Number");
                    final EditText input = new EditText(context);
                    input.setText("40");
                    inputInterval.setView(input);

                    inputInterval.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContentLogHelper contentLogHelper = DatabaseHelper.getInstance().getHelper(ContentLogHelper.class);
                            contentLogHelper.delete(new ArrayList<SearchEntry>());    // delete all

                            dynaArray = myApp.getDynaArray();
                            ContentLog contentLog;

                            int iDraws = Integer.parseInt(input.getText().toString());

                            for(int i=0; i<iDraws; i++) {
                                contentLog = dynaArray.getRandomElementNewLog();
                                DatabaseHelper.getInstance().getHelper(ContentLogHelper.class).createOrUpdate(contentLog);
                            }

                            refresh();
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

        });


        btnPretendBoot.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
             context.sendBroadcast(new Intent("mm.belii3.FAKE_BOOT"));
                                             }
        });

        btnNoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WearMessage wearMessage = new WearMessage(context);
                wearMessage.sendData("/no-time", "");
            }
        });

        fSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WearMessage wearMessage = new WearMessage(context);
                double pct = progress / 100.0;
                int iSecs = (int)(pct * 114);
                wearMessage.sendMessage("/set-timer", String.valueOf(iSecs), "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WearMessage wearMessage = new WearMessage(context);
                double pct = progress / 100.0;
                int iSecs = (int)(pct * 500);
                wearMessage.sendMessage("/set-timer", String.valueOf(iSecs), "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rootView.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopwatchUtil.resetStopwatchStartTime(context);
                if (uiHander == null) {
                    uiHander = new Handler();
                }
                uiHander.post(updateStopwatchRunnable);
                stopwatchView.setText("");
            }
        });

        rootView.findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiHander = null;
                if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                    long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                    long passedSeconds = passedTime / 1000;
                    String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                    stopwatchView.setText(strPassedTime);
                }
            }
        });

        rootView.findViewById(R.id.sync_2_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SyncData().downloadLibrary();

                //new Sync2ClientTask().execute();
            }
        });

        rootView.findViewById(R.id.sync_2_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SyncData().uploadLibrary();

                //new Sync2ServerTask().execute();
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.squares);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView.setAdapter(new SquaresAdapter());

        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        this.databaseHelper = DatabaseHelper.getInstance();
        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);
        this.contentLogHelper = DatabaseHelper.getInstance().getHelper(ContentLogHelper.class);

        refresh();

        /*
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        Fragment fragment = ((MainActivity)context).getSupportFragmentManager().findFragmentById(R.id.container);
        loginButton.setFragment(fragment);

        final View postView = rootView.findViewById(R.id.post_button);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl (Uri.parse("http://www.google.com"))
                        .setContentTitle("Hello Facebook")
                        .setContentDescription(
                                "The 'Hello Facebook' sample showcases simple Facebook integration")
                        .build();
                ShareDialog shareDialog = new ShareDialog((MainActivity)context);
                shareDialog.show(linkContent);
            }
        });

        final View postLayoutView = rootView.findViewById(R.id.post_layout);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            // user has logged in
            postLayoutView.setVisibility(View.VISIBLE);
        }

        CallbackManager callbackManager = ((MyApplication)((MainActivity) context).getApplication()).getCallbackManager();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Test", "onSuccess");
                postLayoutView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.d("Test", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Test", "onError");
            }
        });
        */
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        CallbackManager callbackManager = ((com.better_computer.habitaid.MyApplication)getActivity().getApplication()).getCallbackManager();
        callbackManager.onActivityResult(requestCode, resultCode, data);
        */
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
            // is running
            if (uiHander == null) {
                uiHander = new Handler();
            }
            uiHander.post(updateStopwatchRunnable);
        } else {
            updateStopwatchRunnable.run();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHander = null;
    }

    private Runnable updateStopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                /* we don't want to see the elapsed time until end
                long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                long passedSeconds = passedTime / 1000;
                String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                stopwatchView.setText(strPassedTime);
                */
            } finally {
                if (uiHander != null && StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    uiHander.postDelayed(updateStopwatchRunnable, 1000L);
                }
            }
        }
    };

    class SquaresAdapter extends RecyclerView.Adapter<SquaresAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_square, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return 16;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView text1View;
            TextView text2View;

            public ViewHolder(View itemView) {
                super(itemView);
                text1View = (TextView) itemView.findViewById(R.id.text1);
                text2View = (TextView) itemView.findViewById(R.id.text2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence[] items = new CharSequence[] {text1View.getText(), text2View.getText()};
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setItems(items, null);
                        alertDialog.setCancelable(true);
                        alertDialog.show();
                    }
                });
            }

            void setData(int data) {
                text1View.setText("text1-" + data);
                text2View.setText("text2-" + data);
            }
        }

    }

    private Connection openConnection() {
        String ipaddress = "184.168.194.77";
        String db = "narfdaddy2";
        String username = "narfdaddy2";
        String password = "TreeDemo1";

        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ipaddress + ";"

                    + "databaseName=" + db + ";user=" + username
                    + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
            return connection;
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }

        return null;
    }

    private class Sync2ClientTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            nonSchedHelper.delete(new ArrayList<SearchEntry>());    // delete all

            Connection connection = null;
            Statement stmt = null;
            try {
                connection = openConnection();

                String sql = "SELECT * FROM [dbo].[core_tbl_nonsched]";
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    NonSched nonSched = new NonSched();
                    nonSched.set_id(rs.getString("_id"));
                    nonSched.set_frame(rs.getString("_frame"));
                    nonSched.set_state(rs.getString("_state"));
                    nonSched.setCat(rs.getString("cat"));
                    nonSched.setSubcat(rs.getString("subcat"));
                    nonSched.setWtcat(rs.getInt("wtcat"));
                    nonSched.setSubsub(rs.getString("subsub"));
                    nonSched.setIprio(rs.getInt("iprio"));
                    nonSched.setName(rs.getString("name"));
                    nonSched.setAbbrev(rs.getString("abbrev"));
                    nonSched.setContent(rs.getString("content"));
                    nonSched.setNotes(rs.getString("notes"));
                    nonSchedHelper.create(nonSched);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

/*
    public String getRandomElementNew() {
        if (lenInternalArray == 0) {
            // bingo: the re-seed?
            //return "";

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

                addContributingArrayNew(
                        listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
            }
        }

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;
        int numRepeats = 0;
        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            fSum += (double) item.calWeight;
            numRepeats = item.contributingArray.numRepeats;
            if(dRand < fSum
                    && item.repeated < numRepeats) {

                // so calWeight <= 200 (using 100-scale * 2 standard)
                // lets say if its less than 20, aka category < 10
                // then we reserve right to skip over 50% items
                if(item.calWeight <= 20) {
                    if(rand.nextDouble() < 0.5) {
                        // flag as repeated w/o repeating
                        item.repeated++;
                    }
                    else {
                        String sResult = item.content;

                        // apply wtExtinguish
                        item.repeated++;
                        double newWeight =
                                ((numRepeats - item.repeated) * item.calWeight)
                                        / numRepeats;
                        totalWight -= (item.calWeight - newWeight);
                        // assign new cal weight
                        item.calWeight = newWeight;

                        return sResult;
                    }
                }
                else {
                    String sResult = item.content;

                    // apply wtExtinguish
                    item.repeated++;
                    double newWeight =
                            ((numRepeats - item.repeated) * item.calWeight)
                                    / numRepeats;
                    totalWight -= (item.calWeight - newWeight);
                    // assign new cal weight
                    item.calWeight = newWeight;

                    return sResult;
                }
            }
        }

        // may have changed!
        // since some items have been removed
        totalWight = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            item.repeated = 0;
            totalWight += item.calWeight;
        }
        return getRandomElementNew();
    }
 */