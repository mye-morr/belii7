package com.better_computer.habitaid.util;

import android.os.AsyncTask;
import android.util.Log;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Events;
import com.better_computer.habitaid.data.core.EventsHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

    public class Sync2ServerEvents extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            EventsHelper eventsHelper = DatabaseHelper.getInstance().getHelper(EventsHelper.class);
            List<Events> listEvents = eventsHelper.findAll();

            Connection connection = null;
            PreparedStatement stmt = null;
            try {
                connection = openConnection();
                connection.setAutoCommit(false);

                String sql = "TRUNCATE TABLE [dbo].[core_tbl_events_merge]";
                stmt = connection.prepareStatement(sql);
                stmt.execute();

                sql = "INSERT INTO [dbo].[core_tbl_events_merge] (" +
                                                                "_id,_state,_frame" +
                                                                ",sDate" +
                                                                ",iLongDatetime" +
                                                                ",sName" +
                                                                ",iTimDur" +
                                                                ",iPtsVal" +
                                                                ",iImp" +
                                                                ",sDtTimStr" +
                                                                ",sTimEnd" +
                                                                ")" +
                                                                "VALUES(" +
                                                                "?,?,?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                                                ",?" +
                                ")";

                stmt = connection.prepareStatement(sql);
                for (Events events : listEvents) {
                    stmt.setString(1, events.get_id());
                    stmt.setString(2, events.get_state());
                    stmt.setString(3, events.get_frame());
                    stmt.setString(4, events.getSDate());
                    stmt.setInt(5, events.getILongDatetime());
                    stmt.setString(6, events.getSName());
                    stmt.setInt(7, events.getITimDur());
                    stmt.setInt(8, events.getIPtsVal());
                    stmt.setInt(9, events.getIImp());
                    stmt.setString(10, events.getSDtTimStr());
                    stmt.setString(11, events.getSTimEnd());
                    stmt.executeUpdate();
                }

                sql =
                        "MERGE INTO core_tbl_events tgt" +
                                " USING core_tbl_events_merge d" +
                                " ON d.[_id] = tgt.[_id]" +
                                " WHEN MATCHED THEN" +
                                " UPDATE SET " +
                                "[sDate]= COALESCE(d.[sDate], tgt.[sDate])" +
                                ",[iLongDatetime]= COALESCE(d.[iLongDatetime], tgt.[iLongDatetime])" +
                                ",[sName]= COALESCE(d.[sName], tgt.[sName])" +
                                ",[iTimDur]= COALESCE(d.[iTimDur], tgt.[iTimDur])" +
                                ",[iPtsVal]= COALESCE(d.[iPtsVal], tgt.[iPtsVal])" +
                                ",[iImp]= COALESCE(d.[iImp], tgt.[iImp])" +
                                ",[sDtTimStr]= COALESCE(d.[sDtTimStr], tgt.[sDtTimStr])" +
                                ",[sTimEnd]= COALESCE(d.[sTimEnd], tgt.[sTimEnd])" +
                                " WHEN NOT MATCHED BY TARGET THEN" +
                                " INSERT VALUES (" +
                                "d.[_id],d.[_frame],d.[_state]" +
                                ",d.[sDate]" +
                                ",d.[iLongDatetime]" +
                                ",d.[sName]" +
                                ",d.[iTimDur]" +
                                ",d.[iPtsVal]" +
                                ",d.[iImp]" +
                                ",d.[sDtTimStr]" +
                                ",d.[sTimEnd]" +
                                ");";

                stmt = connection.prepareStatement(sql);
                stmt.execute();

                connection.commit();

                eventsHelper.delete(new ArrayList<SearchEntry>());    // delete all
            } catch (Exception ex) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    private Connection openConnection() {
        String ipaddress = "184.168.194.77";
        String db = "narfdaddy2";
        String username = "narfdaddy2";
        String password = "TreeDemo1";

        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ipaddress + ";"

                    + "databaseName=" + db + ";user=" + username
                    + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
            return connection;
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }

        return null;
    }

    }
