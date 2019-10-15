package com.better_computer.habitaid.util;

import android.os.AsyncTask;
import android.util.Log;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SyncData {

    public void uploadEvent() {
        new Sync2ServerEvents().execute();
    }

    public void uploadLibrary() {
        new Sync2ServerLibrary().execute();
    }

    public void downloadLibrary() {
        new Sync2ClientLibrary().execute();
    }

    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

    private class Sync2ServerLibrary extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            List<NonSched> list = nonSchedHelper.findAll();

            Connection connection = null;
            PreparedStatement stmt = null;
            try {
                connection = openConnection();
                connection.setAutoCommit(false);

                String sql = "TRUNCATE TABLE [dbo].[core_tbl_nonsched_merge]";
                stmt = connection.prepareStatement(sql);
                stmt.execute();

                sql = "INSERT INTO [dbo].[core_tbl_nonsched_merge] (_id ,_frame ,_state ,cat ,subcat ,wtcat ,subsub ,iprio ,name ,abbrev ,content ,notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = connection.prepareStatement(sql);
                for (NonSched nonSched : list) {
                    stmt.setString(1, nonSched.get_id());
                    stmt.setString(2, nonSched.get_frame());
                    stmt.setString(3, nonSched.get_state());
                    stmt.setString(4, nonSched.getCat());
                    stmt.setString(5, nonSched.getSubcat());
                    stmt.setInt(6, nonSched.getWtcat());
                    stmt.setString(7, nonSched.getSubsub());
                    stmt.setInt(8, nonSched.getIprio());
                    stmt.setString(9, nonSched.getName());
                    stmt.setString(10, nonSched.getAbbrev());
                    stmt.setString(11, nonSched.getContent());
                    stmt.setString(12, nonSched.getNotes());
                    stmt.executeUpdate();
                }

                // merge is required because
                // truncate is not allowed on tbl w/ fk
                sql = "MERGE INTO core_tbl_nonsched tgt " +
                        "USING core_tbl_nonsched_merge src " +
                        "ON src._id = tgt._id " +
                        "WHEN MATCHED THEN " +
                        "UPDATE SET " +
                        "[_frame] = COALESCE(src.[_frame], tgt.[_frame])," +
                        "[_state] = COALESCE(src.[_state], tgt.[_state])," +
                        "[cat] = COALESCE(src.[cat], tgt.[cat])," +
                        "[subcat] = COALESCE(src.[subcat], tgt.[subcat])," +
                        "[wtcat] = COALESCE(src.[wtcat], tgt.[wtcat])," +
                        "[subsub] = COALESCE(src.[subsub], tgt.[subsub])," +
                        "[iprio] = COALESCE(src.[iprio], tgt.[iprio])," +
                        "[name] = COALESCE(src.[name], tgt.[name])," +
                        "[abbrev] = COALESCE(src.[abbrev], tgt.[abbrev])," +
                        "[content] = COALESCE(src.[content], tgt.[content])," +
                        "[notes] = COALESCE(src.[notes], tgt.[notes]) " +
                        "WHEN NOT MATCHED BY TARGET THEN " +
                        "INSERT VALUES (" +
                        "src.[_id]," +
                        "src.[_frame]," +
                        "src.[_state]," +
                        "src.[cat]," +
                        "src.[subcat]," +
                        "src.[wtcat]," +
                        "src.[subsub]," +
                        "src.[iprio]," +
                        "src.[name]," +
                        "src.[abbrev]," +
                        "src.[content]," +
                        "src.[notes]) " +
                        "WHEN NOT MATCHED BY SOURCE THEN " +
                        "DELETE;";
                stmt = connection.prepareStatement(sql);
                stmt.execute();

                connection.commit();
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
    }

    private class Sync2ClientLibrary extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            nonSchedHelper.delete(new ArrayList<SearchEntry>());    // delete all

            Connection connection = null;
            Statement stmt = null;
            try {
                connection = openConnection();

                String sql = "SELECT * FROM [dbo].[core_tbl_nonsched]";
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    NonSched nonSched = new NonSched();
                    nonSched.set_id(rs.getString("_id"));
                    nonSched.set_frame(rs.getString("_frame"));
                    nonSched.set_state(rs.getString("_state"));
                    nonSched.setCat(rs.getString("cat"));
                    nonSched.setSubcat(rs.getString("subcat"));
                    nonSched.setWtcat(rs.getInt("wtcat"));
                    nonSched.setSubsub(rs.getString("subsub"));
                    nonSched.setIprio(rs.getInt("iprio"));
                    nonSched.setName(rs.getString("name"));
                    nonSched.setAbbrev(rs.getString("abbrev"));
                    nonSched.setContent(rs.getString("content"));
                    nonSched.setNotes(rs.getString("notes"));
                    nonSchedHelper.create(nonSched);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
    }

}
