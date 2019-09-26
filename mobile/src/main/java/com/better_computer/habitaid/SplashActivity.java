package com.better_computer.habitaid;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.os.Bundle;
import android.util.Log;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.scheduler.SchedulerService;

/*
https://developer.android.com/training/permissions/requesting.html
 */
public class SplashActivity extends Activity {

    private static boolean bPermission = false;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final String[] allRequestedPermissions = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bPermission = false;
        if(getIntent().hasExtra("PERMISSION_FOR_SERVICE")) {
            bPermission = true;
        }
        if (!checkAllRequestedPermissions()) {
            ActivityCompat.requestPermissions(this, allRequestedPermissions, MY_PERMISSIONS_REQUEST);
        }
        else {
            startMainActivity();
        }
    }

    private boolean checkAllRequestedPermissions() {
        for (String permission : allRequestedPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (checkAllRequestedPermissions()) {
                        startMainActivity();
                }
                else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startMainActivity() {
        if (bPermission) {
            //Initialize database helper
            DatabaseHelper.init(getApplicationContext());
            startService(new Intent(this, SchedulerService.class));
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
