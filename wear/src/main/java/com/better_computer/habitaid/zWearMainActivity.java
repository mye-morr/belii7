/*

package com.better_computer.habitaid;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class WearMainActivity extends Activity {
    private BoxInsetLayout mContainerView;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mGestureDetector;

    /*
    how to fade display e.g. for flash-cards:

        final long FIVE_MINUTES = 1000*60*5;
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };

        handler.postDelayed(r, FIVE_MINUTES);
     */

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container); // This is your existing top level RelativeLayout
        mDismissOverlayView = new DismissOverlayView(WearMainActivity.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Configure a gesture detector
        mGestureDetector = new GestureDetector(WearMainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                mDismissOverlayView.show();
            }
        });


        FragmentManager fragmentManager = getFragmentManager();

        Fragment fragButtons = new FragmentWearButtons();
        Bundle b2 = new Bundle();
        b2.putInt("iActiveFace", 2);
        fragButtons.setArguments(b2);

        fragmentManager.beginTransaction().replace(R.id.container,
                fragButtons).commit();

        //fragmentTransaction.add(R.id.fragment_container, hello, "HELLO");
        //fragmentTransaction.commit();

        /*
        final GridViewPager gridViewPager =
                (GridViewPager) findViewById(R.id.pager);
        gridViewPager.setAdapter(new MyGridPagerAdapter(this, getFragmentManager()));

        gridViewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                gridViewPager.setCurrentItem(1, 2, false);
                gridViewPager.getAdapter().notifyDataSetChanged();
                gridViewPager.removeOnLayoutChangeListener(this);
            }
        });

        gridViewPager.setOffscreenPageCount(0);
        */

/*
    }
*/
/*
    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}

*/