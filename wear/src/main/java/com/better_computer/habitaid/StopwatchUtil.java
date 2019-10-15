package com.better_computer.habitaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StopwatchUtil {

    private static final String STOPWATCH_LAST_TOUCHED_TIME = "StopwatchUtil_STOPWATCH_LAST_TOUCHED_TIME";

    private static final String DATE_TRANS_STARTED = "DATE_TRANS_STARTED";
    private static final String DATETIME_TRANS_STARTED = "DATETIME_TRANS_STARTED";

    private static final String DATE_EVENT_STARTED = "DATE_EVENT_STARTED";
    private static final String DATETIME_EVENT_STARTED = "DATETIME_EVENT_STARTED";

    private static final String TRANS_START_TIME = "StopwatchUtil_TRANS_START_TIME";
    private static final String TRANS_STOP_TIME = "StopwatchUtil_TRANS_STOP_TIME";

    private static final String EVENT_START_TIME = "StopwatchUtil_EVENT_START_TIME";
    private static final String EVENT_STOP_TIME = "StopwatchUtil_EVENT_STOP_TIME";

    private static final String ENGAGED_START_TIME = "StopwatchUtil_ENGAGED_START_TIME";
    private static final String ENGAGED_LAST_STATUS = "StopwatchUtil_ENGAGED_START_STATUS";
    private static final String ENGAGED_STOP_TIME = "StopwatchUtil_ENGAGED_STOP_TIME";

    public static long resetTransStartTime(Context context) {
        long time = System.currentTimeMillis();
        setTransStartTime(context, time);
        setTransStopTime(context, -1);
        return time;
    }

    public static long resetEventStartTime(Context context) {
        long time = System.currentTimeMillis();
        setEventStartTime(context, time);
        setEventStopTime(context, -1);
        return time;
    }

    public static long resetEngagedStartTime(Context context, String desc) {
        long time = System.currentTimeMillis();
        setEngagedStartTime(context, time, desc);
        setEngagedStopTime(context, -1);
        return time;
    }

    private static void setTransStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(TRANS_START_TIME, period).apply();
    }

    private static void setEventStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(EVENT_START_TIME, period).apply();
    }

    public static void setStopwatchLastTouchedTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(STOPWATCH_LAST_TOUCHED_TIME, period).apply();
    }

    private static void setEngagedStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putLong(ENGAGED_START_TIME, period).apply();
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

    private static long getTransStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(TRANS_START_TIME, -1);
    }

    private static long getEventStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(EVENT_START_TIME, -1);
    }

    public static long getStopwatchLastTouchedTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(STOPWATCH_LAST_TOUCHED_TIME, -1);
    }

    private static long getEngagedStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(ENGAGED_START_TIME, -1);
    }

    public static void setTransStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(TRANS_STOP_TIME, period).apply();
    }

    public static void setEventStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(EVENT_STOP_TIME, period).apply();
    }

    public static void setEngagedStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(ENGAGED_STOP_TIME, period).apply();
    }

    public static long getTransStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(TRANS_STOP_TIME, -1);
    }

    public static long getEventStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(EVENT_STOP_TIME, -1);
    }

    public static long getEngagedStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(ENGAGED_STOP_TIME, -1);
    }

    public static long getTransPassedTime(Context context) {
        long startTime = getTransStartTime(context);
        long stopTime = getTransStopTime(context);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setTransStartTime(context, current);
            setTransStopTime(context, current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getTransStartTime(context);
        } else {
            return stopTime - getTransStartTime(context);
        }
    }

    public static long getEventPassedTime(Context context) {
        long startTime = getEventStartTime(context);
        long stopTime = getEventStopTime(context);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setEventStartTime(context, current);
            setEventStopTime(context, current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getEventStartTime(context);
        } else {
            return stopTime - getEventStartTime(context);
        }
    }

    public static long getEngagedLastTouchPassedTime(Context context) {
        long startTime = getStopwatchLastTouchedTime(context);

        long current = System.currentTimeMillis();
        return current - startTime;
    }

    public static long getEngagedPassedTime(Context context) {
        long startTime = getEngagedStartTime(context);
        long stopTime = getEngagedStopTime(context);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setEngagedStartTime(context, current);
            setEngagedStopTime(context, current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getEngagedStartTime(context);
        } else {
            return stopTime - getEngagedStartTime(context);
        }
    }

    public static void setDateTransStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_TRANS_STARTED, sDate)
                .putString(DATETIME_TRANS_STARTED, sDateTime)
                .apply();
    }

    public static void setDateEventStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_EVENT_STARTED, sDate)
                .putString(DATETIME_EVENT_STARTED, sDateTime)
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

    public static String getDateEventStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_EVENT_STARTED, "");
    }

    public static String getDateTimeEventStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_EVENT_STARTED, "");
    }
}
