package com.better_computer.habitaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StopwatchUtil {

    private static final String STOPWATCH_LAST_TOUCHED_TIME = "StopwatchUtil_STOPWATCH_LAST_TOUCHED_TIME";

    private static final String EVENT_START_TIME = "StopwatchUtil_EVENT_START_TIME";
    private static final String EVENT_STOP_TIME = "StopwatchUtil_EVENT_STOP_TIME";
    private static final String TASK_START_TIME = "StopwatchUtil_TASK_START_TIME";
    private static final String TASK_STOP_TIME = "StopwatchUtil_TASK_STOP_TIME";
    private static final String ENGAGED_START_TIME = "StopwatchUtil_ENGAGED_START_TIME";
    private static final String ENGAGED_LAST_STATUS = "StopwatchUtil_ENGAGED_START_STATUS";
    private static final String ENGAGED_STOP_TIME = "StopwatchUtil_ENGAGED_STOP_TIME";

    private static final String DATE_SESSION_STARTED = "DATE_SESSION_STARTED";
    private static final String DATETIME_SESSION_STARTED = "DATETIME_SESSION_STARTED";

    private static final String DATE_EVENT_STARTED = "DATE_EVENT_STARTED";
    private static final String DATETIME_EVENT_STARTED = "DATETIME_EVENT_STARTED";

    public static long resetEventStartTime(Context context) {
        long time = System.currentTimeMillis();
        setEventStartTime(context, time);
        setEventStopTime(context, -1);
        return time;
    }

    public static long resetTaskStartTime(Context context) {
        long time = System.currentTimeMillis();
        setTaskStartTime(context, time);
        setTaskStopTime(context, -1);
        return time;
    }

    public static long resetEngagedStartTime(Context context, String desc) {
        long time = System.currentTimeMillis();
        setEngagedStartTime(context, time, desc);
        setEngagedStopTime(context, -1);
        return time;
    }

    private static void setEventStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(EVENT_START_TIME, period).apply();
    }

    private static void setTaskStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(TASK_START_TIME, period).apply();
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

    private static long getEventStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(EVENT_START_TIME, -1);
    }

    private static long getTaskStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(TASK_START_TIME, -1);
    }

    public static long getStopwatchLastTouchedTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(STOPWATCH_LAST_TOUCHED_TIME, -1);
    }

    private static long getEngagedStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(ENGAGED_START_TIME, -1);
    }

    public static void setEventStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(EVENT_STOP_TIME, period).apply();
    }

    public static void setTaskStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(TASK_STOP_TIME, period).apply();
    }

    public static void setEngagedStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(ENGAGED_STOP_TIME, period).apply();
    }

    public static long getTaskStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(TASK_STOP_TIME, -1);
    }

    public static long getEventStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(EVENT_STOP_TIME, -1);
    }

    public static long getEngagedStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(ENGAGED_STOP_TIME, -1);
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

    public static long getTaskPassedTime(Context context) {
        long startTime = getTaskStartTime(context);
        long stopTime = getTaskStopTime(context);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setTaskStartTime(context, current);
            setTaskStopTime(context, current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getTaskStartTime(context);
        } else {
            return stopTime - getTaskStartTime(context);
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

    public static void setDateSessionStarted(String sDate, String sDateTime) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit()
                .putString(DATE_SESSION_STARTED, sDate)
                .putString(DATETIME_SESSION_STARTED, sDateTime)
                .apply();
    }

    public static String getDateSessionStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATE_SESSION_STARTED, "");
    }

    public static String getDateTimeSessionStarted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DATETIME_SESSION_STARTED, "");
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
}
