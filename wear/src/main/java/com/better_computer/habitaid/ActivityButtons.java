package com.better_computer.habitaid;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.share.PressedData;
import com.better_computer.habitaid.share.WearMessage;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.text.TextUtils.substring;
import static java.lang.Integer.parseInt;

public class ActivityButtons extends WearableActivity
{
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;
    private GoogleApiClient mGoogleApiClient = null;
    private SharedPreferences prefs;

    Chronometer mChronometer;

    private int mActiveFace;

    private Button btn1_1;
    private Button btn1_2;
    private Button btn1_3;
    private Button btn1_4;
    private Button btn2_1;
    private Button btn2_2;
    private Button btn2_3;
    private Button btn2_4;
    private Button btn3_1;
    private Button btn3_2;
    private Button btn3_3;
    private Button btn3_4;

    private TextView tvTextLeft;
    private TextView tvTextRight;
    private Button btnCycl;
    private Button btnTrans;
    private Button btnWork;
    private Button btnDoneCanc;

    private String sCat;
    private String sDelimCaptions;
    private String[] sxCaptions;
    private String sDelimReplies;
    private String[] sxReplies;
    private String sDelimPoints;
    private String[] sxPoints;
    private String sActiveFace;

    private String sNewCycl;
    private boolean bNewTrans;
    private boolean bNewWork;
    private boolean bNewTask;
    private boolean bImpuls;
    private boolean bTimerTicking;
    private String sCurEvent;
    private String sType;
    private long lCurSeshNum;

    private PressedData pressedData;
    private Database db;
    public static Context contextOfApplication;
    private boolean bDebug;

