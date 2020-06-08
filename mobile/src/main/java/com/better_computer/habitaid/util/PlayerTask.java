package com.better_computer.habitaid.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.better_computer.habitaid.share.WearMessage;

import java.util.Random;

public class PlayerTask extends AsyncTask<Void, Void, Integer> {

    private Context context;
    private DynaArray dynaArray;
    private int iMinBreak;
    private int iVaria;
    private WearMessage wearMessage;

    public PlayerTask(Context context, DynaArray dynaArray, String sRate) {

        int iBufSemi = 0;
        iBufSemi = sRate.indexOf(";");

        if(iBufSemi > 0) {
            this.iMinBreak = Integer.parseInt(sRate.substring(0,iBufSemi).trim());
            this.iVaria = Integer.parseInt(sRate.substring(iBufSemi+1).trim());
        }
        else {
            this.iMinBreak = Integer.parseInt(sRate);
            this.iVaria = 0;
        }

        this.context = context;
        this.dynaArray = dynaArray;
        this.wearMessage = new WearMessage(context);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Void... params) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        while(!isCancelled()) {

            String sKeyVal = dynaArray.getRandomElementNew("");

            int iBuf = sKeyVal.indexOf("-=");

            if(iBuf > 0) {

                String sKey = sKeyVal.substring(0, iBuf).trim();
                String sVal = sKeyVal.substring(iBuf + 2).trim();

                wearMessage.sendMessage("/start-activity", sKey, "");

                try {
                    Thread.sleep(25 * 1000);

                    wearMessage.sendMessage("/start-activity", sKey, sVal);

                    long lMinSleep = iMinBreak;

                    if(iVaria > 0) {
                        Random rand = new Random();

                        int iPlusMinus = 1;
                        if (rand.nextDouble() < 0.5) {
                            iPlusMinus = -1;
                        }

                        lMinSleep += Math.round(iPlusMinus * rand.nextDouble() * iVaria);
                    }

                    Thread.sleep(lMinSleep * 60 * 1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }

            else { // no -= delimiter

                wearMessage.sendMessage("/start-activity", sKeyVal, "");

                try {
                    long lMinSleep = iMinBreak;

                    if(iVaria > 0) {
                        Random rand = new Random();

                        int iPlusMinus = 1;
                        if (rand.nextDouble() < 0.5) {
                            iPlusMinus = -1;
                        }

                        lMinSleep += Math.round(iPlusMinus * rand.nextDouble() * iVaria);
                    }

                    Thread.sleep(lMinSleep * 60 * 1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        wl.release();
        return 1;
    }

    @Override
    protected void onCancelled(Integer id) {
    }

    @Override
    protected void onPostExecute(Integer id) {
    }

}