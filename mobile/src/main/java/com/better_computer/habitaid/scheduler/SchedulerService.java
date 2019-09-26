package com.better_computer.habitaid.scheduler;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.better_computer.habitaid.MyApplication;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.MessageHelper;

public class SchedulerService extends Service {

    private AlarmReceiver alarmReceiver;
    private long interval = 1000 * 45; //m/ every 45 seconds (in milliseconds)
    private MessageHelper messageHelper;

    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final String[] allRequestedPermissions = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };

    public SchedulerService() {
        DatabaseHelper.init(this);

            this.messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);
            this.alarmReceiver = new AlarmReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmReceiver.setAlarm(this, interval);
        return START_STICKY;
    }

    private boolean checkAllRequestedPermissions() {
        for (String permission : allRequestedPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (checkAllRequestedPermissions()) {
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}