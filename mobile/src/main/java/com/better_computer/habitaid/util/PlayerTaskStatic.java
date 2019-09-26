package com.better_computer.habitaid.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.better_computer.habitaid.share.WearMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerTaskStatic extends AsyncTask<Void, Void, Integer> {
    private Context context;
    private WearMessage wearMessage;

    private String[] sxItems;
    private Integer[] ixRandIdx;
    private int len;
    private int nxt;
    private int iNumSecsWait;
    private float fNumSecsAns;

    public PlayerTaskStatic(Context context, String[] sxItems, int iNumSecsWait) {

        this.context = context;
        this.sxItems = sxItems;
        this.len = sxItems.length;
        this.ixRandIdx = genRandIdx(this.len);
        this.iNumSecsWait = iNumSecsWait;
        this.fNumSecsAns = (float)(iNumSecsWait / 2.0);

        this.nxt = -1;

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

        String sAction;
        String[] fc_output;

        while(!isCancelled()) {

            String sKeyVal = sxItems[ixRandIdx[++nxt]];

            if (nxt == (len-1)) {
                nxt = -1;
            }

            int iBuf = sKeyVal.indexOf("-=");

            String sKey = sKeyVal.substring(0, iBuf).trim();
            String sVal = sKeyVal.substring(iBuf + 2).trim();

            if(sVal.length() > 0) {
                wearMessage.sendMessage("/start-activity", sKey, "");

                try {
                    Thread.sleep((int)(fNumSecsAns * 1000));

                    wearMessage.sendMessage("/start-activity", sKey, sVal);

                    Thread.sleep(iNumSecsWait * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else { // no second clause

                wearMessage.sendMessage("/start-activity", sKey, "");

                try {
                    Thread.sleep(iNumSecsWait * 1000);
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

    protected Integer[] genRandIdx(int iSize) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < iSize; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list.toArray(new Integer[list.size()]);
    }
}