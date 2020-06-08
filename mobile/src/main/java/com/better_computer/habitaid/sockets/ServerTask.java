package com.better_computer.habitaid.sockets;

import android.util.Log;

import com.better_computer.habitaid.util.Callback;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTask {

    public void startServer(Callback callback, int serverPort) throws Exception {
        // Create a server socket and handle requests
        // from client in a separate thread
        ServerSocket serverSoc  = new ServerSocket(serverPort);
        handleRequests(serverSoc, callback);
    }

    // This function would handle incoming requests
    // from the client, if any, in future
    private void handleRequests(final ServerSocket socket, final Callback callback) {
        new Thread() {
            public void run() {
                try{
                    // Run indefinitely
                    while(true) {
                        // socket object to receive incoming client requests
                        Socket client = socket.accept();

                        // takes input from the client socket
                        DataInputStream in = new DataInputStream(
                                new BufferedInputStream(client.getInputStream()));

                        byte[] buffer = new byte[2048];

                        // Read what we got from client
                        int bytesRead = in.read(buffer);
                        String stringFromWindows = new String(buffer, 0, bytesRead);
                        // invoke the callback with errorCode as -1
                        // as all is good so far
                        callback.execute(-1, stringFromWindows);

                        // Close connection with Client
                        in.close();
                        client.close();
                    }
                } catch (Exception ex) {
                    callback.execute(1, ex);
                }
            }
        }.start();
    }
}