    private static final String LOG_TAG = ActivityButtons.class.getSimpleName();

    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, ActivityButtons.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buttons);
        setAmbientEnabled();

        contextOfApplication = getApplicationContext();
        final MyApplication myApp = (MyApplication)getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        lCurSeshNum = myApp.getSeshCur();

        pressedData = new PressedData();
        db = new Database(getApplicationContext());

        if(myApp.bFirstLaunch) {
            Calendar cNow = Calendar.getInstance();
            StopwatchUtil.resetTransStartTime(contextOfApplication);
            StopwatchUtil.setDateTransStarted(
                    dateFormat.format(cNow.getTime()),
                    dateTimeFormat.format(cNow.getTime()));
            db.clearImp();

            myApp.bFirstLaunch = false;
        }

        bDebug = myApp.bDebug;

        sNewCycl = myApp.sNewCycl;
        bNewTrans = myApp.bNewTrans;
        bNewWork = myApp.bNewWork;
        bNewTask = myApp.bNewTask;

        bImpuls = myApp.bImpuls;
        bTimerTicking = myApp.bTimerTicking;
        sCurEvent = myApp.sCurEvent;

        mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
        mDismissOverlayView = new DismissOverlayView(ActivityButtons.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Configure a gesture detector
        mGestureDetector = new GestureDetector(ActivityButtons.this, new GestureDetector.SimpleOnGestureListener() {

            /*
            @Override
            public void onLongPress(MotionEvent event) {
                ((MyApplication)getApplication()).offTimer();
                mDismissOverlayView.show();
            }
            */

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {

                Intent intent;
                switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                    case 1:
                        //Toast.makeText(getApplicationContext(), "prj / smtas", Toast.LENGTH_SHORT).show();

                        intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("sType", "dash/wk");

                        startActivity(intent);

                        //Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:

                        ActivityText.startActivity(getApplicationContext());

                        //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                        return true;
                    case 3:
                        // Toast.makeText(getApplicationContext(), "timDecr / ptsPos", Toast.LENGTH_SHORT).show();

                        intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("sType", "dash/day");

                        startActivity(intent);

                        //Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT).show();
                        return true;
                    case 4:

                        //Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                        return true;
                        }
                    return false;
                }

            private int getSlope(float x1, float y1, float x2, float y2) {
                Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
                if (angle > 45 && angle <= 135)
                    // top
                    return 1;
                if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                    // left
                    return 2;
                if (angle < -45 && angle>= -135)
                    // down
                    return 3;
                if (angle > -45 && angle <= 45)
                    // right
                    return 4;
                return 0;
            }
        });

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        btn1_1 = (Button) findViewById(R.id.btn1_1);
        btn1_2 = (Button) findViewById(R.id.btn1_2);
        btn1_3 = (Button) findViewById(R.id.btn1_3);
        btn1_4 = (Button) findViewById(R.id.btn1_4);
        btn2_1 = (Button) findViewById(R.id.btn2_1);
        btn2_2 = (Button) findViewById(R.id.btn2_2);
        btn2_3 = (Button) findViewById(R.id.btn2_3);
        btn2_4 = (Button) findViewById(R.id.btn2_4);
        btn3_1 = (Button) findViewById(R.id.btn3_1);
        btn3_2 = (Button) findViewById(R.id.btn3_2);
        btn3_3 = (Button) findViewById(R.id.btn3_3);
        btn3_4 = (Button) findViewById(R.id.btn3_4);

        tvTextLeft = (TextView) findViewById(R.id.tvTextLeft);
        tvTextRight = (TextView) findViewById(R.id.tvTextRight);
        btnCycl = (Button) findViewById(R.id.btnCycl);
        btnTrans = (Button) findViewById(R.id.btnTrans);
        btnWork = (Button) findViewById(R.id.btnWork);
        btnDoneCanc = (Button) findViewById(R.id.btnDoneCanc);

        if(bNewTrans || bNewWork || bNewTask) {
            if(myApp.bJustPicked) {

                Calendar cNow = Calendar.getInstance();

                long passedTime = StopwatchUtil.getTransPassedTime(contextOfApplication);
                long passedSecs = passedTime / 1000;
                int iMinPassed = (int) Math.round(passedSecs / 60.0);

                if(bDebug) {
                    Toast.makeText(getApplicationContext(),
                            "trans:decd -> effic",
                            Toast.LENGTH_LONG).show();
                }

                // trans start time doesn't reset
                // after optn, so whether you got that far or not
                // up to decided will be logged when start task
                db.doneEffic(
                        lCurSeshNum
                        ,dateFormat.format(cNow.getTime())
                        ,"trans"
                        , iMinPassed
                        , "decd"
                        , 0);

                StopwatchUtil.resetEventStartTime(contextOfApplication);
                StopwatchUtil.setDateEventStarted(
                        dateFormat.format(cNow.getTime()),
                        dateTimeFormat.format(cNow.getTime()));

                StopwatchUtil.resetEngagedStartTime(contextOfApplication, "maint");

                bImpuls = false;
                myApp.bImpuls = false;

                myApp.bJustPicked = false;
            }

            btnCycl.setText("");
            btnTrans.setText("");
            btnWork.setText("");
            btnDoneCanc.setText("dn cn");
        }
        else {
            btnCycl.setText(sNewCycl);
            btnTrans.setText("trans");
            btnWork.setText("work");
            btnDoneCanc.setText("task");
        }

        recaption();

        btnCycl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Calendar cNow = Calendar.getInstance();

                String sDateToday = StopwatchUtil.getDateTodayStarted(contextOfApplication);
                if(!sDateToday.equalsIgnoreCase(dateFormat.format(cNow.getTime()))) {
                    StopwatchUtil.setDateTodayStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));
                }

                if(bDebug) {
                    Toast.makeText(getApplicationContext(),
                            "trans:rstrt -> effic",
                            Toast.LENGTH_LONG).show();
                }

                long passedTime = StopwatchUtil.getTransPassedTime(contextOfApplication);
                long passedSecs = passedTime / 1000;
                int iMinPassed = (int) Math.round(passedSecs / 60.0);
                db.doneEffic(
                        lCurSeshNum
                        ,dateFormat.format(cNow.getTime())
                        ,"trans"
                        , iMinPassed
                        , "rstrt"
                        , 0);

                StopwatchUtil.resetTransStartTime(contextOfApplication);
                StopwatchUtil.setDateTransStarted(
                        dateFormat.format(cNow.getTime()),
                        dateTimeFormat.format(cNow.getTime()));

                btnCycl.setText("offt");
                myApp.sNewCycl = "offt";
                lCurSeshNum = lCurSeshNum + 1;
                myApp.setSeshCur(lCurSeshNum + 1);

                return true;
            }
        });

        btnCycl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aka "done"

                MyApplication myApp = ((MyApplication)getApplication());
                if(bNewWork || bNewTask) {
                    // don't process phantom clicks
                }
                else {
                    sNewCycl = myApp.sNewCycl;

                    Calendar cNow = Calendar.getInstance();
                    long passedTime;
                    long passedSecs;
                    int iMinPassed;

                    switch(sNewCycl) {
                        case "offt":
                            sNewCycl = "plng";
                            myApp.sNewCycl = sNewCycl;
                            btnCycl.setText(sNewCycl);

                            passedTime = StopwatchUtil.getTransPassedTime(contextOfApplication);
                            passedSecs = passedTime / 1000;
                            iMinPassed = (int) Math.round(passedSecs / 60.0);

                            if(bDebug) {
                                Toast.makeText(getApplicationContext(),
                                        "l0st:offt -> effic\n" +
                                                "trans:offt -> event\n" +
                                                "start: timer",
                                        Toast.LENGTH_LONG).show();
                            }

                            db.doneEffic(
                                    lCurSeshNum
                                    ,dateFormat.format(cNow.getTime())
                                    ,"l0st"
                                    ,iMinPassed
                                    ,"offt"
                                    , 0);

                            db.doneEvent(
                                    StopwatchUtil.getDateTransStarted(contextOfApplication),
                                    "trans: offt",
                                    iMinPassed,
                                    0,
                                    StopwatchUtil.getDateTimeTransStarted(contextOfApplication),
                                    timeFormat.format(cNow.getTime()));

                            StopwatchUtil.resetTransStartTime(contextOfApplication);
                            StopwatchUtil.setDateTransStarted(
                                    dateFormat.format(cNow.getTime()),
                                    dateTimeFormat.format(cNow.getTime()));

                            //initiate the buzzer
                            StopwatchUtil.resetEngagedStartTime(contextOfApplication, "start");

                            if(!myApp.isOnTimer()) {
                                myApp.toggleTimer();
                            }

                            break;
                        case "plng":
                            sNewCycl = "decd";
                            myApp.sNewCycl = sNewCycl;
                            btnCycl.setText(sNewCycl);

                            passedTime = StopwatchUtil.getTransPassedTime(contextOfApplication);
                            passedSecs = passedTime / 1000;
                            iMinPassed = (int) Math.round(passedSecs / 60.0);

                            if(bDebug) {
                                Toast.makeText(getApplicationContext(),
                                        "trans:optn -> effic\n" +
                                                "trans:optn -> event\n" +
                                                "no-reset: trans",
                                        Toast.LENGTH_LONG).show();
                            }

                            db.doneEffic(
                                    lCurSeshNum
                                    ,dateFormat.format(cNow.getTime())
                                    ,"trans"
                                    ,iMinPassed
                                    ,"optn"
                                    , 0);

                            db.doneEvent(
                                    StopwatchUtil.getDateTransStarted(contextOfApplication),
                                    "trans: optn",
                                    iMinPassed,
                                    0,
                                    StopwatchUtil.getDateTimeTransStarted(contextOfApplication),
                                    timeFormat.format(cNow.getTime()));

                            sCurEvent = "decd";
                            myApp.sCurEvent = "decd";
                            break;
                    }
                }
            }
        });

        btnTrans.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApp = ((MyApplication)getApplication());

                if(bNewTrans || bNewWork || bNewTask) {
                    // don't process phantom clicks
                }
                else {
                    startEvent(true, false, false);
                }
            }
        });

        btnWork.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aka "done"
                MyApplication myApp = ((MyApplication)getApplication());

                if(bNewTrans || bNewWork || bNewTask) {
                    // don't process phantom clicks
                }
                else {
                    startEvent(false, true, false);
                }
            }
        });

        btnDoneCanc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MyApplication myApp = ((MyApplication) getApplication());

                if(bNewTrans || bNewWork || bNewTask) {
                    // aka "cancel"

                    tvTextLeft.setText("");
                    tvTextRight.setText("");

                    btnCycl.setText("offt");
                    btnTrans.setText("trans");
                    btnWork.setText("work");
                    btnDoneCanc.setText("task");

                    sNewCycl = "offt";
                    myApp.sNewCycl = "offt";
                    bNewTrans = false;
                    myApp.bNewTrans = false;
                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;

                    bImpuls = false;
                    myApp.bImpuls = false;
                    sCurEvent = "";
                    myApp.sCurEvent = "";

                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());

                    db.clearImp();
                    recaption();
                }

                return true;
            }
        });

        btnDoneCanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApp = ((MyApplication)getApplication());

                String sEventPrefix = "";
                if(bNewTrans || bNewWork || bNewTask) {
                    if (bNewTrans) {
                        sEventPrefix = "trans";
                    }
                    else if (bNewWork) {
                        sEventPrefix = "work";
                    }
                    else if (bNewTask) {
                        sEventPrefix = "task";
                    }

                    sActiveFace = "1";
                    sDelimCaptions = prefs.getString("sDelimCaptions" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");
                    sDelimPoints = prefs.getString("sDelimPoints" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");
                    sDelimReplies = prefs.getString("sDelimReplies" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");

                    sxCaptions = sDelimCaptions.split(";");
                    sxPoints = sDelimPoints.split(";");
                    sxReplies = sDelimReplies.split(";");

                    // spaces make it work :-\
                    if (sxReplies[11].equalsIgnoreCase(" ")) {
                        sxReplies[11] = "";
                    }

                    sCurEvent = myApp.sCurEvent;
                    sType = myApp.sCurType;

                    prefs.edit().putString("sActiveFace","1").apply();
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Calendar cNow = Calendar.getInstance();

                    StopwatchUtil.resetTransStartTime(contextOfApplication);
                    StopwatchUtil.setDateTransStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    if(bDebug) {
                        Toast.makeText(getApplicationContext(),
                                "reset: trans\n" +
                                        sEventPrefix + ": " + myApp.sCurEvent + " -> event\n" +
                                        "sActiveFace: 1\n" +
                                        "reset: engaged",
                                Toast.LENGTH_LONG).show();
                    }

                    long passedTime = StopwatchUtil.getEventPassedTime(contextOfApplication);
                    long passedSecs = passedTime / 1000;
                    int iMinPassed = (int) Math.round(passedSecs / 60.0);
                    db.doneEvent(
                            StopwatchUtil.getDateEventStarted(contextOfApplication),
                            sEventPrefix + ": " + myApp.sCurEvent,
                            iMinPassed,
                            0,
                            StopwatchUtil.getDateTimeEventStarted(contextOfApplication),
                            timeFormat.format(cNow.getTime()));

                    //!!! NEED TO LOG REST OF MAINT!!
                    String sGamesLastStatus = StopwatchUtil.getEngagedLastStatus(contextOfApplication);

                    if (sGamesLastStatus.equalsIgnoreCase("maint")) {
                        StopwatchUtil.setStopwatchLastTouchedTime(contextOfApplication, System.currentTimeMillis());

                        passedTime = StopwatchUtil.getEngagedPassedTime(contextOfApplication);
                        passedSecs = passedTime / 1000;
                        int iPassedMin = (int) Math.round(passedSecs / 60.0);

                        db.doneEffic(
                                lCurSeshNum
                                , dateFormat.format(cNow.getTime())
                                , "engaged"
                                , iPassedMin
                                , sCurEvent
                                , bTimerTicking ? 1 : 0);

                        StopwatchUtil.resetEngagedStartTime(contextOfApplication, "maint");
                    }

                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());

                    sNewCycl = "offt";
                    myApp.sNewCycl = "offt";
                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;
                    bImpuls = false;
                    myApp.bImpuls = false;

                    btnCycl.setText("offt");
                    btnTrans.setText("trans");
                    btnWork.setText("work");
                    btnDoneCanc.setText("task");

                    if(bNewTrans) {

                        // sType is like numIncr, timDecr etc.
                        Intent intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("sTrans", sCurEvent);
                        intent.putExtra("iMin", iMinPassed);
                        intent.putExtra("sType", sType);

                        bNewTrans = false;
                        myApp.bNewTrans = false;

                        sCurEvent = "";
                        myApp.sCurEvent = "";
                        sType = "";
                        // application-level sCurType
                        // will be set by ActivityTwoLists
                        //myApp.sCurType = "";
                        startActivity(intent);
                    }
                    else {
                        bNewTrans = false;
                        myApp.bNewTrans = false;

                        sCurEvent = "";
                        myApp.sCurEvent = "";
                        sType = "";
                        myApp.sCurType = "";
                        ActivityButtons.startActivity(getApplicationContext());
                    }

                    bTimerTicking = false;
                    myApp.bTimerTicking = false;
                    StopwatchUtil.resetEngagedStartTime(contextOfApplication, "done");
                }
                else {
                    startEvent(false,false,true);
                }
            }
        });

        btn1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[0],false);
                sendButton(sCat, sxCaptions[0], sxPoints[0], sxReplies[0], false);
            }
        });
        btn1_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[0],true);
                sendButton(sCat, sxCaptions[0], sxPoints[0], sxReplies[0], true);
                return true;
            }
        });
        btn1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[1],false);
                sendButton(sCat, sxCaptions[1], sxPoints[1], sxReplies[1], false);
            }
        });
        btn1_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[1],true);
                sendButton(sCat, sxCaptions[1], sxPoints[1], sxReplies[1], true);
                return true;
            }
        });
        btn1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[2],false);
                sendButton(sCat, sxCaptions[2], sxPoints[2], sxReplies[2], false);
            }
        });
        btn1_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[2],true);
                sendButton(sCat, sxCaptions[2], sxPoints[2], sxReplies[2], true);
                return true;
            }
        });
        btn1_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[3],false);
                sendButton(sCat, sxCaptions[3], sxPoints[3], sxReplies[3], false);
            }
        });
        btn1_4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[3],true);
                sendButton(sCat, sxCaptions[3], sxPoints[3], sxReplies[3], true);
                return true;
            }
        });
        btn2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[4],false);
                sendButton(sCat, sxCaptions[4], sxPoints[4], sxReplies[4], false);
            }
        });
        btn2_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[4],true);
                sendButton(sCat, sxCaptions[4], sxPoints[4], sxReplies[4], true);
                return true;
            }
        });
        btn2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[5],false);
                sendButton(sCat, sxCaptions[5], sxPoints[5], sxReplies[5], false);
            }
        });
        btn2_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[5],true);
                sendButton(sCat, sxCaptions[5], sxPoints[5], sxReplies[5], true);
                return true;
            }
        });
        btn2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[6],false);
                sendButton(sCat, sxCaptions[6], sxPoints[6], sxReplies[6], false);
            }
        });
        btn2_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[6],true);
                sendButton(sCat, sxCaptions[6], sxPoints[6], sxReplies[6], true);
                return true;
            }
        });
        btn2_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[7],false);
                sendButton(sCat, sxCaptions[7], sxPoints[7], sxReplies[7], false);
            }
        });
        btn2_4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[7],true);
                sendButton(sCat, sxCaptions[7], sxPoints[7], sxReplies[7], true);
                return true;
            }
        });
        btn3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[8],false);
                sendButton(sCat, sxCaptions[8], sxPoints[8], sxReplies[8], false);
            }
        });
        btn3_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[8],true);
                sendButton(sCat, sxCaptions[8], sxPoints[8], sxReplies[8], true);
                return true;
            }
        });
        btn3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[9],false);
                sendButton(sCat, sxCaptions[9], sxPoints[9], sxReplies[9], false);
            }
        });
        btn3_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionalMsg(sxReplies[9],true);
                sendButton(sCat, sxCaptions[9], sxPoints[9], sxReplies[9], true);
                return true;
            }
        });
        btn3_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[10],false);
                sendButton(sCat, sxCaptions[10], sxPoints[10], sxReplies[10], false);
            }
        });
        btn3_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionalMsg(sxReplies[10],true);
                sendButton(sCat, sxCaptions[10], sxPoints[10], sxReplies[10], true);
                return true;
            }
        });
        btn3_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                optionalMsg(sxReplies[11],false);
                sendButton(sCat, sxCaptions[11], sxPoints[11], sxReplies[11], false);
            }
        });
        btn3_4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //StopwatchUtil.resetStopwatchStartTime(getApplicationContext(), "maint");
                optionalMsg(sxReplies[11],true);
                sendButton(sCat, sxCaptions[11], sxPoints[11], sxReplies[11], true);
                return true;
            }
        });

    }

    private void recaption() {
        MyApplication myApp = (MyApplication)getApplication();

        if(bImpuls) {
            sActiveFace = "2";
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            sActiveFace = "1";
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        sCat = prefs.getString("sCat" + sActiveFace, "");
        sDelimCaptions = prefs.getString("sDelimCaptions" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");
        sDelimPoints = prefs.getString("sDelimPoints" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");
        sDelimReplies = prefs.getString("sDelimReplies" + sActiveFace, "1;2;3;4;5;6;7;8;9;10;11;12");

        //!!! careful about the jacka$$es here
        // spaces make it work :-\
        sxCaptions = sDelimCaptions.split(";");
        sxPoints = sDelimPoints.split(";");
        sxReplies = sDelimReplies.split(";");

        if(sxReplies[11].equalsIgnoreCase(" ")) {
            sxReplies[11] = "";
        }

        btnCycl.setText(sNewCycl);
        btn1_1.setText(sxCaptions[0]);
        btn1_2.setText(sxCaptions[1]);
        btn1_3.setText(sxCaptions[2]);
        btn1_4.setText(sxCaptions[3]);
        btn2_1.setText(sxCaptions[4]);
        btn2_2.setText(sxCaptions[5]);
        btn2_3.setText(sxCaptions[6]);
        btn2_4.setText(sxCaptions[7]);
        btn3_1.setText(sxCaptions[8]);
        btn3_2.setText(sxCaptions[9]);
        btn3_3.setText(sxCaptions[10]);
        btn3_4.setText(sxCaptions[11]);

        if(bNewTrans || bNewWork || bNewTask) {
            btnCycl.setText("");

            String sCaptionLeft = "";

            if (bNewTrans) {
                sCaptionLeft = "trans";
            }
            if (bNewWork) {
                sCaptionLeft = "work";
            }
            else if (bNewTask) {
                sCaptionLeft = "task";
            }

            SpannableStringBuilder cs = new SpannableStringBuilder(sCaptionLeft + " " + myApp.sCurEvent);
            cs.setSpan(new SuperscriptSpan(), 0, sCaptionLeft.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            cs.setSpan(new RelativeSizeSpan(0.85f), 0, sCaptionLeft.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTextLeft.setText(cs);

            int iTimReq = myApp.iCurTaskTimReq;
            if (iTimReq > 1) {
                mChronometer.setVisibility(View.VISIBLE);
                tvTextRight.setText(Integer.toString(iTimReq));

                if(bTimerTicking) {
                    mChronometer.setBase(myApp.lTimerBase);
                }
                else {
                    mChronometer.setBase(SystemClock.elapsedRealtime());

                    bTimerTicking = true;
                    myApp.bTimerTicking = true;
                    myApp.lTimerBase = mChronometer.getBase();
                }

                mChronometer.start();
            }
            else
            {
                mChronometer.setVisibility(View.GONE);
                bTimerTicking = false;
                myApp.bTimerTicking = false;
            }
        }
    }

    private void optionalMsg(String sReply, boolean bIsLongClick) {
        String[] sxReplies = sReply.split("&");
        String sReplyActive = sxReplies[0].trim();

        if(bIsLongClick) {
            if(sxReplies.length > 1) {
                sReplyActive = sxReplies[1].trim();
            }
            else {
                sReplyActive = "";
            }
        }

        if(sReplyActive.length() > 0) {
            if(!sReplyActive.startsWith("num"))
            Toast.makeText(getApplicationContext(), sReplyActive, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendButton(String sCat, String sCaption, String sPts, String sReply, boolean bIsLongClick) {

        MyApplication myApp = (MyApplication)getApplication();

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        Calendar calNow = Calendar.getInstance();

        String sDate = dateFormat.format(calNow.getTime());
        //String sTime = timeFormat.format(calNow.getTime());

        String[] sxCaptions = sCaption.split(" ");
        String sCaptionActive = sxCaptions[0];

        if(bIsLongClick) {
            if(sxCaptions.length > 1) {
                sCaptionActive = sxCaptions[1];
            }
        }

        String[] sxReplies = sReply.split("&");
        String sReplyActive = sxReplies[0].trim();

        if(bIsLongClick) {
            if(sxReplies.length > 1) {
                sReplyActive = sxReplies[1].trim();
            }
        }

        if(sCaptionActive.equalsIgnoreCase("strst")) {
            if(bIsLongClick) {
                myApp.toggleTimer();
            }
            else {
                ActivityModTimer.startActivity(getApplicationContext());
            }
        }
        else if(sCaptionActive.equalsIgnoreCase("playr")) {
            WearMessage wearMessage = new WearMessage(getApplicationContext());
            wearMessage.sendMessage("/toggle-player", "", "");
        }
        else if(sCaptionActive.equalsIgnoreCase("drill")) {
            myApp.bInDrill = true;
            WearMessage wearMessage = new WearMessage(getApplicationContext());
            wearMessage.sendMessage("/toggle-drill", "", "");
        }
        else if (sCaptionActive.equalsIgnoreCase("reset")) {
            db.clearEffic();
            StopwatchUtil.setTodayStartTime(contextOfApplication, System.currentTimeMillis());
        }
        else if (sCaptionActive.equalsIgnoreCase("list")) {

            String sDelimElements = db.viewEffic();
            String sDelimPts = "";
            String sDelimReplies = "";

            Intent intent = new Intent(getApplicationContext(), ActivityList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("sListName", sCaptionActive);
            intent.putExtra("sDelimItems", sDelimElements);
            intent.putExtra("sDelimPts", sDelimPts);
            intent.putExtra("sReply", sReplyActive);

            if (sDelimReplies.length() > 0) {
                intent.putExtra("sDelimReplies", sDelimReplies);
            }
            startActivity(intent);
        }
        else if (sCaptionActive.equalsIgnoreCase("retro")) {
            ActivityInput.startActivity(getApplicationContext(),"retro");
        }
        else if (sCaptionActive.equalsIgnoreCase("imme")) {
            ActivityInput.startActivity(getApplicationContext(),"imme");
        }
        else if (sCaptionActive.equalsIgnoreCase("lvl")) {
            Toast.makeText(getApplicationContext(),
                    getBatteryLevel(),
                    Toast.LENGTH_SHORT).show();
        }
        else if (sCaptionActive.equalsIgnoreCase("tmchk")) {
            if(bIsLongClick) {
                ActivityInput.startActivity(getApplicationContext(),"tmchk");
            }
            else {
                Calendar cNow = Calendar.getInstance();
                String sDateTime = dateTimeFormat.format(cNow.getTime());

                Toast.makeText(getApplicationContext(),
                        sDateTime.substring(sDateTime.length()-2),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (sCaptionActive.equalsIgnoreCase("intf")
                || sCaptionActive.equalsIgnoreCase("spacd")
                || sCaptionActive.equalsIgnoreCase("dart")) {

            if(bDebug) {
                Toast.makeText(getApplicationContext(),
                        "reset: engaged",
                        Toast.LENGTH_LONG).show();
            }

            StopwatchUtil.resetEngagedStartTime(this, "maint");

            String sDelimElements = prefs.getString("0numbers", "");
            String sDelimPts = prefs.getString("0numbers" + "_Pts", "");
            String sDelimReplies = prefs.getString("0numbers" + "_Replies", "");

            Intent intent = new Intent(getApplicationContext(), ActivityList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("sListName", sCaptionActive);
            intent.putExtra("sDelimItems", sDelimElements);
            intent.putExtra("sDelimPts", sDelimPts);
            intent.putExtra("sReply", sReplyActive);

            if (sDelimReplies.length() > 0) {
                intent.putExtra("sDelimReplies", sDelimReplies);
            }
            startActivity(intent);
        }
        else if (sCaptionActive.equalsIgnoreCase("calm")) {
            bImpuls = !bImpuls;
            myApp.bImpuls = bImpuls;

            Intent intent = new Intent(getApplicationContext(), ActivityButtons.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else if (sCaptionActive.equalsIgnoreCase("debug")) {
            bDebug = !bDebug;
            myApp.bDebug = bDebug;
        }
        else if (sCaptionActive.equalsIgnoreCase("maint")) {
            if(bIsLongClick) {

                Calendar cNow = Calendar.getInstance();

                if(bImpuls) {
                    long passedTime = StopwatchUtil.getCalmPassedTime(contextOfApplication);
                    long passedSecs = passedTime / 1000;
                    int iMinPassed = (int) Math.round(passedSecs / 60.0);
                    db.doneEvent(
                            StopwatchUtil.getDateCalmStarted(contextOfApplication),
                            "calm",
                            iMinPassed,
                            0,
                            StopwatchUtil.getDateTimeCalmStarted(contextOfApplication),
                            timeFormat.format(cNow.getTime()));

                    db.doneEffic(
                            lCurSeshNum
                            , dateFormat.format(cNow.getTime())
                            , "calm"
                            , iMinPassed
                            , sCurEvent
                            , bTimerTicking ? 1 : 0);

                    StopwatchUtil.resetEngagedStartTime(contextOfApplication, "maint");

                    bImpuls = false;
                }
                else {
                    StopwatchUtil.resetCalmStartTime(contextOfApplication);
                    StopwatchUtil.setDateCalmStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    bImpuls = true;
                }

                myApp.bImpuls = bImpuls;

                Intent intent = new Intent(getApplicationContext(), ActivityButtons.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            else {
                if (bNewTrans || bNewWork || bNewTask) {
                    String sGamesLastStatus = StopwatchUtil.getEngagedLastStatus(this);

                    if (sGamesLastStatus.equalsIgnoreCase("maint")) {
                        StopwatchUtil.setStopwatchLastTouchedTime(this, System.currentTimeMillis());

                        long passedTime = StopwatchUtil.getEngagedPassedTime(this);
                        long passedSecs = passedTime / 1000;
                        int iPassedMin = (int) Math.round(passedSecs / 60.0);

                        db.doneEffic(
                                lCurSeshNum
                                , sDate
                                , "engaged"
                                , iPassedMin
                                , sCurEvent
                                , bTimerTicking ? 1 : 0);

                        StopwatchUtil.resetEngagedStartTime(this, "maint");
                    } else { // just started focusing
                        StopwatchUtil.resetEngagedStartTime(this, "maint");
                    }
                }
            }
            myApp.resetMissedPrompt();
        }
        else {
            String sDelimElements = prefs.getString("0" + sCaptionActive, "");
            String sDelimPts = prefs.getString("0" + sCaptionActive + "_Pts", "");
            String sDelimReplies = prefs.getString("0" + sCaptionActive + "_Replies", "");

            String sGamesLastStatus = StopwatchUtil.getEngagedLastStatus(this);

            // there are subitems for category
            if (sDelimElements.length() > 0) {

                // we know sCaption != "maint" at this point
                // so cash in the last of the engaged time
                if(sGamesLastStatus.equalsIgnoreCase("maint")) {

                    long passedLastTouchTime = StopwatchUtil.getEngagedLastTouchPassedTime(this);
                    long passedLastTouchSecs = passedLastTouchTime / 1000;

                    if (passedLastTouchSecs < 60 * 25) { // 25 min log-limit
                        StopwatchUtil.setEngagedStopTime(this, System.currentTimeMillis());

                        long passedTime = StopwatchUtil.getEngagedPassedTime(this);
                        long passedSecs = passedTime / 1000;

                        int iPassedMin = (int) Math.round(passedSecs / 60.0);

                        db.doneEffic(
                                lCurSeshNum
                                ,sDate
                                ,"engaged"
                                ,iPassedMin
                                ,sCurEvent
                                ,bTimerTicking?1:0);

                    }

                    if(bDebug) {
                        Toast.makeText(getApplicationContext(),
                                "top-off: maint\n" +
                                        "reset: missedPrompt\n" +
                                        "reset: eng\\" + sCaptionActive,
                                Toast.LENGTH_LONG).show();
                    }

                    myApp.resetMissedPrompt();

                    //!! not sure if this is right?
                    // probably yes, for lists of impulses etc.
                    StopwatchUtil.resetEngagedStartTime(this, sCaptionActive);
                }

                // get the non-special lists, including 0trans
                // this would create events for 0trans,
                // as per ActivityList non-point captions

                Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("sListName", sCaptionActive);
                intent.putExtra("sDelimItems", sDelimElements);
                intent.putExtra("sDelimPts", sDelimPts);
                intent.putExtra("sReply", sReplyActive);

                if (sDelimReplies.length() > 0) {
                    intent.putExtra("sDelimReplies", sDelimReplies);
                }
                startActivity(intent);
            }

            // there are NO subitems for category
            else {

                String[] sxPts = sPts.split("&");
                String sPtsActive = sxPts[0].trim();

                if(bIsLongClick) {
                    if(sxPts.length > 1) {
                        sPtsActive = sxPts[1].trim();
                    }
                }

                int iCheck = Integer.parseInt(sPtsActive);

                //!!! when the impulse happened is irrelevant
                if(iCheck == 0) {
                    db.addImp(sCaptionActive, 0);
                }

                // we know sCaption != "maint" at this point
                // so cash in the last of the engaged time
                if (sGamesLastStatus.equalsIgnoreCase("maint")) {

                    long passedLastTouchTime = StopwatchUtil.getEngagedLastTouchPassedTime(this);
                    long passedLastTouchSecs = passedLastTouchTime / 1000;

                    if (passedLastTouchSecs < 60 * 25) { // 25 min log-limit
                        StopwatchUtil.setEngagedStopTime(this, System.currentTimeMillis());

                        long passedTime = StopwatchUtil.getEngagedPassedTime(this);
                        long passedSecs = passedTime / 1000;

                        int iPassedMin = (int) Math.round(passedSecs / 60.0);

                        db.doneEffic(
                                lCurSeshNum
                                , sDate
                                , "engaged"
                                , iPassedMin,
                                sCurEvent
                                , bTimerTicking ? 1 : 0);

                    }

                    if(bDebug) {
                        Toast.makeText(getApplicationContext(),
                                "top-off: maint\n" +
                                        "reset: missedPrompt\n" +
                                        "reset: eng\\" + sCaptionActive,
                                Toast.LENGTH_LONG).show();
                    }

                    myApp.resetMissedPrompt();

                    //!! seems right for impulses?
                    StopwatchUtil.resetEngagedStartTime(this, sCaptionActive);
                }

                myApp.resetMissedPrompt();
            }
        }
    }

    public void startEvent(boolean bNewTrans, boolean bNewWork, boolean bNewTask) {
        String sPrefPrefix = "";
        String sListName = "";

        if(bNewTrans) {
            sPrefPrefix = "comtrans";
            sListName = "comtrans";
        }
        else if(bNewWork) {
            sPrefPrefix = "comprj";
            sListName = "comwork";
        }
        else if(bNewTask) {
            sPrefPrefix = "comtas";
            sListName = "comtas";
        }

        String sDelimElements = prefs.getString("0" + sPrefPrefix, "");
        String sDelimPts = prefs.getString("0" + sPrefPrefix + "_Pts", "");
        String sDelimReplies = prefs.getString("0" + sPrefPrefix + "_Replies", "");

        Intent intent = new Intent(getApplicationContext(), ActivityList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("sListName", sListName);
        intent.putExtra("sDelimItems", sDelimElements);
        intent.putExtra("sDelimPts", sDelimPts);
        //intent.putExtra("sReply", sReplyActive);

        if (sDelimReplies.length() > 0) {
            intent.putExtra("sDelimReplies", sDelimReplies);
        }

        db.clearImp();
        startActivity(intent);
    }

    private String getBatteryLevel()
    {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus =  registerReceiver(null, iFilter);
        int iLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return Integer.toString(iLevel);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}