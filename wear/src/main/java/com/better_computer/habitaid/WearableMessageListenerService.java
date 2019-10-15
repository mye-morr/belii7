package com.better_computer.habitaid;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.better_computer.habitaid.share.ButtonsData;
import com.better_computer.habitaid.share.LibraryData;
import com.better_computer.habitaid.share.MessageData;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableMessageListenerService extends WearableListenerService {

    private static final String LOG_TAG = WearableMessageListenerService.class.getSimpleName();
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String NEXT_CARD_MSG = "NEXT_CARD_MSG";

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d(LOG_TAG, "onMessageReceived : " + event);
        if (event.getPath().equals(START_ACTIVITY_PATH)) {
            // looks like double conversion to json string
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            ActivityMessage.startActivity(this, messageData);
        }
        else if (event.getPath().equalsIgnoreCase("/no-time")) {
            Context context = getApplicationContext();
            ComponentName yourWatchFace = new ComponentName("com.better_computer.blankwatchface", "MyWatchFace");
            Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                    .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, yourWatchFace)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else if (event.getPath().equalsIgnoreCase("/set-timer")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            ((MyApplication)getApplication()).iPrefSeconds = Integer.valueOf(messageData.getText1());
        }
        else if (event.getPath().equalsIgnoreCase("/set-buttons")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            ButtonsData buttonsData = ButtonsData.toButtonsData(jsonString);

            String sActiveFace = buttonsData.getActiveFace();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putString("sCat" + sActiveFace, buttonsData.getCat())
                    .putString("sDelimCaptions" + sActiveFace, buttonsData.getDelimCaptions())
                    .putString("sDelimReplies" + sActiveFace, buttonsData.getDelimReplies())
                    .putString("sDelimPoints" + sActiveFace, buttonsData.getDelimPoints())
                    .commit();
        }
        else if (event.getPath().equalsIgnoreCase("/set-pref")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putString(messageData.getText1(), messageData.getText2())
                    .commit();
        }
        else if (event.getPath().equalsIgnoreCase("/store-next-card")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putString(NEXT_CARD_MSG, messageData.getText1())
                    .apply();
        }
        else if(event.getPath().equalsIgnoreCase("/set-single-library")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);

            LibraryData libraryData = LibraryData.toLibraryData(jsonString);
            String sCat = libraryData.getDelimCat();
            String sElements = libraryData.getDelimElements();
            String sPts = libraryData.getDelimPoints();
            String sReplies = libraryData.getDelimReplies();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString(sCat, sElements).commit();
            prefs.edit().putString(sCat+"_Pts", sPts).commit();
            if(sReplies.split(";").length > 0) {
                prefs.edit().putString(sCat + "_Replies", sReplies).commit();
            }
        }
        else if(event.getPath().equalsIgnoreCase("/set-library")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);

            LibraryData libraryData = LibraryData.toLibraryData(jsonString);
            String[] sxCat = libraryData.getDelimCat().split("\\|");
            String[] sxElements = libraryData.getDelimElements().split("\\|");
            String[] sxPts = libraryData.getDelimPoints().split("\\|");
            String[] sxReplies = libraryData.getDelimReplies().split("\\|");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            for (int i=0; i<sxCat.length; i++) {
                prefs.edit().putString(sxCat[i], sxElements[i]).apply();
                prefs.edit().putString(sxCat[i] + "_Pts", sxPts[i]).apply();
                if(sxReplies[i].split(";").length > 0) {
                    prefs.edit().putString(sxCat[i] + "_Replies", sxReplies[i]).apply();
                }
            }

            Database db = new Database(this);
            db.clearPrj();
            db.clearSmTas();
        }
        else if(event.getPath().equalsIgnoreCase("/done-drilling")) {
            ((MyApplication)getApplication()).bInDrill = false;
        }

        /*
        else if(event.getPath().equalsIgnoreCase("/set-prjsmtas")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);

            Database db = new Database(this);
            db.clearPrj();
            db.clearSmTas();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putString("sDelimPrj", messageData.getText1())
                    .putString("sDelimSmTas", messageData.getText2())
                    .commit();
        }
        */

    }
}