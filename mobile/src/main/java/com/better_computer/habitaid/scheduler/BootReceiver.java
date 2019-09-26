package com.better_computer.habitaid.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.better_computer.habitaid.SplashActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i1 = new Intent(context, SplashActivity.class);
        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i1.putExtra("PERMISSION_FOR_SERVICE","_");
        context.startActivity(i1);

            /*
            Intent i1 = new Intent(context, SchedulerService.class);
            context.startService(i1);
            */

            /* if we wanted to have a parallel service
            Intent i2 = new Intent(context, SchedulerServiceDaily.class);
            context.startService(i2);
            */
    }
}