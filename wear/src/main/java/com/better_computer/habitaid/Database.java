package com.better_computer.habitaid;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.better_computer.habitaid.share.SessionData;
import com.better_computer.habitaid.share.EventData;
import com.better_computer.habitaid.share.WearMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Database extends SQLiteOpenHelper {

    //The database file.
	private static final String DATABASE = "tasks.db";

	protected Context context;
	private SharedPreferences prefs;

    //Version 2, version 1 was a test version.
	private static final int VERSION = 2;
	

	public Database(Context context, String name, CursorFactory factory,
					int version) {
		super(context, name, factory, version);
		this.context = context;
	}
	
	public Database(Context context) {
		super(context,DATABASE,null,VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//starting database

		/*
		// in case we later decide to start
		// duplicate events in watch db
		db.execSQL("CREATE TABLE sesh ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), name VARCHAR(20), timDur INTEGER, timEng INTEGER, " +
				"task TEXT, " + // in the delimited format: timStr: name (timAct-timReq);
				"impul INTEGER, impulDets TEXT, pts INTEGER, ptsDets TEXT" +
				"sDtTimStr VARCHAR(20), sTimEnd VARCHAR(10));");
		*/

		db.execSQL("CREATE TABLE statsEffic ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"sesh INTEGER" +
				",dat VARCHAR(10), typ VARCHAR(10), min INTEGER, event VARCHAR(20), wasTimed INTEGER);");
		db.execSQL("CREATE TABLE statsTrans ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(20), min INTEGER, impul INTEGER, count INTEGER);");
		db.execSQL("CREATE TABLE impul ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(20), timWhen INTEGER);");
		db.execSQL("CREATE TABLE pts ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), ptsEvent INTEGER, name VARCHAR(20), timWhen INTEGER);");

	}

	public void clearEffic() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM statsEffic"); //clear all
		db.close();
	}

	public String viewEffic() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorStats = db.rawQuery(
				"SELECT CAST(sesh as TEXT) || ' ' || SUBSTR(dat,6,5) || ' ' || typ || ' ' || CAST(min as TEXT) || ' ' || event || ' ' || CAST(wasTimed as TEXT)"
				+ " FROM statsEffic", null);
		// WHERE date<>'" + sDate + "'
		Cursor cursorInner;
		int iCount = 0;
		String sRet = "";
		while (cursorStats.moveToNext())
		{
			sRet = sRet + ";" + cursorStats.getString(0);
		}

		cursorStats.close();

		if(sRet.length() > 0) {
			sRet = sRet.substring(1);
		}
		return sRet;
	}

	public void addImp(String sImp, int iTimWhen) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO impul (name, timWhen) VALUES (" +
				"\"" + sImp + "\"" +
				"," + String.valueOf(iTimWhen) + ")");
		db.close();
	}

	public void addPts(String sDate, int iPtsEvent, String sName, int iTimWhen) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO pts (date, ptsEvent, name, timWhen) VALUES (" +
				"\"" + sDate + "\"," +
				String.valueOf(iPtsEvent) +
				",\"" + sName + "\"" +
				"," + String.valueOf(iTimWhen) + ")");
		db.close();
	}

	public void doneTrans(String sName, int iTimDur) {
		int iAvgMin = 0;
		int iAvgImpul = 0;
		int iCount = 0;

		SQLiteDatabase db = getWritableDatabase();

		Cursor cursorImpul = db.rawQuery(
				"SELECT id FROM impul"
				, null);

		int iImpul = cursorImpul.getCount();
		cursorImpul.close();

		Cursor cursorTrans = db.rawQuery("SELECT * FROM statsTrans WHERE name='" + sName + "'", null);
		if (cursorTrans.getCount() == 0) {

			iAvgImpul = Math.round(iImpul / iTimDur);

			db.execSQL("INSERT INTO statsTrans (name, min, impul, count) VALUES (" +
					"\"" + sName + "\"" +
					"," + String.valueOf(iTimDur) +
					"," + String.valueOf(iAvgImpul) +
					",1)");
			db.close();

		}
		else {
			cursorTrans.moveToNext();
			iAvgMin = cursorTrans.getInt(2);
			iAvgImpul = cursorTrans.getInt(3);
			iCount = cursorTrans.getInt(4);

			int iTotMin = iAvgMin * iCount;

			iAvgMin = Math.round((iTotMin + iTimDur) / (iCount + 1));
			iAvgImpul = Math.round((iAvgImpul * iTotMin + iImpul) / (iTotMin + iTimDur));

			db.execSQL("UPDATE statsTrans SET " +
					"min=" + String.valueOf(iAvgMin) +
					",impul=" + String.valueOf(iAvgImpul) +
					",count=" + String.valueOf(iCount + 1) +
					" WHERE name='" + sName + "';");
			db.close();
		}
	}

	public void doneEffic(Long lCurSeshNum,
						  String sDate,
							String sType,
						 int iMin,
						 String sEvent,
						 int iWasTimed) {

		/*
		db.execSQL("CREATE TABLE statsEffic ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"sesh INTEGER,dat VARCHAR(10), typ VARCHAR(10), min INTEGER, event VARCHAR(20), wasTimed INTEGER);");
		 */

		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO statsEffic (sesh, dat, typ, min, event, wasTimed) VALUES (" +
				"" + String.valueOf(lCurSeshNum) + "" +
				",\"" + sDate + "\"" +
				",\"" + sType + "\"" +
				"," + String.valueOf(iMin) + "" +
				",\"" + sEvent + "\"" +
				"," + String.valueOf(iWasTimed) + "" +
				")");
		db.close();

		/*
		SQLiteDatabase db = getWritableDatabase();

		db.execSQL("INSERT INTO timDecr (date, name, min, timWhen) VALUES (" +
				"\"" + sDate + "\"" +
				",\"" + sName + "\"" +
				"," + String.valueOf(iMin) + "" +
				",\"" + sTimWhen + "\"" +
				")");
		db.close();
		*/
	}

	public String[] summaryEfficSesh(long lCurSeshNum) {

		/*
		db.execSQL("CREATE TABLE statsEffic ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"dat VARCHAR(10), typ VARCHAR(10), min INTEGER, event VARCHAR(20), wasTimed INTEGER);");
		 */

		// returns ArrayList lRet to be displayed
		// statsTrans holds avgMin and avgImpul (see abov)
		// lRet contains deviations from avg + impulses

		List<String> lRet = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorTyp = db.rawQuery(
				"SELECT typ, SUM(min) " +
						"FROM statsEffic " +
						"WHERE sesh='" + String.valueOf(lCurSeshNum) + "' " +
						"GROUP BY typ;"
				, null);

		int iMinEngaged = 0;
		int iMinTrans = 0;
		int iMinLost = 0;
		String sTyp = "";

		while (cursorTyp.moveToNext())
		{
			sTyp = cursorTyp.getString(0);
			if(sTyp.equalsIgnoreCase("l0st")) {
				iMinLost = cursorTyp.getInt(1);
			}
			else if (sTyp.equalsIgnoreCase("trans")) {
				iMinTrans = cursorTyp.getInt(1);
			}
			else if (sTyp.equalsIgnoreCase("engaged")) {
				iMinEngaged = cursorTyp.getInt(1);
			}
		}

		cursorTyp.close();

		int iMinSum = iMinEngaged + iMinTrans + iMinLost;
		double dPercEngaged = 0;
		double dPercTrans = 0;
		double dPercLost = 0;

		if(iMinSum > 0) {
			dPercEngaged = (double) iMinEngaged / (double)iMinSum;
			dPercTrans = (double)iMinTrans / (double)iMinSum;
			dPercLost = (double)iMinLost / (double)iMinSum;
		}

		lRet.add(String.valueOf(Math.round(dPercEngaged * 100))
				+ " / "
				+ String.valueOf(Math.round(dPercTrans * 100))
				+ " / "
				+ String.valueOf(Math.round(dPercLost * 100)));

		return lRet.toArray(new String[]{});
	}

	public String[] summaryEfficDay(String sDatToday) {

		/*
		db.execSQL("CREATE TABLE statsEffic ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"dat VARCHAR(10), typ VARCHAR(10), min INTEGER, event VARCHAR(20), wasTimed INTEGER);");
		 */

		// returns ArrayList lRet to be displayed
		// statsTrans holds avgMin and avgImpul (see abov)
		// lRet contains deviations from avg + impulses

		List<String> lRet = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorTyp = db.rawQuery(
				"SELECT typ, SUM(min) " +
						"FROM statsEffic " +
						"WHERE dat='" + sDatToday + "' " +
						"GROUP BY typ;"
				, null);

		int iMinEngaged = 0;
		int iMinTrans = 0;
		int iMinLost = 0;
		String sTyp = "";

		while (cursorTyp.moveToNext())
		{
			sTyp = cursorTyp.getString(0);
			if(sTyp.equalsIgnoreCase("l0st")) {
				iMinLost = cursorTyp.getInt(1);
			}
			else if (sTyp.equalsIgnoreCase("trans")) {
				iMinTrans = cursorTyp.getInt(1);
			}
			else if (sTyp.equalsIgnoreCase("engaged")) {
				iMinEngaged = cursorTyp.getInt(1);
			}
		}

		cursorTyp.close();

		int iMinSum = iMinEngaged + iMinTrans + iMinLost;
		double dPercEngaged = 0;
		double dPercTrans = 0;
		double dPercLost = 0;

		if(iMinSum > 0) {
			dPercEngaged = (double)iMinEngaged / (double)iMinSum;
			dPercTrans = (double)iMinTrans / (double)iMinSum;
			dPercLost = (double)iMinLost / (double)iMinSum;
		}

		lRet.add(String.valueOf(Math.round(dPercEngaged * 100))
				+ " / "
				+ String.valueOf(Math.round(dPercTrans * 100))
				+ " / "
				+ String.valueOf(Math.round(dPercLost * 100)));

		return lRet.toArray(new String[]{});
	}


	// used for ActivityInput -> "l0st"
	public void doneTimDecr(String sDate,
							String sName, int iMin, String sTimWhen) {

		/*
		db.execSQL("CREATE TABLE timDecr ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), name VARCHAR(20), min INTEGER, timWhen VARCHAR(5));");
		 */

		/*
		SQLiteDatabase db = getWritableDatabase();

		db.execSQL("INSERT INTO timDecr (date, name, min, timWhen) VALUES (" +
				"\"" + sDate + "\"" +
				",\"" + sName + "\"" +
				"," + String.valueOf(iMin) + "" +
				",\"" + sTimWhen + "\"" +
				")");
		db.close();
		*/

		EventData eventData = new EventData();
		eventData.setDate(sDate);
		eventData.setName(sName);
		eventData.setTimDur(String.valueOf(iMin));
		eventData.setPtsVal(String.valueOf(0));
		eventData.setImp(String.valueOf(0));
		eventData.setImpDets("");
		eventData.setDtTimStr(sDate + " " + sTimWhen);
		eventData.setTimEnd(sTimWhen);

		final String eventDataString = eventData.toJsonString();
		WearMessage wearMessage = new WearMessage(context);
		wearMessage.sendData("/done-event", eventDataString);

	}

	public void doneEvent(String sDate,
						  String sName,
						  int iTimDur,
						  int iPtsVal,
						  String sDtTimStr,
						  String sTimEnd) {

		int iTotImp = 0;
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursorImpul = db.rawQuery("SELECT id FROM impul ORDER BY timWhen", null);
		int numRowImpul = cursorImpul.getCount();
		iTotImp = numRowImpul;

		//!!! should be removed from EventData
		String sImpDets = "";

		EventData eventData = new EventData();
		eventData.setDate(sDate);
		eventData.setName(sName);
		eventData.setTimDur(String.valueOf(iTimDur));
		eventData.setPtsVal(String.valueOf(iPtsVal));
		eventData.setImp(String.valueOf(iTotImp));
		eventData.setImpDets(sImpDets);
		eventData.setDtTimStr(sDtTimStr);
		eventData.setTimEnd(sTimEnd);

		final String eventDataString = eventData.toJsonString();
		WearMessage wearMessage = new WearMessage(context);
		wearMessage.sendData("/done-event", eventDataString);

	}

	public String[] summaryStats(String sTrans, int iTotMin) {

		/*
		db.execSQL("CREATE TABLE statsTrans ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(20), min INTEGER, impul INTEGER, count INTEGER);");
		*/

		// returns ArrayList lRet to be displayed
		// statsTrans holds avgMin and avgImpul (see abov)
		// lRet contains deviations from avg + impulses

		List<String> lRet = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorImpul = db.rawQuery(
				"SELECT * FROM statsTrans WHERE name='" + sTrans + "';"
				, null);

		lRet.add(sTrans);

		String sAvgMin = "0";
		String sAvgImpul = "";

		if(cursorImpul.getCount() == 1) {
			cursorImpul.moveToNext();

			sAvgMin = String.valueOf(cursorImpul.getInt(2));
			sAvgImpul = String.valueOf(cursorImpul.getInt(3));
		}

		cursorImpul.close();

		cursorImpul = db.rawQuery(
				"SELECT id FROM impul"
				, null);

		String sTotImpul = String.valueOf(cursorImpul.getCount());
		cursorImpul.close();

		String sPrefix = "";
		int iDiff = iTotMin - parseInt(sAvgMin);
		if (iDiff >= 0) {
			sPrefix = "+";
		}
		lRet.add("m: " + sPrefix + String.valueOf(iDiff));
		lRet.add("i: " + String.valueOf(sTotImpul) + " - " + sAvgImpul);

		cursorImpul = db.rawQuery(
				"SELECT name, count(name) FROM impul GROUP BY name ORDER BY count(name) DESC"
				, null);

		while (cursorImpul.moveToNext())
		{
			lRet.add(cursorImpul.getInt(1)
					+ " | " + cursorImpul.getString(0));
		}

		cursorImpul.close();

		return lRet.toArray(new String[]{});
	}

	public String[] summaryTimIncr(String sDate) {

		/*
		db.execSQL("CREATE TABLE timIncr ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), name VARCHAR(20), min INTEGER, timWhen VARCHAR(5));");
		 */

		List<String> lRet = new ArrayList<String>();
		lRet.add("");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorImpul = db.rawQuery(
				"SELECT name, SUM(min) / (SELECT COUNT(DISTINCT date) FROM timIncr) FROM timIncr GROUP BY name ORDER BY SUM(min) / (SELECT COUNT(DISTINCT date) FROM timIncr) DESC"
				, null);
		// WHERE date<>'" + sDate + "'
		Cursor cursorInner;
		int iCount = 0;
		while (cursorImpul.moveToNext())
		{
			String sName = cursorImpul.getString(0);
			cursorInner = db.rawQuery("SELECT SUM(min) FROM timIncr WHERE date='" + sDate + "' and name='" + sName + "'",null);
			if(cursorInner.getCount() == 1){
				cursorInner.moveToFirst();
				iCount = cursorInner.getInt(0);
			}
			cursorInner.close();

			lRet.add(cursorImpul.getString(0) + " (" + (int)cursorImpul.getDouble(1) + ") - " + String.valueOf(iCount));
		}

		cursorImpul.close();

		return lRet.toArray(new String[]{});
	}

	public String[] summaryTimDecr(String sDate) {

		/*
		db.execSQL("CREATE TABLE timDecr ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), name VARCHAR(20), min INTEGER, timWhen VARCHAR(5));");
		 */

		List<String> lRet = new ArrayList<String>();
		lRet.add("");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursorImpul = db.rawQuery(
				"SELECT name, SUM(min) / (SELECT COUNT(DISTINCT date) FROM timDecr) FROM timDecr GROUP BY name ORDER BY SUM(min) / (SELECT COUNT(DISTINCT date) FROM timDecr) DESC"
				, null);
		// WHERE date<>'" + sDate + "'
		Cursor cursorInner;
		int iCount = 0;
		while (cursorImpul.moveToNext())
		{
			String sName = cursorImpul.getString(0);
			cursorInner = db.rawQuery("SELECT SUM(min) FROM timDecr WHERE date='" + sDate + "' and name='" + sName + "'",null);
			if(cursorInner.getCount() == 1){
				cursorInner.moveToFirst();
				iCount = cursorInner.getInt(0);
			}
			cursorInner.close();

			lRet.add("(" + (int)cursorImpul.getDouble(1) + ") "
					+ cursorImpul.getString(0)
					+ " - " + String.valueOf(iCount));
		}

		cursorImpul.close();

		return lRet.toArray(new String[]{});
	}

	public String[] summaryPtsPos(String sDate) {

		/*
		db.execSQL("CREATE TABLE timDecr ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR(10), name VARCHAR(20), min INTEGER, timWhen VARCHAR(5));");
		 */

		List<String> lRet = new ArrayList<String>();
		lRet.add("");

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursorPtsPos = db.rawQuery(
				"SELECT name, SUM(ptsEvent) FROM pts "
						+ "WHERE date='" + sDate + "'"
						+ " AND ptsEvent > 0 "
						+ "GROUP BY name ORDER BY SUM(ptsEvent) DESC"
				, null);

		int iSum = 0;
		int iPtsVal = 0;
		while (cursorPtsPos.moveToNext())
		{
			iPtsVal = cursorPtsPos.getInt(1);
			lRet.add(String.valueOf(iPtsVal) + " | " + cursorPtsPos.getString(0));

			iSum += iPtsVal;
		}

		cursorPtsPos.close();

		lRet.set(0, String.valueOf(iSum));
		return lRet.toArray(new String[]{});
	}

	public void clearImp() {
		SQLiteDatabase db = getWritableDatabase();

		// does it make sense to keep this info?
		// actually not, b/c its by event only etc.
		db.execSQL("DELETE FROM impul");
	}

