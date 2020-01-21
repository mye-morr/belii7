package com.better_computer.habitaid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.WearMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityTwoLists extends Activity{

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;
    private ListView mListViewLeft;
    private ListView mListViewTimes;
    private ListView mListViewRight;
    private String sCurTask;
    private String sTrans;
    private String sType;
    private int iMin;
    private int iImpul;
    private Database db;

    // 0 is after task, 1 is timDecr/numIncr, 2 is prj/smtas
    private int iMode = 0;
    private boolean bAfterTask = false;
    private boolean bModePrjSmTas = false;

    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, ActivityTwoLists.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_lists);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final MyApplication myApp = (MyApplication)getApplication();
        bAfterTask = false;

        Intent intent = getIntent();
        if(intent.hasExtra("sTrans")) {
            iMode = 0;
            sTrans = intent.getStringExtra("sTrans");
            iMin = intent.getIntExtra("iMin", 0);
            bAfterTask = true;
        }
        else {
            sType = intent.getStringExtra("sType");
            if(sType.equalsIgnoreCase("dash/day")) {
                iMode = 2;
            }
            else if(sType.equalsIgnoreCase("day/wk")) {
                iMode = 1;
            }
        }

        db = new Database(getApplicationContext());
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                mListViewLeft = (ListView) stub.findViewById(R.id.list_left);
                mListViewTimes = (ListView) stub.findViewById(R.id.list_times);
                mListViewRight = (ListView) stub.findViewById(R.id.list_right);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                //final String[] sxPrj = db.readPrjNoTime();
                //final String[] sxSmTas = db.readSmTasNoTime();

                Calendar calNow = Calendar.getInstance();
                // prj/smtas
                if(iMode == 2) { //dash/day

                    final String[] sxSesh = db.summaryEfficSesh(myApp.getSeshCur());
                    mListViewLeft.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxSesh));

                    //!!! should be from day-start ie long -> date time
                    // do a lookup for date and greater than time etc.
                    // call it 'since-time' etc.

                    //final String[] sxDay = db.summaryEfficDay(dateFormat.format(calNow.getTime()));
                    //mListViewRight.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    //        R.layout.listview_row, android.R.id.text1, sxDay));

                    /*
                    final String[] sxDash = db.getPrj();
                    final String[] sxDay = db.getSmTas();

                    mListViewLeft.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxDash));
                    mListViewRight.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxDay));
                    */
                }
                // timDecr/ptsPos
                else if (iMode ==1) {
                    /*
                    final String[] sxTimDecr = db.summaryTimDecr(dateFormat.format(calNow.getTime()));
                    final String[] sxPtsPos = db.summaryPtsPos(dateFormat.format(calNow.getTime()));

                    mListViewLeft.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxTimDecr));
                    mListViewRight.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxPtsPos));
                    */
                }
                else { // after task
                    final String[] sxStats = db.summaryStats(sTrans, iMin);
                    final String[] sxPtsPos = db.summaryPtsPos(dateFormat.format(calNow.getTime()));

                    mListViewLeft.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxStats));
                    mListViewRight.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.listview_row, android.R.id.text1, sxPtsPos));
                }

                final String[] sxTimes = listTimes.toArray(new String[]{});

                mListViewTimes.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.listview_row_center, android.R.id.text1, sxTimes));

                mListViewLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        /*
                        String sWithTime = mListViewPrj.getItemAtPosition(i).toString();
                        int iPosSpace = sWithTime.indexOf(' ');

                        ActivityInput.startActivity(getApplicationContext(),
                                sWithTime.substring(iPosSpace+1));
                        */

                        ActivityInput.startActivity(getApplicationContext(),
                                mListViewLeft.getItemAtPosition(i).toString());
                    }
                });

                mListViewTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String sMinutes = mListViewTimes.getItemAtPosition(i).toString();

                        if(sMinutes.equalsIgnoreCase("back")) {
                        }
                        else {
                            MyApplication myApp = (MyApplication)getApplication();
                            myApp.sCurEvent = sCurTask;
                            myApp.bNewTask = true;
                            myApp.resetMissedPrompt();

                            StopwatchUtil.resetEventStartTime(getApplicationContext());

                            // old method
                            //db.addTimeSmTas(sCurTask, sMinutes);

                            MessageData messageData = new MessageData();
                            messageData.setText1(sCurTask);
                            messageData.setText2(sMinutes);

                            final String messageString = messageData.toJsonString();
                            WearMessage wearMessage = new WearMessage(getApplicationContext());
                            wearMessage.sendData("/sched-task", messageString);
                        }

                        ActivityButtons.startActivity(getApplicationContext());
                    }
                });

                mListViewRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        /*
                        String sWithTime = mListViewSmTas.getItemAtPosition(i).toString();
                        int iPosSpace = sWithTime.indexOf(' ');
                        sCurTask = sWithTime.substring(iPosSpace+1);
                        */
                        sCurTask = mListViewRight.getItemAtPosition(i).toString();

                        mListViewLeft.setVisibility(View.GONE);
                        mListViewTimes.setVisibility(View.VISIBLE);
                    }
                });

                mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
                mDismissOverlayView = new DismissOverlayView(ActivityTwoLists.this);
                mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));

                // Configure a gesture detector
                mGestureDetector = new GestureDetector(ActivityTwoLists.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent event) {
                        ((MyApplication)getApplication()).offTimer();
                        mDismissOverlayView.show();
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {

                        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                            case 1:
                                if(bAfterTask) {
                                    db.doneTrans(sTrans, iMin);

                                    MyApplication myApp = (MyApplication)getApplication();
                                    Calendar calNow = Calendar.getInstance();

                                    String sDate = dateFormat.format(calNow.getTime());
                                    String sTime = timeFormat.format(calNow.getTime());

                                    // enter fufi
                                    String sType = myApp.sCurType;

                                    // at the moment this is not displayed anywhere
                                    if(sType.equalsIgnoreCase("timIncr")) {
                                        //db.doneTimIncr(sDate, sTrans, iMin, sTime);
                                    }
                                    // this gets displayed in iMode = 1
                                    else if (sType.equalsIgnoreCase("timDecr")) {
                                        //db.doneTimDecr(sDate, sTrans, iMin, sTime);
                                    }

                                    myApp.sCurType = "";
                                }

                                ActivityButtons.startActivity(getApplicationContext());

                                //Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
                                return true;
                            case 2:
                                //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                                return true;
                            case 3:
                                if(bAfterTask) {
                                    db.doneTrans(sTrans, iMin);

                                    MyApplication myApp = (MyApplication)getApplication();
                                    Calendar calNow = Calendar.getInstance();

                                    String sDate = dateFormat.format(calNow.getTime());
                                    String sTime = timeFormat.format(calNow.getTime());

                                    // enter fufi
                                    String sType = myApp.sCurType;

                                    // at the moment this is not displayed anywhere
                                    if(sType.equalsIgnoreCase("timIncr")) {
                                        //db.doneTimIncr(sDate, sTrans, iMin, sTime);
                                    }
                                    // this gets displayed in iMode = 1
                                    else if (sType.equalsIgnoreCase("timDecr")) {
                                        db.doneTimDecr(sDate, sTrans, iMin, sTime);
                                    }

                                    myApp.sCurType = "";
                                }

                                ActivityButtons.startActivity(getApplicationContext());
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
            }
        });
    }

    private static ArrayList<String> listTimes;
    static {
        listTimes = new ArrayList<String>();
        listTimes.add("back");
        //listTimes.add("done-x");
        //listTimes.add("done");
        listTimes.add("");
        listTimes.add("3");
        listTimes.add("5");
        listTimes.add("7");
        listTimes.add("10");
        listTimes.add("15");
        listTimes.add("20");
        listTimes.add("30");
        listTimes.add("40");
        listTimes.add("50");
        listTimes.add("60");
        listTimes.add("70");
        listTimes.add("80");
        listTimes.add("90");
        listTimes.add("100");
        listTimes.add("110");
        listTimes.add("120");
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}