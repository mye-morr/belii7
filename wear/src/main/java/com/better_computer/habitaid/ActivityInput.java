package com.better_computer.habitaid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.PressedData;
import com.better_computer.habitaid.share.WearMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class ActivityInput extends WearableActivity
{
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static DateFormat hourFormat = new SimpleDateFormat("HH");
    private Database db;

    private volatile String sBuf;
    private volatile int iPressed = -1;
    private volatile int iPressedCnt = -1;
    private volatile boolean bTimes = false;

    private Handler handlerIntervalTimer = new Handler();
    final Runnable r = new Runnable() {
        public void run() {
            try {
                if (sBuf.length() > 0) {
                    if (bTimes) {
                        mTvTimes.setText(mTvTimes.getText().toString() + sBuf);
                    } else {
                        mTvLetters.setText(mTvLetters.getText().toString() + sBuf);
                    }

                    sBuf = "";
                    iPressed = -1;
                    iPressedCnt = 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Button btn0_1;
    private Button btn0_2;
    private Button btn0_3;
    private Button btn0_4;
    private Button btn0_5;
    private Button btn1_1;
    private Button btn1_2;
    private Button btn1_3;
    private Button btn1_4;
    private Button btn1_5;
    private Button btn2_1;
    private Button btn2_2;
    private Button btn2_3;
    private Button btn2_4;
    private Button btn2_5;
    private Button btn3_1;
    private Button btn3_2;
    private Button btn3_3;
    private Button btn3_4;
    private Button btn3_5;

    private TextView mTvTimes;
    private TextView mTvLetters;
    private TextView mTvMainTask;
    private Button mBtnBsTimes;
    private Button mBtnBsLetters;
    private Button mBtnCancel;
    private Button mBtnAccept;

    private String sPurpose = "";
    private String sType = "";
    private PressedData pressedData;

    private static final String LOG_TAG = ActivityInput.class.getSimpleName();

    // could be used to postpone, sched new tasks, start a session, log events
    // in all of these cases, sMainTask is not for what it sounds like
    public static final void startActivity(Context context, String sPurpose) {
        Intent intent = new Intent(context, ActivityInput.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Purpose", sPurpose);
        context.startActivity(intent);
    }

    // could be used to postpone, sched new tasks, start a session, log events
    // in all of these cases, sMainTask is not for what it sounds like
    public static final void startActivity(Context context, String sPurpose, String sTask, String sType) {
        Intent intent = new Intent(context, ActivityInput.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Purpose", sPurpose);
        intent.putExtra("Task", sTask);
        intent.putExtra("Type", sType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sPurpose = getIntent().getStringExtra("Purpose");

        final MyApplication myApp = (MyApplication)getApplication();
        db = new Database(getApplicationContext());

        pressedData = new PressedData();

        sBuf = "";

        btn0_1 = (Button) findViewById(R.id.btn0_1);
        btn0_2 = (Button) findViewById(R.id.btn0_2);
        btn0_3 = (Button) findViewById(R.id.btn0_3);
        btn0_4 = (Button) findViewById(R.id.btn0_4);
        btn0_5 = (Button) findViewById(R.id.btn0_5);
        btn1_1 = (Button) findViewById(R.id.btn1_1);
        btn1_2 = (Button) findViewById(R.id.btn1_2);
        btn1_3 = (Button) findViewById(R.id.btn1_3);
        btn1_4 = (Button) findViewById(R.id.btn1_4);
        btn1_5 = (Button) findViewById(R.id.btn1_5);
        btn2_1 = (Button) findViewById(R.id.btn2_1);
        btn2_2 = (Button) findViewById(R.id.btn2_2);
        btn2_3 = (Button) findViewById(R.id.btn2_3);
        btn2_4 = (Button) findViewById(R.id.btn2_4);
        btn2_5 = (Button) findViewById(R.id.btn2_5);
        btn3_1 = (Button) findViewById(R.id.btn3_1);
        btn3_2 = (Button) findViewById(R.id.btn3_2);
        btn3_3 = (Button) findViewById(R.id.btn3_3);
        btn3_4 = (Button) findViewById(R.id.btn3_4);
        btn3_5 = (Button) findViewById(R.id.btn3_5);

        mTvTimes = (TextView) findViewById(R.id.tvTimes);
        mTvLetters = (TextView) findViewById(R.id.tvLetters);
        mTvMainTask = (TextView) findViewById(R.id.tvMainTask);
        mBtnBsTimes = (Button) findViewById(R.id.btnBsTimes);
        mBtnBsLetters = (Button) findViewById(R.id.btnBsLetters);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnAccept = (Button) findViewById(R.id.btnAccept);

        mTvMainTask.setText(sPurpose);

        if(sPurpose.equalsIgnoreCase("comwork")) {
            String sTask = getIntent().getStringExtra("Task");
            sType = getIntent().getStringExtra("Type");
            mTvLetters.setText(sTask);
            bTimes = true;

            sPurpose = "new_work";
        }
        else if(sPurpose.equalsIgnoreCase("comtas")) {
            String sTask = getIntent().getStringExtra("Task");
            sType = getIntent().getStringExtra("Type");
            mTvLetters.setText(sTask);
            bTimes = true;

            sPurpose = "new_task";
        }
        else if(sPurpose.equalsIgnoreCase("comtrans")) {
            String sTask = getIntent().getStringExtra("Task");
            sType = getIntent().getStringExtra("Type");
            mTvLetters.setText(sTask);
            bTimes = true;

            sPurpose = "new_trans";
        }
        else if(sPurpose.equalsIgnoreCase("spacd")) {
            bTimes = true;
        }
        else if(sPurpose.equalsIgnoreCase("tmchk")) {
            bTimes = true;
        }
        else if(sPurpose.equalsIgnoreCase("postpone")) {
            bTimes = true;
        }
        else if(sPurpose.equalsIgnoreCase("l0st")) {
            String sTask = getIntent().getStringExtra("Task");
            mTvLetters.setText(sTask);
            bTimes = true;
        }
        else if(sPurpose.equalsIgnoreCase("intf")) {
            String sTask = getIntent().getStringExtra("Task");
            mTvLetters.setText(sTask);
            bTimes = true;
        }
        else if(sPurpose.equalsIgnoreCase("postwhip")) {
            bTimes = true;
        }

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityButtons.startActivity(getApplicationContext());
            }
        });

        // could be used to postpone, sched new tasks, start a session, log events
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String sTask = mTvLetters.getText().toString();
            String sMinutes = mTvTimes.getText().toString();

            if(sPurpose.equalsIgnoreCase("postpone")) {
                myApp.setTimeTaskDue(System.currentTimeMillis()
                        + Integer.valueOf(sMinutes) * 60 * 1000);
            }
            else if(sPurpose.equalsIgnoreCase("postwhip")) {
                int iIncr = Integer.valueOf(sMinutes);

                Random rand = new Random();

                int iPlusMinus = 1;
                if (rand.nextDouble() < 0.5) {
                    iPlusMinus = -1;
                }

                iIncr += Math.round(iPlusMinus * 0.25 * iIncr);

                myApp.setTimeWhipDue(System.currentTimeMillis()
                        + iIncr * 60 * 1000);
            }
            else if(sPurpose.equalsIgnoreCase("new_work")) {

                Calendar cNow = Calendar.getInstance();
                StopwatchUtil.resetEventStartTime(getApplicationContext());
                StopwatchUtil.setDateEventStarted(
                        dateFormat.format(cNow.getTime()),
                        dateTimeFormat.format(cNow.getTime()));

                myApp.bNewWork = true;
                myApp.sCurEvent = sTask;
                myApp.sCurType = sType;
            }
            else if(sPurpose.equalsIgnoreCase("new_task")) {
                Calendar cNow = Calendar.getInstance();
                StopwatchUtil.resetEventStartTime(getApplicationContext());
                StopwatchUtil.setDateEventStarted(
                        dateFormat.format(cNow.getTime()),
                        dateTimeFormat.format(cNow.getTime()));

                myApp.bNewTask = true;
                myApp.sCurEvent = sTask;
                myApp.sCurType = sType;

                /* THIS IS THE GOAL!! BACKBURNER FOR NOW
                myApp.iCurTaskTimReq = Integer.valueOf(sMinutes);
                myApp.setTimeTaskDue(System.currentTimeMillis()
                        + Integer.valueOf(sMinutes) * 60 * 1000);
                */
            }
            else if(sPurpose.equalsIgnoreCase("new_trans")) {

                Calendar cNow = Calendar.getInstance();
                StopwatchUtil.resetEventStartTime(getApplicationContext());
                StopwatchUtil.setDateEventStarted(
                        dateFormat.format(cNow.getTime()),
                        dateTimeFormat.format(cNow.getTime()));

                myApp.bNewTrans = true;
                myApp.sCurEvent = sTask;
                myApp.sCurType = sType;

            }
            else if(sPurpose.equalsIgnoreCase("retro")) {
                if(sMinutes.length()==0) {
                    sMinutes = "1";
                }

                long passedTime = StopwatchUtil.getEventPassedTime(getApplicationContext());
                long passedSecs = passedTime / 1000;
                int iPassedMin = (int) Math.round(passedSecs / 60.0);

                /*
                db.doneTask(
                        sTask,
                        parseInt(sMinutes),
                        parseInt(sMinutes),
                        iPassedMin - parseInt(sMinutes)
                );
                */

                myApp.addPtsCur(parseInt(sMinutes));
            }
            else if(sPurpose.equalsIgnoreCase("imme")) {
                if(sMinutes.length()==0) {
                    sMinutes = "4";
                }

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendMessage("/new-toda", sTask, sMinutes);
            }
            else if(sPurpose.equalsIgnoreCase("tmchk")) {
                if(sMinutes.length()==0) {
                    sMinutes = "0";
                }

                Calendar calNow = Calendar.getInstance();

                String sHour = hourFormat.format(calNow.getTime());
                int iHour = Integer.parseInt(sHour);
                int iMinutes = Integer.parseInt(sMinutes);

                if ((iHour >= 19 && iMinutes <= 5)
                    || (iHour < iMinutes)) {
                    Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
                }
            }
            else if(sPurpose.equalsIgnoreCase("l0st")) {
                if(sMinutes.length()==0) {
                    sMinutes = "1";
                }

                Calendar calNow = Calendar.getInstance();

                String sDate = dateFormat.format(calNow.getTime());
                String sTime = timeFormat.format(calNow.getTime());

                db.doneTimDecr(sDate, sTask, parseInt(sMinutes), sTime);
            }

            ActivityButtons.startActivity(getApplicationContext());
            }
        });

        mTvTimes.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                bTimes = true;
            }
        });

        mTvLetters.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                bTimes = false;
            }
        });

        mBtnBsTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foo = mTvTimes.getText().toString();
                if(foo.length() > 0) {
                    mTvTimes.setText(foo.substring(0, foo.length() - 1));
                }
            }
        });

        mBtnBsLetters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String foo = mTvLetters.getText().toString();
            if(foo.length() > 0) {
                mTvLetters.setText(foo.substring(0, foo.length() - 1));
            }
            }
        });

        btn0_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(0, btn0_1.getText().toString());
            }
        });
        btn0_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(1, btn0_2.getText().toString());
            }
        });
        btn0_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(2, btn0_3.getText().toString());
            }
        });
        btn0_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(3, btn0_4.getText().toString());
            }
        });
        btn0_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(4, btn0_5.getText().toString());
            }
        });
        btn1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(5, btn1_1.getText().toString());
            }
        });
        btn1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(6, btn1_2.getText().toString());
            }
        });
        btn1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(7, btn1_3.getText().toString());
            }
        });
        btn1_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(8, btn1_4.getText().toString());
            }
        });
        btn1_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(9, btn1_5.getText().toString());
            }
        });
        btn2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(10, btn2_1.getText().toString());
            }
        });
        btn2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(11, btn2_2.getText().toString());
            }
        });
        btn2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(12, btn2_3.getText().toString());
            }
        });
        btn2_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(13, btn2_4.getText().toString());
            }
        });
        btn2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(14, btn2_5.getText().toString());
            }
        });
        btn3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(15, btn3_1.getText().toString());
            }
        });
        btn3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(16, btn3_2.getText().toString());
            }
        });
        btn3_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(17, btn3_3.getText().toString());
            }
        });
        btn3_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(18, btn3_4.getText().toString());
            }
        });
        btn3_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButton(19, btn3_5.getText().toString());
            }
        });
    }

    private void processButton(int iIdx, String sCaption) {
        handlerIntervalTimer.removeCallbacks(r);

        if(iPressed == iIdx) {
            iPressedCnt++;

            if(iPressedCnt < sCaption.length()) {
                sBuf = sCaption.substring(iPressedCnt, iPressedCnt + 1);
            }
            else {
                if(bTimes) {
                    mTvTimes.setText(mTvTimes.getText().toString() + sBuf);
                }
                else {
                    mTvLetters.setText(mTvLetters.getText().toString() + sBuf);
                }

                sBuf = sCaption.substring(0,1);
                iPressedCnt = 0;
            }
        }
        else {
            if(bTimes) {
                mTvTimes.setText(mTvTimes.getText().toString() + sBuf);
            }
            else {
                mTvLetters.setText(mTvLetters.getText().toString() + sBuf);
            }

            sBuf = sCaption.substring(0,1);
            iPressedCnt = 0;
        }

        iPressed = iIdx;
        handlerIntervalTimer.postDelayed(r, 1000);
    }
}