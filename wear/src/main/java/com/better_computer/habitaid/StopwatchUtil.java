package com.better_computer.habitaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StopwatchUtil {

    private static final String STOPWATCH_LAST_TOUCHED_TIME = "StopwatchUtil_STOPWATCH_LAST_TOUCHED_TIME";

    private static final String DATE_TODAY_STARTED = "DATE_TODAY_STARTED";
    private static final String DATETIME_TODAY_STARTED = "DATETIME_TODAY_STARTED";

    private static final String DATE_TRANS_STARTED = "DATE_TRANS_STARTED";
    private static final String DATETIME_TRANS_STARTED = "DATETIME_TRANS_STARTED";

    private static final String DATE_EVENT_STARTED = "DATE_EVENT_STARTED";
    private static final String DATETIME_EVENT_STARTED = "DATETIME_EVENT_STARTED";

    private static final String DATE_CALM_STARTED = "DATE_CALM_STARTED";
    private static final String DATETIME_CALM_STARTED = "DATETIME_CALM_STARTED";

    private static final String TODAY_START_TIME = "StopwatchUtil_TODAY_START_TIME";
    private static final String TRANS_START_TIME = "StopwatchUtil_TRANS_START_TIME";
    private static final String TRANS_STOP_TIME = "StopwatchUtil_TRANS_STOP_TIME";
    private static final String EVENT_START_TIME = "StopwatchUtil_EVENT_START_TIME";
    private static final String EVENT_STOP_TIME = "StopwatchUtil_EVENT_STOP_TIME";
    private static final String ENGAGED_START_TIME = "StopwatchUtil_ENGAGED_START_TIME";
    private static final String ENGAGED_STOP_TIME = "StopwatchUtil_ENGAGED_STOP_TIME";
    private static final String CALM_START_TIME = "StopwatchUtil_CALM_START_TIME";
    private static final String CALM_STOP_TIME = "StopwatchUtil_CALM_STOP_TIME";

    private static final String ENGAGED_LAST_STATUS = "StopwatchUtil_ENGAGED_START_STATUS";

    private static void setTime(Context context, String sConst, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(sConst, period).apply();
    }

    public static long resetStartTime(Context context, String sConstStart, String sConstStop) {
        long time = System.currentTimeMillis();
        setTime(context,sConstStart,time);
        setTime(context,sConstStop,-1);
        return time;
    }

    private static long getTime(Context context, String sConst) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(sConst, -1);
    }

    public static long getPassedTime(Context context, String sConstStart, String sConstStop) {
        long startTime = getTime(context, sConstStart);
        long stopTime = getTime(context, sConstStop);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setTime(context,sConstStart,current);
            setTime(context,sConstStop,current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getTime(context, sConstStart);
        } else {
            return stopTime - getTime(context, sConstStart);
        }
    }

    public static void setTodayStartTime(Context context, long period) { setTime(context,TODAY_START_TIME,period); }
    private static long getTodayStartTime(Context context) { return getTime(context, TODAY_START_TIME);}
    public static long getTodayPassedTime(Context context) {
        long current = System.currentTimeMillis();
        return current - getTime(context, TODAY_START_TIME);
    }

    public static void setTransStartTime(Context context, long period) { setTime(context,TRANS_START_TIME,period); }
    public static void setTransStopTime(Context context, long period) { setTime(context,TRANS_STOP_TIME,period); }
    public static void setEventStartTime(Context context, long period) { setTime(context,EVENT_START_TIME,period); }
    public static void setEventStopTime(Context context, long period) { setTime(context,EVENT_STOP_TIME,period); }
    public static void setEngagedStartTime(Context context, long period) { setTime(context,ENGAGED_START_TIME,period); }
    public static void setEngagedStopTime(Context context, long period) { setTime(context,ENGAGED_STOP_TIME,period); }
    public static void setCalmStartTime(Context context, long period) { setTime(context,CALM_START_TIME,period); }
    public static void setCalmStopTime(Context context, long period) { setTime(context,CALM_STOP_TIME,period); }
    public static void setStopwatchLastTouchedTime(Context context, long period) { setTime(context,STOPWATCH_LAST_TOUCHED_TIME,period); }

    public static long resetTransStartTime(Context context) { return resetStartTime(context, TRANS_START_TIME, TRANS_STOP_TIME); }
    public static long resetEventStartTime(Context context) { return resetStartTime(context, EVENT_START_TIME, EVENT_STOP_TIME); }
    public static long resetCalmStartTime(Context context) { return resetStartTime(context, CALM_START_TIME, CALM_STOP_TIME); }

    private static long getTransStartTime(Context context) { return getTime(context, TRANS_START_TIME);}
    private static long getTransStopTime(Context context) { return getTime(context, TRANS_STOP_TIME);}
    private static long getEventStartTime(Context context) { return getTime(context, EVENT_START_TIME);}
    private static long getEventStopTime(Context context) { return getTime(context, EVENT_STOP_TIME);}
    private static long getEngagedStartTime(Context context) { return getTime(context, ENGAGED_START_TIME);}
    private static long getEngagedStopTime(Context context) { return getTime(context, ENGAGED_STOP_TIME);}
    private static long getCalmStartTime(Context context) { return getTime(context, CALM_START_TIME);}
    private static long getCalmStopTime(Context context) { return getTime(context, CALM_STOP_TIME);}
    public static long getStopwatchLastTouchedTime(Context context) { return getTime(context, STOPWATCH_LAST_TOUCHED_TIME);}

    public static long getTransPassedTime(Context context) { return getPassedTime(context, TRANS_START_TIME, TRANS_STOP_TIME);}
    public static long getEventPassedTime(Context context) { return getPassedTime(context, EVENT_START_TIME, EVENT_STOP_TIME);}
    public static long getEngagedPassedTime(Context context) { return getPassedTime(context, ENGAGED_START_TIME, ENGAGED_STOP_TIME);}
    public static long getCalmPassedTime(Context context) { return getPassedTime(context, CALM_START_TIME, CALM_STOP_TIME);}


    public static long resetEngagedStartTime(Context context, String desc) {
        long time = System.currentTimeMillis();
        setEngagedStartTime(context, time, desc);
        setEngagedStopTime(context, -1);
        return time;
    }

    private static void setEngagedStartTime(Context context, long period, String desc) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putLong(ENGAGED_START_TIME, period)
                .putString(ENGAGED_LAST_STATUS, desc)
                .apply();
    }

    public static String getEngagedLastStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(ENGAGED_LAST_STATUS, "");
    }

    public static long getEngagedLastTouchPassedTime(Context context) {
        long startTime = getStopwatchLastTouchedTime(context);

        long current = System.currentTimeMillis();
        return current - startTime;
    }

    public static void setDateTransStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_TRANS_STARTED, sDate)
                .putString(DATETIME_TRANS_STARTED, sDateTime)
                .apply();
    }

    public static String getDateTransStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_TRANS_STARTED, "");
    }

    public static String getDateTimeTransStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_TRANS_STARTED, "");
    }

    public static void setDateEventStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_EVENT_STARTED, sDate)
                .putString(DATETIME_EVENT_STARTED, sDateTime)
                .apply();
    }

    public static String getDateEventStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_EVENT_STARTED, "");
    }

    public static String getDateTimeEventStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_EVENT_STARTED, "");
    }

    public static void setDateCalmStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_CALM_STARTED, sDate)
                .putString(DATETIME_CALM_STARTED, sDateTime)
                .apply();
    }

    public static String getDateCalmStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_CALM_STARTED, "");
    }

    public static String getDateTimeCalmStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_CALM_STARTED, "");
    }


    public static void setDateTodayStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_TODAY_STARTED, sDate)
                .putString(DATETIME_TODAY_STARTED, sDateTime)
                .apply();
    }

    public static String getDateTodayStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_TODAY_STARTED, "");
    }

    public static String getDateTimeTodayStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_TODAY_STARTED, "");
    }

}
