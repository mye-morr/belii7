package com.better_computer.habitaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.PressedData;
import com.better_computer.habitaid.share.WearMessage;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private Button btnUndo;
    private Button btnWork;
    private Button btnOther;
    private Button btnTransDoneCanc;

    private String sCat;
    private String sDelimCaptions;
    private String[] sxCaptions;
    private String sDelimReplies;
    private String[] sxReplies;
    private String sDelimPoints;
    private String[] sxPoints;
    private String sActiveFace;

    private boolean bNewWork;
    private boolean bNewTask;
    private boolean bNewTrans;
    private boolean bImpuls;
    private String sCurEvent;
    private String sType;

    private boolean bShowPts = false;

    private PressedData pressedData;
    private Database db;
    public static Context contextOfApplication;

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

        pressedData = new PressedData();
        db = new Database(getApplicationContext());
        bNewWork = myApp.bNewWork;
        bNewTask = myApp.bNewTask;
        bNewTrans = myApp.bNewTrans;
        bImpuls = myApp.bImpuls;
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
                        intent.putExtra("sType", "prj/smtas");

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
                        intent.putExtra("sType", "timDecr/ptsPos");

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
        btnWork = (Button) findViewById(R.id.btnWork);
        btnOther = (Button) findViewById(R.id.btnOther);
        btnTransDoneCanc = (Button) findViewById(R.id.btnTransDoneCanc);
        btnUndo = (Button) findViewById(R.id.btnUndo);

        if(bNewWork || bNewTask || bNewTrans) {
            btnWork.setText("");
            btnOther.setText("");
            btnTransDoneCanc.setText("dn cn");

        }
        else {
            btnWork.setText("work");
            btnOther.setText("other");
            btnTransDoneCanc.setText("trans");
        }

        recaption();

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

                if(bNewWork || bNewTask || bNewTrans) {
                    // don't process phantom clicks
                }
                else {
                    db.clearImp();

                    bNewWork = true;
                    myApp.bNewWork = true;
                    myApp.sCurEvent = "";
                    myApp.sCurType = "";

                    Calendar cNow = Calendar.getInstance();
                    StopwatchUtil.resetEventStartTime(contextOfApplication);
                    StopwatchUtil.setDateEventStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    String sDelimElements = prefs.getString("0comprj", "");
                    String sDelimPts = prefs.getString("0comprj" + "_Pts", "");
                    String sDelimReplies = prefs.getString("0comprj" + "_Replies", "");

                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("sListName", "comwork");
                    intent.putExtra("sDelimItems", sDelimElements);
                    intent.putExtra("sDelimPts", sDelimPts);
                    //intent.putExtra("sReply", sReplyActive);

                    if (sDelimReplies.length() > 0) {
                        intent.putExtra("sDelimReplies", sDelimReplies);
                    }

                    bImpuls = true;
                    myApp.bImpuls = true;
                    startActivity(intent);
                }
            }
        });

        btnOther.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aka "done"
                MyApplication myApp = ((MyApplication)getApplication());

                if(bNewWork || bNewTask || bNewTrans) {
                    // don't process phantom clicks
                }
                else {
                    db.clearImp();

                    bNewTask = true;
                    myApp.bNewTask = true;
                    myApp.sCurEvent = "";
                    myApp.sCurType = "";

                    Calendar cNow = Calendar.getInstance();
                    StopwatchUtil.resetEventStartTime(contextOfApplication);
                    StopwatchUtil.setDateEventStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    String sDelimElements = prefs.getString("0comtas", "");
                    String sDelimPts = prefs.getString("0comtas" + "_Pts", "");
                    String sDelimReplies = prefs.getString("0comtas" + "_Replies", "");

                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("sListName", "comtas");
                    intent.putExtra("sDelimItems", sDelimElements);
                    intent.putExtra("sDelimPts", sDelimPts);
                    //intent.putExtra("sReply", sReplyActive);

                    if (sDelimReplies.length() > 0) {
                        intent.putExtra("sDelimReplies", sDelimReplies);
                    }

                    bImpuls = true;
                    myApp.bImpuls = true;
                    startActivity(intent);
                }
            }
        });

        btnTransDoneCanc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MyApplication myApp = ((MyApplication) getApplication());

                if(bNewWork || bNewTask || bNewTrans) {
                    // aka "cancel"

                    tvTextLeft.setText("");
                    btnWork.setText("work");
                    btnOther.setText("oth");
                    btnTransDoneCanc.setText("trans");

                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;
                    bNewTrans = false;
                    myApp.bNewTrans = false;
                    bImpuls = false;
                    myApp.bImpuls = false;
                    sCurEvent = "";
                    myApp.sCurEvent = "";

                    db.clearImp();
                    recaption();
                }

                return true;
            }
        });

        btnTransDoneCanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApp = ((MyApplication)getApplication());

                if(bNewWork) {
                    tvTextLeft.setText("");
                    btnWork.setText("work");
                    btnOther.setText("oth");
                    btnTransDoneCanc.setText("trans");

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

                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;
                    bNewTrans = false;
                    myApp.bNewTrans = false;
                    bImpuls = false;
                    myApp.bImpuls = false;

                    sCurEvent = myApp.sCurEvent;
                    sType = myApp.sCurType;

                    prefs.edit().putString("sActiveFace","1").apply();
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Calendar cNow = Calendar.getInstance();

                    long passedTime = StopwatchUtil.getEventPassedTime(contextOfApplication);
                    long passedSecs = passedTime / 1000;
                    int iMinPassed = (int) Math.round(passedSecs / 60.0);
                    db.doneEvent(
                            StopwatchUtil.getDateEventStarted(contextOfApplication),
                            "work: " + myApp.sCurEvent,
                            iMinPassed,
                            0,
                            StopwatchUtil.getDateTimeEventStarted(contextOfApplication),
                            timeFormat.format(cNow.getTime()));

                    Intent intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("sTrans", sCurEvent);
                    intent.putExtra("iMin", iMinPassed);
                    intent.putExtra("sType", sType);

                    sCurEvent = "";
                    myApp.sCurEvent = "";
                    sType = "";
                    // application-level sCurType
                    // will be used by ActivityTwoLists
                    //myApp.sCurType = "";

                    startActivity(intent);
                }
                else if(bNewTask) {
                    tvTextLeft.setText("");
                    btnWork.setText("work");
                    btnOther.setText("oth");
                    btnTransDoneCanc.setText("trans");

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

                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;
                    bNewTrans = false;
                    myApp.bNewTrans = false;
                    bImpuls = false;
                    myApp.bImpuls = false;

                    sCurEvent = myApp.sCurEvent;
                    sType = myApp.sCurType;

                    sActiveFace = "1";
                    prefs.edit().putString("sActiveFace","1").apply();
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Calendar cNow = Calendar.getInstance();

                    long passedTime = StopwatchUtil.getEventPassedTime(contextOfApplication);
                    long passedSecs = passedTime / 1000;
                    int iMinPassed = (int) Math.round(passedSecs / 60.0);
                    db.doneEvent(
                            StopwatchUtil.getDateEventStarted(contextOfApplication),
                            "oth: " + myApp.sCurEvent,
                            iMinPassed,
                            0,
                            StopwatchUtil.getDateTimeEventStarted(contextOfApplication),
                            timeFormat.format(cNow.getTime()));

                    Intent intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("sTrans", sCurEvent);
                    intent.putExtra("iMin", iMinPassed);
                    intent.putExtra("sType", sType);

                    sCurEvent = "";
                    myApp.sCurEvent = "";
                    sType = "";
                    // application-level sCurType
                    // will be used by ActivityTwoLists
                    //myApp.sCurType = "";

                    startActivity(intent);
                }
                else if(bNewTrans) {
                    // already in transition
                    // so clicked on 'dn'

                    btnWork.setText("work");
                    btnOther.setText("oth");
                    btnTransDoneCanc.setText("trans");

                    bNewWork = false;
                    myApp.bNewWork = false;
                    bNewTask = false;
                    myApp.bNewTask = false;
                    bNewTrans = false;
                    myApp.bNewTrans = false;
                    bImpuls = false;
                    myApp.bImpuls = false;

                    prefs.edit().putString("sActiveFace","1").apply();
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Calendar cNow = Calendar.getInstance();

                    long passedTime = StopwatchUtil.getEventPassedTime(contextOfApplication);
                    long passedSecs = passedTime / 1000;
                    int iMinPassed = (int) Math.round(passedSecs / 60.0);
                    db.doneEvent(
                            StopwatchUtil.getDateEventStarted(contextOfApplication),
                            "trans: " + sCurEvent,
                            iMinPassed,
                            0,
                            StopwatchUtil.getDateTimeEventStarted(contextOfApplication),
                            timeFormat.format(cNow.getTime()));

                    Intent intent = new Intent(getApplicationContext(), ActivityTwoLists.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    intent.putExtra("sTrans", sCurEvent);
                    intent.putExtra("iMin", iMinPassed);


                    sCurEvent = "";
                    myApp.sCurEvent = "";
                    sType = "";
                    myApp.sCurType = "";

                    startActivity(intent);
                }
                else {
                    // not yet in session
                    // so clicked on 'trans'

                    db.clearImp();
                    btnWork.setText("");
                    btnOther.setText("");
                    btnTransDoneCanc.setText("dn cn");

                    Calendar cNow = Calendar.getInstance();
                    StopwatchUtil.resetEventStartTime(contextOfApplication);
                    StopwatchUtil.setDateEventStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    db.clearImp();

                    bNewTrans = true;
                    myApp.bNewTrans = true;
                    bImpuls = true;
                    myApp.bImpuls = true;

                    myApp.sCurEvent = "";
                    myApp.sCurType = "";

                    cNow = Calendar.getInstance();
                    StopwatchUtil.resetEventStartTime(contextOfApplication);
                    StopwatchUtil.setDateEventStarted(
                            dateFormat.format(cNow.getTime()),
                            dateTimeFormat.format(cNow.getTime()));

                    String sDelimElements = prefs.getString("0comtrans", "");
                    String sDelimPts = prefs.getString("0comtrans" + "_Pts", "");
                    String sDelimReplies = prefs.getString("0comtrans" + "_Replies", "");

                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("sListName", "comtrans");
                    intent.putExtra("sDelimItems", sDelimElements);
                    intent.putExtra("sDelimPts", sDelimPts);
                    //intent.putExtra("sReply", sReplyActive);

                    if (sDelimReplies.length() > 0) {
                        intent.putExtra("sDelimReplies", sDelimReplies);
                    }

                    startActivity(intent);
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
                //String sCaption = btn2_4.getText().toString();

                /*
                MyApplication myApp = (MyApplication)getApplication();
                myApp.bIsTransition = !myApp.bIsTransition;
                */

                optionalMsg(sxReplies[7],false);
                sendButton(sCat, sxCaptions[7], sxPoints[7], sxReplies[7], false);

                /*
                if(sCaption.equalsIgnoreCase("nxste")) {
                    btn2_4.setText("done");
                }
                else if(sCaption.equalsIgnoreCase("done")) {
                    btn2_4.setText("nxste");
                }
                */
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

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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

        tvTextLeft.setText(myApp.sCurEvent);
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
        btnUndo = (Button) findViewById(R.id.btnUndo);
        btnWork = (Button) findViewById(R.id.btnWork);

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
        else if (sCaptionActive.equalsIgnoreCase("retro")) {
            ActivityInput.startActivity(getApplicationContext(),"retro");
        }
        else if (sCaptionActive.equalsIgnoreCase("imme")) {
            ActivityInput.startActivity(getApplicationContext(),"imme");
        }
        else if (sCaptionActive.equalsIgnoreCase("tmchk")) {
            ActivityInput.startActivity(getApplicationContext(),"tmchk");
        }
        else if (sCaptionActive.equalsIgnoreCase("intf")
                || sCaptionActive.equalsIgnoreCase("spacd")) {

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
        else if (sCaptionActive.equalsIgnoreCase("a-int")) {
            Calendar calNow = Calendar.getInstance();
            String sDate = dateFormat.format(calNow.getTime());

            db.addPts(sDate, 3, sCaptionActive, 0);
        }
        else if (sCaptionActive.equalsIgnoreCase("clr-i")) {
            db.clearImp();
        }
        else if (sCaptionActive.equalsIgnoreCase("reset")) {
            db.clearDb();
        }
        else if (sCaptionActive.equalsIgnoreCase("calm")) {
            bImpuls = !bImpuls;
            myApp.bImpuls = bImpuls;

            Intent intent = new Intent(getApplicationContext(), ActivityButtons.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else if (sCaptionActive.equalsIgnoreCase("maint")) {
            String sGamesLastStatus = StopwatchUtil.getEngagedLastStatus(this);

            if(sGamesLastStatus.equalsIgnoreCase("maint")) {
                StopwatchUtil.setStopwatchLastTouchedTime(this, System.currentTimeMillis());

                long passedTime = StopwatchUtil.getEngagedPassedTime(this);
                long passedSecs = passedTime / 1000;

                int iPassedMin = (int) Math.round(passedSecs / 60.0);
                myApp.addTimeEngaged(iPassedMin);

                StopwatchUtil.resetEngagedStartTime(this, "maint");
            }
            else { // just started focusing
                StopwatchUtil.resetEngagedStartTime(this, "maint");
            }

            myApp.resetMissedPrompt();
        }
        else {
            String sDelimElements = prefs.getString("0" + sCaptionActive, "");
            String sDelimPts = prefs.getString("0" + sCaptionActive + "_Pts", "");
            String sDelimReplies = prefs.getString("0" + sCaptionActive + "_Replies", "");

            // there are subitems for category
            if (sDelimElements.length() > 0) {
                String sGamesLastStatus = StopwatchUtil.getEngagedLastStatus(this);

                // we know sCaption != "maint" at this point
                if(sGamesLastStatus.equalsIgnoreCase("maint")) {

                    long passedLastTouchTime = StopwatchUtil.getEngagedLastTouchPassedTime(this);
                    long passedLastTouchSecs = passedLastTouchTime / 1000;

                    // ^^ looks like this is to
                    // keep tracking engaged time etc.
                    // which kinda almost makes sense??
                    if (passedLastTouchSecs < 60 * 25) { // 25 min log-limit
                        StopwatchUtil.setEngagedStopTime(this, System.currentTimeMillis());

                        long passedTime = StopwatchUtil.getEngagedPassedTime(this);
                        long passedSecs = passedTime / 1000;

                        int iPassedMin = (int) Math.round(passedSecs / 60.0);
                        myApp.addTimeEngaged(iPassedMin);

                        myApp.resetMissedPrompt();
                    }

                    // sets ENGAGED_LAST_STATUS to "maint"
                    StopwatchUtil.resetEngagedStartTime(this, "maint");
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

                long passedTime = StopwatchUtil.getEventPassedTime(this);
                long passedSecs = passedTime / 1000;
                int iPassedMin = (int) Math.round(passedSecs / 60.0);

                Calendar calNow = Calendar.getInstance();

                String sDate = dateFormat.format(calNow.getTime());
                String sTime = timeFormat.format(calNow.getTime());

                String[] sxPts = sPts.split("&");
                String sPtsActive = sxPts[0].trim();

                if(bIsLongClick) {
                    if(sxPts.length > 1) {
                        sPtsActive = sxPts[1].trim();
                    }
                }

                int iCheck = Integer.parseInt(sPtsActive);
                if(iCheck == 0) {
                    db.addImp(sCaptionActive, iPassedMin);
                }
                else {
                    // !!! attention: if need to affect points
                    // from home screen, not from ActivityList

                    // there's no way to reset the points currently
                    // so don't add them in the first place

                    //db.addPts(iCheck, sCaptionActive, iPassedMin);

                    // this would trigger anxieties etc.
                    //myApp.addPtsCur(iCheck);
                }

                myApp.resetMissedPrompt();
            }
        }
    }

    /*
        else if (sCaption.equalsIgnoreCase("done")){
            StopwatchUtil.resetTransitionStartTime(this);
            myApp.resetMissedPrompt();
        }
        else if (sCaption.equalsIgnoreCase("log")) {
            ActivityInput.startActivity(getApplicationContext(),"log");
        }
        else if (sCaption.equalsIgnoreCase("nxste")) {
            StopwatchUtil.setTransitionStopTime(this, System.currentTimeMillis());

            long passedTime = StopwatchUtil.getTransitionPassedTime(this);
            long passedSecs = passedTime / 1000;
            int iPassedMin = (int) Math.round(passedSecs / 60.0);

            if (iPassedMin <= 3) {
                myApp.iCountGoodTransitions++;
            } else {
                myApp.iCountGoodTransitions = 0;
            }

            MessageData messageData = new MessageData();
            messageData.setText1("transition");
            messageData.setText2(String.valueOf(iPassedMin));

            myApp.resetMissedPrompt();

            final String messageString = messageData.toJsonString();
            WearMessage wearMessage = new WearMessage(getApplicationContext());
            wearMessage.sendMessage("/done-task", messageString);

        }
    */

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}