/*
	//we add a new choice - so now it's possible to extend the list of choices.
	public void addChoice(String choice) {
		SQLiteDatabase db = getReadableDatabase();
		db.execSQL("INSERT INTO choices (name) VALUES ('"+choice+"')");
		db.close();
	}
*/

    /*
     * Reading the user-defined choices from the database
     */

/*
	public String[] readPrj() {

		SQLiteDatabase database = getReadableDatabase();

		Cursor cursor = database.rawQuery("SELECT cast(time as text) || ' ' || name FROM prj ORDER BY iprio", null);
		int count = cursor.getCount();

		if (count==0) //there is nothing, so first time we start the app.
		{
			//if empty then put all elements from constants into database there
            //that means: no extra choices was input by the user (or they were cleared)

			prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String[] sxPrj = prefs.getString("0comprj", "2").split(";");


			for (String prj : sxPrj)
				database.execSQL("INSERT INTO prj (name,time) VALUES ('"+prj+"',0)");

			cursor.close();
			return sxPrj;
		}
		else
		{
			//if not empty, then read all choices from the table and redefine the elements
			String[] elements = new String[count];
			int index = 0;
			while (cursor.moveToNext())
			{
				elements[index] = cursor.getString(0);
				index++;
			}
			Choices.ELEMENTS = elements; 	//overwrite the elements array
			cursor.close();
            return elements;
        }
    }

	public String[] readPrjNoTime() {

		SQLiteDatabase database = getReadableDatabase();

		Cursor cursor = database.rawQuery("SELECT name FROM prj ORDER BY iprio", null);
		int count = cursor.getCount();

		if (count==0) //there is nothing, so first time we start the app.
		{
			//if empty then put all elements from constants into database there
			//that means: no extra choices was input by the user (or they were cleared)

			prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String[] sxPrj = prefs.getString("0comprj", "2").split(";");


			for (String prj : sxPrj)
				database.execSQL("INSERT INTO prj (name,time) VALUES ('"+prj+"',0)");

			cursor.close();
			return sxPrj;
		}
		else
		{
			//if not empty, then read all choices from the table and redefine the elements
			String[] elements = new String[count];
			int index = 0;
			while (cursor.moveToNext())
			{
				elements[index] = cursor.getString(0);
				index++;
			}
			Choices.ELEMENTS = elements; 	//overwrite the elements array
			cursor.close();
			return elements;
		}
	}
*/

	//Add a new item into the database - to track what we had today for lunch!
	public void deleteSmTas(String sTask) {
		SQLiteDatabase db = getWritableDatabase();

		String sQuery = "DELETE FROM smtas WHERE name='" + sTask + "'";

		db.execSQL(sQuery);
		db.close();
	}

	//Add a new item into the database - to track what we had today for lunch!
	public void addTimeSmTas(String sTask, String sTime) {
		SQLiteDatabase db = getWritableDatabase();

		String sQuery = "UPDATE smtas SET time=time+" + sTime + " WHERE name='" + sTask + "'";

		db.execSQL(sQuery);
		db.close();
	}

	//Add a new item into the database - to track what we had today for lunch!
	public void addTimePrj(String sTask, String sTime) {
		SQLiteDatabase db = getWritableDatabase();

		db.execSQL("UPDATE prj SET time=time+" + sTime + " WHERE name='" + sTask + "'");
		db.close();
	}

    //Add a new item into the database - to track what we had today for lunch!
	public void addFood(String menu) {
		Calendar calendar = Calendar.getInstance();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		SQLiteDatabase db = getWritableDatabase();
		//System.out.println("Adding food: "+menu+" at day: "+weekday);
        //We are also saving the current day - maybe for future uses.
		db.execSQL("INSERT INTO menu (name,weekday) VALUES ('"+menu+"',"+weekday+")");
		db.close();
	}

    //clear all menu data
	public void clearPrj() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM prj"); //clear all
		db.close();
	}

    //clear all extra added choices from the database
	public void clearSmTas() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM smtas");
		db.close();
	}

    //This gets all the starts from the database as an arraylist.
	public ArrayList<Item> getStats() {
		ArrayList<Item> stats = new ArrayList<Item>();
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT name,weekday FROM menu ORDER BY name",null);
		int total = 0; 
		HashMap<String, Integer> map = new HashMap<String, Integer>();
        //simple loop for calculating the frequencies of an item.
		while (cursor.moveToNext()) {
			total++;
			String name = cursor.getString(0);
			if (map.containsKey(name)) //update frequency
			{
				int freq = map.get(name)+1;
				map.put(name, freq);
			}
			else //new item
			{
				map.put(name, 1);
			}
			int day = cursor.getInt(1);	//not yet used for anything
		}
		System.out.println("TOTAL ROWS = "+total);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			stats.add(new Item(entry.getKey(), entry.getValue(), total));
		}
		cursor.close();
		database.close();
		return stats;
	}

    /*
      This was an upgrade method, the old version had no user-added choices.
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion==1 && newVersion==2)
			db.execSQL("CREATE TABLE choices ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name TEXT);");
	}
}
