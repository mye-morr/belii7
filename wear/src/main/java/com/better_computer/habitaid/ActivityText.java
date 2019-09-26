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
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityText extends WearableActivity {

    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;
    private SharedPreferences prefs;
    private TextView tvText;

    private int mActiveFaceText;
    private String sActiveFaceText;
    private String sText;

    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, ActivityText.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
        mDismissOverlayView = new DismissOverlayView(ActivityText.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Configure a gesture detector
        mGestureDetector = new GestureDetector(ActivityText.this, new GestureDetector.SimpleOnGestureListener() {
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
                        ActivityButtons.startActivity(getApplicationContext());

                        //Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:
                        if (mActiveFaceText < 4) {
                            mActiveFaceText++;
                            prefs.edit().putString("sActiveFaceText", Integer.toString(mActiveFaceText)).commit();
                        }

                        sActiveFaceText = Integer.toString(mActiveFaceText);

                        sText = prefs.getString("sText" + sActiveFaceText, "");
                        tvText.setText(sText);

                        //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                        return true;
                    case 3:



                        //Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT).show();
                        return true;
                    case 4:
                        if (mActiveFaceText > 0) {
                            mActiveFaceText--;
                            prefs.edit().putString("sActiveFaceText", Integer.toString(mActiveFaceText)).commit();
                        }

                        sActiveFaceText = Integer.toString(mActiveFaceText);

                        sText = prefs.getString("sText" + sActiveFaceText, "");
                        tvText.setText(sText);

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

        tvText = (TextView) findViewById(R.id.tvText);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sActiveFaceText = prefs.getString("sActiveFaceText", "2");
        mActiveFaceText = Integer.valueOf(sActiveFaceText);

        sText = prefs.getString("sText" + sActiveFaceText, "");
        tvText.setText(sText);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}
