package com.better_computer.habitaid;

/*
// Configure a gesture detector
mGestureDetector = new GestureDetector(zOldMainActivity.this, new GestureDetector.SimpleOnGestureListener() {

@Override
public void onLongPress(MotionEvent event) {
        mDismissOverlayView.show();
        Log.d(LOG_TAG, " onLongPress: " + event.toString());
        }

@Override
public boolean onFling(MotionEvent e1, MotionEvent e2,
        float velocityX, float velocityY) {

        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
        case 1:
        Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
        return true;
        case 2:
        return true;
        case 3:
        Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT).show();
        return true;
        case 4:
        if (mActiveFace > 0) {
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
*/