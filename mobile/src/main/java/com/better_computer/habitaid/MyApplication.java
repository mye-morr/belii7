package com.better_computer.habitaid;

import android.app.Application;

import com.better_computer.habitaid.util.DynaArray;

/*
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
*/

public class MyApplication extends Application {

    /*
    private CallbackManager callbackManager;
    */
    public DynaArray dynaArray = new DynaArray();
    public boolean bPlayerOn = false;
    public boolean bDrillOn = false;

    @Override
    public void onCreate() {
        super.onCreate();


        /*
        Stetho.initializeWithDefaults(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        */
    }

    /*
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
    */

    public DynaArray getDynaArray() {
        return dynaArray;
    }
}
