package com.better_computer.habitaid.sockets;

import android.os.AsyncTask;
import android.util.Log;

import com.better_computer.habitaid.util.Callback;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

// This class is responsible for executing the socket communication
// in the background thread (other than UI Thread)
public class SocketCommunicationTask extends AsyncTask<Object, Void, String> {

    // Reference to a callback
    Callback callback = null;

    // This task will run in the background and do the socket
    // communication
    @Override
    protected String doInBackground(Object... objects) {
        try {
            // Get parameters from arguments
            String serverIp = objects[0].toString();
            int serverPort  = Integer.parseInt(objects[1].toString());
            String msg_to_send = objects[2].toString();
            callback = (Callback)objects[3];

            // establish the connection with server port
            Socket s = new Socket(serverIp, serverPort);
            // sends output to the socket
            PrintWriter out = new PrintWriter(s.getOutputStream());
            // send a message
            out.print(Calendar.getInstance().getTime() + " " + msg_to_send);

            // we are done, close the connection
            out.close();
            s.close();
        }
        catch (Exception ex) {
            Log.e("", "AK doInBackground: ", ex);
            return ex.getMessage();
        }
        return null;
    }

    // This will be called when socket communication is completed
    // i.e. on completion of the task
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        // If we failed, then communicate the same
        if(s != null) {
            callback.execute(1, s);
        }

    }
}