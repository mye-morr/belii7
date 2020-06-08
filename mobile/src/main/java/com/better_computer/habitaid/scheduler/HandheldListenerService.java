package com.better_computer.habitaid.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.better_computer.habitaid.MyApplication;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.data.core.Events;
import com.better_computer.habitaid.data.core.EventsHelper;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.PressedData;
import com.better_computer.habitaid.share.SessionData;
import com.better_computer.habitaid.share.EventData;
import com.better_computer.habitaid.share.WearMessage;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class HandheldListenerService extends WearableListenerService {

    private static final String LOG_TAG = HandheldListenerService.class.getSimpleName();
    private WearMessage wearMessage;
    private ScheduleHelper scheduleHelper;

    @Override
    public void onMessageReceived(MessageEvent event) {

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);

        if(event.getPath().equalsIgnoreCase("/done-event")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            EventData eventData = EventData.toEventData(jsonString);

            Events trans = new Events();
            trans.setSDate(eventData.getDate());
            trans.setSName(eventData.getName());
            trans.setITimDur(parseInt(eventData.getTimDur()));
            trans.setIPtsVal(parseInt(eventData.getPtsVal()));
            trans.setIImp(parseInt(eventData.getImp()));
            trans.setSDtTimStr(eventData.getDtTimStr());
            trans.setSTimEnd(eventData.getTimEnd());

            DatabaseHelper.getInstance().getHelper(EventsHelper.class).createOrUpdate(trans);
        }
        else if(event.getPath().equalsIgnoreCase("/fetch-next-card")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sCategory = messageData.getText1();

            Context context = getApplicationContext();
            MyApplication myApp = ((MyApplication) getApplication());
            String sNextCard = "";
            sNextCard = myApp.dynaArray.getRandomElementNew(sCategory);
            wearMessage = new WearMessage(context);
            wearMessage.sendMessage("/store-next-card", sNextCard, "");
        }
        else if(event.getPath().equalsIgnoreCase("/reset-cards")) {
            MyApplication myApp = ((MyApplication) getApplication());
            myApp.dynaArray.init();
        }
        else if(event.getPath().equalsIgnoreCase("/prepare-ack1")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();
            String sFrqKeyMsg = "4;2";

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));

            Schedule schedule = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).get(keys);

            schedule.set_frame("inactive");
            schedule.set_state("inactive");
            schedule.setPrepCount("1");
            scheduleHelper.update(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/prepare-ack2")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();
            String sFrqKeyMsg = "4;2";

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));

            Schedule schedule = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).get(keys);

            schedule.set_frame("completed");
            schedule.set_state("active");
            schedule.setPrepCount("2");
            schedule.setNextExecute(schedule.getNextDue());
            scheduleHelper.update(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/postpone-sched")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();
            String sFrqKeyMsg = "4;2";

            int iBufSemi = 0;
            iBufSemi = sFrqKeyMsg.indexOf(";");

            int iIncr = Integer.parseInt(sFrqKeyMsg.substring(0,iBufSemi).trim());
            int iVaria = Integer.parseInt(sFrqKeyMsg.substring(iBufSemi+1).trim());

            Random rand = new Random();

            int iPlusMinus = 1;
            if (rand.nextDouble() < 0.5) {
                iPlusMinus = -1;
            }

            iIncr += Math.round(iPlusMinus * rand.nextDouble() * iVaria);
            Calendar instCal = Calendar.getInstance();
            instCal.add(Calendar.MINUTE, iIncr);

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));

            Schedule schedule = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).get(keys);

            schedule.getNextExecute().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
            schedule.getNextExecute().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));
            schedule.getNextExecute().set(Calendar.DAY_OF_MONTH, instCal.get(Calendar.DAY_OF_MONTH));
            scheduleHelper.update(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/cancel-sched")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);

            String sTask = messageData.getText1();

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));
            DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).delete(keys);
        }
        else if(event.getPath().equalsIgnoreCase("/reset-sched")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));

            Schedule schedule = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).get(keys);

            schedule.set_frame("inactive");
            schedule.set_state("inactive");

            scheduleHelper.update(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/new-toda")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();
            String sFrqKeyMsg = messageData.getText2();

            int iIncr = Integer.parseInt(sFrqKeyMsg);
            int iVaria = (int)Math.round(0.3 * iIncr);

            Random rand = new Random();

            int iPlusMinus = 1;
            if (rand.nextDouble() < 0.5) {
                iPlusMinus = -1;
            }

            iIncr += Math.round(iPlusMinus * rand.nextDouble() * iVaria);
            Calendar instCal = Calendar.getInstance();
            instCal.add(Calendar.MINUTE, iIncr);

            Schedule schedule = new Schedule();
            schedule.setReceiver("");
            schedule.setReceiverName("");

            schedule.setCategory("ontrack");
            schedule.setSubcategory("Toda");

            schedule.getNextDue().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
            schedule.getNextDue().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));

            schedule.setRemindInterval("4");

            schedule.set_frame("");
            schedule.set_state("active");
            schedule.setPrepCount("0");
            schedule.setRepeatEnable("false");

            schedule.setNextExecute(schedule.getNextDue());
            schedule.setMessage(sTask);

            // technically returns boolean, but no UI to flag if .. :-S
            DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).createOrUpdate(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/cycle")) {

            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);
            String sTask = messageData.getText1();
            String sFrqKeyMsg = messageData.getText2();

            int iBufSemi = 0;
            iBufSemi = sFrqKeyMsg.indexOf(";");

            int iIncr = Integer.parseInt(sFrqKeyMsg.substring(0,iBufSemi).trim());
            int iVaria = Integer.parseInt(sFrqKeyMsg.substring(iBufSemi+1).trim());

            Random rand = new Random();

            int iPlusMinus = 1;
            if (rand.nextDouble() < 0.5) {
                iPlusMinus = -1;
            }

            iIncr += Math.round(iPlusMinus * rand.nextDouble() * iVaria);
            Calendar instCal = Calendar.getInstance();
            instCal.add(Calendar.MINUTE, iIncr);

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "category", SearchEntry.Search.EQUAL, "ontrack"));
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcategory", SearchEntry.Search.EQUAL, "Monit"));
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));

            Schedule schedule = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).get(keys);

            schedule.getNextExecute().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
            schedule.getNextExecute().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));
            schedule.getNextExecute().set(Calendar.DAY_OF_MONTH, instCal.get(Calendar.DAY_OF_MONTH));
            scheduleHelper.update(schedule);
        }
        else if(event.getPath().equalsIgnoreCase("/toggle-player")) {
            Context context = getApplicationContext();
            MyApplication myApp = ((MyApplication) getApplication());
            if (myApp.bPlayerOn) {
                myApp.bPlayerOn = false;
                PlayerService.stopService(getApplicationContext());

                WearMessage wearMsg = new WearMessage(this);
                wearMsg.sendSignal("/start-timer");

            } else {

                SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

                String sql = "SELECT DISTINCT (cat || ';' || subcat || ';' || wtcat) as foo FROM core_tbl_nonsched WHERE _state='active' ORDER BY cat,subcat";

                String foo = "";
                List<String> listCatSubcat = new ArrayList<String>();
                try {
                    Cursor cursor = database.rawQuery(sql, new String[0]);
                    if (cursor.moveToFirst()) {
                        do {
                            foo = cursor.getString(0);
                            listCatSubcat.add(cursor.getString(0));
                        } while (cursor.moveToNext());
                    }

                    //fix - android.database.CursorWindowAllocationException Start
                    cursor.close();
                    //fix - android.database.CursorWindowAllocationException End
                } catch (Exception e) {
                    e.printStackTrace();
                }

                myApp.dynaArray.init();
                int iCount = 0;

                for (String s : listCatSubcat) {
                    String sCat = "";
                    String sSubcat = "";
                    String sWtcat = "";

                    String[] sxTokens = s.split(";");
                    sCat = sxTokens[0];
                    sSubcat = sxTokens[1];
                    sWtcat = sxTokens[2];

                    iCount = 0;
                    List<Pair> listWtContent = new ArrayList<Pair>();

                    sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                            + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "' AND _state='active'";
                    try {
                        Cursor cursor = database.rawQuery(sql, new String[0]);
                        if (cursor.moveToFirst()) {
                            do {
                                listWtContent.add(new Pair(2, cursor.getString(0)));
                                iCount++;
                            } while (cursor.moveToNext());
                        }

                        //fix - android.database.CursorWindowAllocationException Start
                        cursor.close();
                        //fix - android.database.CursorWindowAllocationException End
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myApp.dynaArray.addContributingArrayNew(
                            listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
                }

                myApp.bPlayerOn = true;
                PlayerService.startService(context, "2;1");
            }
        }
        else if(event.getPath().equalsIgnoreCase("/toggle-drill")) {
            Context context = getApplicationContext();
            MyApplication myApp = ((MyApplication) getApplication());
            if (myApp.bPlayerOn) {
                myApp.bPlayerOn = false;
                PlayerService.stopService(getApplicationContext());

                WearMessage wearMsg = new WearMessage(this);
                wearMsg.sendSignal("/start-timer");

            } else {

                SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

                String sql = "SELECT DISTINCT (cat || ';' || subcat || ';' || wtcat) as foo FROM core_tbl_nonsched WHERE _state='active' ORDER BY cat,subcat";

                String foo = "";
                List<String> listCatSubcat = new ArrayList<String>();
                try {
                    Cursor cursor = database.rawQuery(sql, new String[0]);
                    if (cursor.moveToFirst()) {
                        do {
                            foo = cursor.getString(0);
                            listCatSubcat.add(cursor.getString(0));
                        } while (cursor.moveToNext());
                    }

                    //fix - android.database.CursorWindowAllocationException Start
                    cursor.close();
                    //fix - android.database.CursorWindowAllocationException End
                } catch (Exception e) {
                    e.printStackTrace();
                }

                myApp.dynaArray.init();
                int iCount = 0;

                for (String s : listCatSubcat) {
                    String sCat = "";
                    String sSubcat = "";
                    String sWtcat = "";

                    String[] sxTokens = s.split(";");
                    sCat = sxTokens[0];
                    sSubcat = sxTokens[1];
                    sWtcat = sxTokens[2];

                    iCount = 0;
                    List<Pair> listWtContent = new ArrayList<Pair>();

                    sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                            + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "' AND _state='active'";
                    try {
                        Cursor cursor = database.rawQuery(sql, new String[0]);
                        if (cursor.moveToFirst()) {
                            do {
                                listWtContent.add(new Pair(2, cursor.getString(0)));
                                iCount++;
                            } while (cursor.moveToNext());
                        }

                        //fix - android.database.CursorWindowAllocationException Start
                        cursor.close();
                        //fix - android.database.CursorWindowAllocationException End
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myApp.dynaArray.addContributingArrayNew(
                            listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
                }

                myApp.bPlayerOn = true;
                PlayerService.startService(context, "1;0");
            }

            /*
            Context context = getApplicationContext();
            final MyApplication myApp = ((MyApplication) getApplication());

            if (myApp.bDrillOn) {
                myApp.bDrillOn = false;
                PlayerServiceStatic.stopService(context);

                WearMessage wearMsg = new WearMessage(context);
                wearMsg.sendSignal("/done-drilling");
            }
            else {

                String sCat = "00nh";
                String sSubcat = "~NONE";

                SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();
                String sql;
                if(sSubcat.equalsIgnoreCase("~NONE")) {
                    sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                            + "WHERE cat='" + sCat + "'";
                }
                else {
                    sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                            + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "'";
                }

                List<String> listItems = new ArrayList<String>();
                try {
                    Cursor cursor = database.rawQuery(sql, new String[0]);

                    if (cursor.moveToFirst()) {
                        do {
                            listItems.add(cursor.getString(0));
                        } while (cursor.moveToNext());
                    }

                    //fix - android.database.CursorWindowAllocationException Start
                    cursor.close();
                    //fix - android.database.CursorWindowAllocationException End
                } catch (Exception e) {
                    e.printStackTrace();
                }

                myApp.bDrillOn = true;
                PlayerServiceStatic.startService(context, listItems.toArray(new String[]{}), 30);
            }
            */
        }
    }
}
