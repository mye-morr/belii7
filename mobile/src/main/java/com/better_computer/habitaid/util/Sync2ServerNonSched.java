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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

    public class Sync2ServerNonSched extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            List<NonSched> listNonSched = nonSchedHelper.findAll();

            Connection connection = null;
            PreparedStatement stmt = null;
            try {
                connection = openConnection();
                connection.setAutoCommit(false);

                String sql = "TRUNCATE TABLE [dbo].[core_tbl_nonSched_merge]";
                stmt = connection.prepareStatement(sql);
                stmt.execute();

                sql = "INSERT INTO [dbo].[core_tbl_nonSched_merge] (" +
                                                                "_id,_state,_frame" +
                                                                ",cat" +
                                                                ",subcat" +
                                                                ",wtcat" +
                                                                ",subsub" +
                                                                ",iprio" +
                                                                ",name" +
                                                                ",abbrev" +
                                                                ",content" +
                                                                ",notes" +
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
                                                                ",?" +
                                ")";

                stmt = connection.prepareStatement(sql);
                for (NonSched nonSched : listNonSched) {
                    stmt.setString(1, nonSched.get_id());
                    stmt.setString(2, nonSched.get_state());
                    stmt.setString(3, nonSched.get_frame());
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

                sql =
                        "MERGE INTO core_tbl_nonSched tgt" +
                                " USING core_tbl_nonSched_merge d" +
                                " ON d.[_id] = tgt.[_id]" +
                                " WHEN MATCHED THEN" +
                                " UPDATE SET " +
                                "[cat]= COALESCE(d.[cat], tgt.[cat])" +
                                ",[subcat]= COALESCE(d.[subcat], tgt.[subcat])" +
                                ",[wtcat]= COALESCE(d.[wtcat], tgt.[wtcat])" +
                                ",[subsub]= COALESCE(d.[subsub], tgt.[subsub])" +
                                ",[iprio]= COALESCE(d.[iprio], tgt.[iprio])" +
                                ",[name]= COALESCE(d.[name], tgt.[name])" +
                                ",[abbrev]= COALESCE(d.[abbrev], tgt.[abbrev])" +
                                ",[content]= COALESCE(d.[content], tgt.[content])" +
                                ",[notes]= COALESCE(d.[notes], tgt.[notes])" +
                                " WHEN NOT MATCHED BY TARGET THEN" +
                                " INSERT VALUES (" +
                                "d.[_id],d.[_frame],d.[_state]" +
                                ",d.[cat]" +
                                ",d.[subcat]" +
                                ",d.[wtcat]" +
                                ",d.[subsub]" +
                                ",d.[iprio]" +
                                ",d.[name]" +
                                ",d.[abbrev]" +
                                ",d.[content]" +
                                ",d.[notes]" +
                                ");";

                stmt = connection.prepareStatement(sql);
                stmt.execute();

                connection.commit();

                nonSchedHelper.delete(new ArrayList<SearchEntry>());    // delete all
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
