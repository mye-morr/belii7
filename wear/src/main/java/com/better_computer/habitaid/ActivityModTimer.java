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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.PressedData;

public class ActivityModTimer extends Activity{

    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;

    private SeekBar fSeekBar;
    private SeekBar fSeekBar2;
    private Button btnPostWhip;

    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, ActivityModTimer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modtimer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                fSeekBar = (SeekBar) stub.findViewById(R.id.seekBar);
                fSeekBar2 = (SeekBar) stub.findViewById(R.id.seekBar2);
                btnPostWhip = (Button) findViewById(R.id.btnPostWhip);

                fSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        double pct = progress / 100.0;
                        int iSecs = (int)(pct * 114);

                        ((MyApplication)getApplication()).iPrefSeconds = iSecs;
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

                        double pct = progress / 100.0;
                        int iSecs = (int)(pct * 500);

                        ((MyApplication)getApplication()).iPrefSeconds = iSecs;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                btnPostWhip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityInput.startActivity(getApplicationContext(), "postwhip");
                    }
                });

                mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
                mDismissOverlayView = new DismissOverlayView(ActivityModTimer.this);
                mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));

                // Configure a gesture detector
                mGestureDetector = new GestureDetector(ActivityModTimer.this, new GestureDetector.SimpleOnGestureListener() {

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