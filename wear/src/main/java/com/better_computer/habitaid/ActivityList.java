package com.better_computer.habitaid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.PressedData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class ActivityList extends Activity{

    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;
    private ListView mListViewMain;
    private String sListName;
    private String[] sxItems;
    private String[] sxPts;
    private String[] sxReplies;
    private String sReply;
    private boolean bNoReplies;

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private PressedData pressedData;
    private Database db;

    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, ActivityList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pressedData = new PressedData();

        Intent intent = getIntent();
        sListName = intent.getStringExtra("sListName");
        sxItems = intent.getStringExtra("sDelimItems").split(";");
        sxPts = intent.getStringExtra("sDelimPts").split(";");
        sReply = intent.getStringExtra("sReply");

        bNoReplies = false;
        if(intent.hasExtra("sDelimReplies")) {
            String sReplies = intent.getStringExtra("sDelimReplies") + ";.;";
            sxReplies = sReplies.split(";");
        }
        else {
            bNoReplies = true;
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                final MyApplication myApp = (MyApplication)getApplication();
                db = new Database(getApplicationContext());

                mListViewMain = (ListView) stub.findViewById(R.id.list_main);

                final String[] fsxItems = sxItems;

                mListViewMain.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.listview_row, android.R.id.text1, fsxItems));

                mListViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String sItem = mListViewMain.getItemAtPosition(i).toString();

                        if(sListName.equalsIgnoreCase("comwork")) {
                            // also resets the task start time
                            ActivityInput.startActivity(getApplicationContext(), "comwork", sItem, sxReplies[i]);
                        }
                        else if(sListName.equalsIgnoreCase("comtas")) {
                            // also resets the task start time
                            ActivityInput.startActivity(getApplicationContext(), "comtas", sItem, sxReplies[i]);
                        }
                        else if(sListName.equalsIgnoreCase("comtrans")) {
                            // also resets the task start time
                            ActivityInput.startActivity(getApplicationContext(), "comtrans", sItem, sxReplies[i]);
                        }
                        else if(sListName.equalsIgnoreCase("l0st")) {
                            // also resets the task start time
                            ActivityInput.startActivity(getApplicationContext(), "l0st", sItem, sxReplies[i]);
                        }
                        else if(sListName.equalsIgnoreCase("intf")
                                || sListName.equalsIgnoreCase("spacd")) {

                            Calendar calNow = Calendar.getInstance();

                            String sDate = dateFormat.format(calNow.getTime());
                            String sTime = timeFormat.format(calNow.getTime());

                            db.doneTimDecr(sDate, sListName, parseInt(sItem), sTime);
                            myApp.resetMissedPrompt();
                            ActivityButtons.startActivity(getApplicationContext());
                        }
                        else {
                            if (!bNoReplies) {
                                String sFoo = sxReplies[i].trim();
                                if (sFoo.length() > 0) {
                                    Toast.makeText(getApplicationContext(), sFoo, Toast.LENGTH_LONG).show();
                                }
                            }

                            long passedTime = StopwatchUtil.getEventPassedTime(getApplicationContext());
                            long passedSecs = passedTime / 1000;
                            int iPassedMin = (int) Math.round(passedSecs / 60.0);

                            Calendar calNow = Calendar.getInstance();

                            String sDate = dateFormat.format(calNow.getTime());
                            String sTime = timeFormat.format(calNow.getTime());

                            /*
                            if(sReply.equalsIgnoreCase("numIncr"))
                            {
                                db.doneGooha(sDate, sListName, sTime);
                            }
                            else if (sReply.equalsIgnoreCase("numDecr")) {
                                db.doneBadha(sDate, sListName, sTime);
                            }
                            else {
                            */

                            // impulses would never happen in a list
                            // either its a point-value or an event
                            int iCheck = Integer.parseInt(sxPts[i]);
                            if (iCheck == 0) {
                                db.doneEvent(
                                        sDate, sItem,
                                        0,
                                        iCheck,
                                        StopwatchUtil.getDateTimeEventStarted(getApplicationContext()),
                                        sTime
                                );
                            }
                            else {
                                db.addPts(sDate, iCheck, sItem, iPassedMin);

                                // ^ more anxieties
                                // myApp.addPtsCur(iCheck);
                            }

                            myApp.resetMissedPrompt();
                            ActivityButtons.startActivity(getApplicationContext());
                        }
                    }
                });

                mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
                mDismissOverlayView = new DismissOverlayView(ActivityList.this);
                mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));

                // Configure a gesture detector
                mGestureDetector = new GestureDetector(ActivityList.this, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {

                        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                            case 1:
                                //Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
                                return true;
                            case 2:
                                //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                                return true;
                            case 3:
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

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}