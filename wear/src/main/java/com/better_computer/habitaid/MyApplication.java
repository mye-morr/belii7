package com.better_computer.habitaid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.WearMessage;

import java.util.Random;

public class MyApplication extends Application {

    /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("sCat" + sActiveFace, buttonsData.getCat())
                    .putString("sDelimCaptions" + sActiveFace, buttonsData.getDelimCaptions())
                    .putString("sDelimReplies" + sActiveFace, buttonsData.getDelimReplies())
                    .putString("sDelimPoints" + sActiveFace, buttonsData.getDelimPoints())
                    .commit();
     */

    private static final String CUR_SESH_NUM = "1";
    private static final String TIME_TASK_DUE = "TIME_TASK_DUE";
    private static final String TIME_WHIP_DUE = "TIME_MSG_DUE";
    private static final String NEXT_CARD_MSG = "NEXT_CARD_MSG";
    private static final String FRQ_ENCOURAGE = "FRQ_ENCOURAGE";
    private static final String PTS_CUR = "PTS_CUR";

    public static long iPrefSeconds = 38;
    private static long[] vibrationPattern = {0, 500};
    private Handler handlerIntervalTimer = new Handler();
    private WearMessage wearMessage;

    private static boolean bIsOnTimer = false;
    private static boolean bTimerTurnedOff = false;

    private static int iCtrMissed = 0;

    public volatile static boolean bInDrill = false;
    public volatile static boolean bNewTrans = false;
    public volatile static boolean bNewWork = false;
    public volatile static boolean bNewTask = false;
    public volatile static boolean bImpuls = false;
    public volatile static boolean bTimerTicking = false;
    public volatile static boolean bJustPicked = false;

    public volatile static String sNewCycl = "offt";
    public volatile static String sCurEvent = "";
    public volatile static String sCurType = "";
    public volatile static int iCurTaskTimReq = 0;
    public volatile static long lTimerBase = 0;
    public volatile static long lSeshNum = 0;

    public static long lCountdownNextLib = 5;
    public static long lTimeWhipDue = 0;

    public static boolean bFirstLaunch = true;
    public static boolean bDebug = false;

    // the idea here was that, if the transitions between small tasks were short
    // then it would have a multiplier effect on effective minutes (eg guitar-hero)
    // good in concept but rough in practice because concentration has a limit
    //public int iCountGoodTransitions = 0;

    PowerManager.WakeLock wl;

    final Runnable r = new Runnable() {
        public void run() {

            String sMsg = "";
            String sFrqEncourage = "10;7";
            long[] modVibrationPattern = {0, 2000};
            boolean bIsModVibrationPattern = false;
            MessageData messageData = new MessageData();
            Random rand = new Random();

            Context applicationContext = ActivityButtons.getContextOfApplication();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            WearMessage wearMessage = new WearMessage(applicationContext);

            if(bTimerTurnedOff) {
                handlerIntervalTimer.removeCallbacks(this);
                bTimerTurnedOff = false;
            }
            else {
                // aggrevation
                if (bNewTrans || bNewWork || bNewTask || !sNewCycl.equalsIgnoreCase("offt"))
                {
                    if ((iCtrMissed > 15) &&
                            (iCtrMissed % 3 == 0)) {

                        iCtrMissed++;

                        if (iCtrMissed < 30) {
                            sMsg = prefs.getString("alert_msg_secondary", "alert_msg_secondary");
                            messageData.setText1(sMsg);
                            messageData.setText2("");

                            //subtractPtsCur(5);

                            bIsModVibrationPattern = true;
                        } else {
                            sMsg = prefs.getString("alert_msg_critical", "alert_msg_critical");
                            messageData.setText1(sMsg);
                            messageData.setText2("");

                            //subtractPtsCur(10);

                            bIsModVibrationPattern = true;
                        }

                        //ActivityCard.startActivity(getApplicationContext(), messageData);
                        //ActivityMessage.startActivity(getApplicationContext(), messageData);
                        ActivityMessage.startActivity(getApplicationContext(), messageData, modVibrationPattern);

                    } else {
                        iCtrMissed++;

                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        //-1 - don't repeat
                        final int indexInPatternToRepeat = -1;
                        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                    } // end reached threshold of iCtrMissed

                    // if not in any commitment
                    // goal is not to whip unless curEvent is beyond offt
                } else {
                    lTimeWhipDue = prefs.getLong(TIME_WHIP_DUE, 0);

                    if(System.currentTimeMillis() > lTimeWhipDue)
                    {
                        if (!bInDrill) {
                            lCountdownNextLib--;

                            if (lCountdownNextLib < 1) {
                                sFrqEncourage = prefs.getString(FRQ_ENCOURAGE, "10;7");
                                int iBufSemi = 0;
                                iBufSemi = sFrqEncourage.indexOf(";");

                                int iIncr = 0;
                                int iVaria = 0;

                                if (iBufSemi > 0) {
                                    iIncr = Integer.parseInt(sFrqEncourage.substring(0, iBufSemi).trim());
                                    iVaria = Integer.parseInt(sFrqEncourage.substring(iBufSemi + 1).trim());
                                } else {
                                    iIncr = Integer.parseInt(sFrqEncourage);
                                    iVaria = 0;
                                }

								sMsg = prefs.getString(NEXT_CARD_MSG, "NEXT_CARD_MSG");

								int iBuf = sMsg.indexOf("-=");
								String sKey = sMsg.substring(0, iBuf).trim();
								String sVal = sMsg.substring(iBuf + 2).trim();

								messageData.setText1(sKey);
								messageData.setText2(sVal);
								ActivityCard.startActivity(getApplicationContext(), messageData);

								String sCategory = "";
								if(sNewCycl.equalsIgnoreCase("offt")) {
								    sCategory = "0encourag";
                                }

								wearMessage = new WearMessage(getApplicationContext());
								wearMessage.sendMessage("/fetch-next-card", "0encourag", "");
								// wearmessage update swp from dynarray
								// really this should be player: combined learning + habits

                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                //-1 - don't repeat
                                final int indexInPatternToRepeat = -1;
                                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

                                int iPlusMinus = 1;
                                if (rand.nextDouble() < 0.5) {
                                    iPlusMinus = -1;
                                }
                                lCountdownNextLib = iIncr + Math.round(iPlusMinus * rand.nextDouble() * iVaria);

                            } // end of lCountdownNextLib
                        } // !InDrill
                    }
                }

                handlerIntervalTimer.postDelayed(this, 1000 * iPrefSeconds);
            }
        }
    };

