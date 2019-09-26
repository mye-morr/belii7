package com.better_computer.habitaid.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.Toast;

import com.better_computer.habitaid.MainActivity;
import com.better_computer.habitaid.MyApplication;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.data.core.Session;
import com.better_computer.habitaid.data.core.SessionHelper;
import com.better_computer.habitaid.data.core.Event;
import com.better_computer.habitaid.data.core.EventHelper;
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

        if(event.getPath().equalsIgnoreCase("/status")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            PressedData pressedData = PressedData.toPressedData(jsonString);

            Games game = new Games();
            game.setCat(pressedData.getCat());
            game.setSubcat("");
            game.setContent(pressedData.getCaption());
            game.setPts(parseInt(pressedData.getPoints()));
            game.setTimestamp(Calendar.getInstance());

            DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);

            /*
            String sCat = pressedData.getCat();

            if (sCat.equalsIgnoreCase("focus")) {
                String sPressed = pressedData.getCaption();
                String sMultiplier = pressedData.getPoints();
                String sGamesLastStatus = StopwatchUtil.getStopwatchLastStatus(this);

                if (sPressed.startsWith("^")) { // it's a count
                    Games game = new Games();
                    game.setCat(sCat);
                    game.setSubcat("count");
                    game.setContent(sPressed);
                    game.setPts(-1);
                    game.setTimestamp(Calendar.getInstance());
                    DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);

                } else { // it's a drain


                    MyApplication myApp = (MyApplication)getApplication();

                    if (sPressed.equalsIgnoreCase("maint") ||
                            !sPressed.equalsIgnoreCase(sGamesLastStatus)) {
                        StopwatchUtil.setStopwatchStopTime(this, System.currentTimeMillis());

                        long passedTime = StopwatchUtil.getStopwatchPassedTime(this);
                        long passedSecs = passedTime / 1000;

                        if (passedSecs < 60 * 25) { // 25 min log-limit
                            Games game = new Games();
                            game.setCat(sCat);
                            game.setSubcat("");
                            game.setContent(sGamesLastStatus);

                            int iPassedMin = -1 * ((int) Math.round(((1+(0.2 * Integer.valueOf(sMultiplier))) * passedSecs) / 60.0));
                            if (sGamesLastStatus.equalsIgnoreCase("maint")) {
                                iPassedMin = -iPassedMin;
                            }

                            game.setPts(iPassedMin);
                            game.setTimestamp(Calendar.getInstance());

                            DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);
                            StopwatchUtil.resetStopwatchStartTime(this, sPressed);
                        } else {
                            StopwatchUtil.resetStopwatchStartTime(this, sPressed);
                        }
                    }


                }
            } else {
                Games game = new Games();
                game.setCat(pressedData.getCat());
                game.setSubcat("");
                game.setContent(pressedData.getCaption());
                game.setPts(parseInt(pressedData.getPoints()));
                game.setTimestamp(Calendar.getInstance());

                DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);
            }

            */
        }
        else if(event.getPath().equalsIgnoreCase("/done-session")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            SessionData sessionData = SessionData.toSessionData(jsonString);

            Session sesh = new Session();
            sesh.setDate(sessionData.getDate());
            sesh.setName(sessionData.getName());
            sesh.setTimDur(parseInt(sessionData.getTimDur()));
            sesh.setTask(sessionData.getTask());
            sesh.setImp(parseInt(sessionData.getImp()));
            sesh.setImpDets(sessionData.getImpDets());
            sesh.setPts(parseInt(sessionData.getPts()));
            sesh.setPtsDets(sessionData.getPtsDets());
            sesh.setDtTimStr(sessionData.getDtTimStr());
            sesh.setTimEnd(sessionData.getTimEnd());

            DatabaseHelper.getInstance().getHelper(SessionHelper.class).createOrUpdate(sesh);
        }
        else if(event.getPath().equalsIgnoreCase("/done-event")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            EventData eventData = EventData.toEventData(jsonString);

            Event trans = new Event();
            trans.setDate(eventData.getDate());
            trans.setName(eventData.getName());
            trans.setTimDur(parseInt(eventData.getTimDur()));
            trans.setPtsVal(parseInt(eventData.getPtsVal()));
            trans.setImp(parseInt(eventData.getImp()));
            trans.setImpDets(eventData.getImpDets());
            trans.setDtTimStr(eventData.getDtTimStr());
            trans.setTimEnd(eventData.getTimEnd());

            DatabaseHelper.getInstance().getHelper(EventHelper.class).createOrUpdate(trans);
        }
        else if(event.getPath().equalsIgnoreCase("/fetch-next-card")) {
            Context context = getApplicationContext();
            MyApplication myApp = ((MyApplication) getApplication());
            String sNextCard = "";
            sNextCard = myApp.dynaArray.getRandomElementNew();
            wearMessage = new WearMessage(context);
            wearMessage.sendMessage("/store-next-card", sNextCard, "");
        }
        else if(event.getPath().equalsIgnoreCase("/reset-cards")) {
            MyApplication myApp = ((MyApplication) getApplication());
            myApp.dynaArray.init();
        }
        else if(event.getPath().equalsIgnoreCase("/done-task")) {
            byte[] inBytesData = event.getData();
            String jsonString = new String(inBytesData);
            MessageData messageData = MessageData.toMessageData(jsonString);

            String sTask = messageData.getText1();
            String sTime = messageData.getText2();

            Games game = new Games();

            if(sTask.equalsIgnoreCase("engaged") || sTask.equalsIgnoreCase("transition")) {
                game.setCat("00" + sTask);
            }
            else {
                game.setCat("00task");
            }

            game.setSubcat("");
            game.setContent("dun: " + sTask);
            game.setPts(Integer.valueOf(sTime));
            game.setTimestamp(Calendar.getInstance());

            DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);

            if(!sTask.equalsIgnoreCase("engaged")) {
                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "category", SearchEntry.Search.EQUAL, "ontrack"));
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcategory", SearchEntry.Search.EQUAL, "Toda"));
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "message", SearchEntry.Search.EQUAL, sTask));
                DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).delete(keys);
            }
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