    public boolean isOnTimer() {
        return bIsOnTimer;
    }

    @SuppressLint("InvalidWakeLockTag")
    public void toggleTimer() {

        if(!bIsOnTimer) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl =  pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();

            iCtrMissed = 0;
            vibrationPattern[1] = 500;
            handlerIntervalTimer.post(r);
            bIsOnTimer = true;

            StopwatchUtil.resetEngagedStartTime(this, "maint");

            WearMessage wearMessage = new WearMessage(getApplicationContext());
            wearMessage.sendMessage("/fetch-next-card", "0encourag", "");
        }
        else {
            handlerIntervalTimer.removeCallbacks(r);
            bTimerTurnedOff = true;
            handlerIntervalTimer.post(r);
            wl.release();
            bIsOnTimer = false;

            wearMessage = new WearMessage(getApplicationContext());
            wearMessage.sendMessage("/reset-cards", "", "");
        }
    }

    public void offTimer() {
        try {
            handlerIntervalTimer.removeCallbacks(r);
            bTimerTurnedOff = true;
            handlerIntervalTimer.post(r);

            if (wl != null) {
                wl.release();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void resetMissedPrompt() {
        iCtrMissed = 0;
    }

    public void setTimeTaskDue(long lTimeDue) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit().putLong(TIME_TASK_DUE, lTimeDue).apply();
    }

    public void setTimeWhipDue(long lTimeDue) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit().putLong(TIME_WHIP_DUE, lTimeDue).apply();
    }

    public long getSeshCur() {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return prefs.getLong(CUR_SESH_NUM, 0);
    }

    public void setSeshCur(long lSeshNum) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        prefs.edit().putLong(CUR_SESH_NUM, lSeshNum).apply();
    }

    public int getPtsCur() {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return prefs.getInt(PTS_CUR, 0);
    }

    public void addPtsCur(int iPts) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        int iMinEngaged = prefs.getInt(PTS_CUR, 0);
        iMinEngaged += iPts;
        prefs.edit().putInt(PTS_CUR, iMinEngaged).apply();
    }

    public void subtractPtsCur(int iPts) {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        int iMinEngaged = prefs.getInt(PTS_CUR, 0);
        iMinEngaged -= iPts;
        prefs.edit().putInt(PTS_CUR, iMinEngaged).apply();
    }

    public void resetPtsCur() {
        Context applicationContext = ActivityButtons.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        prefs.edit().putInt(PTS_CUR, 0).apply();
    }

}