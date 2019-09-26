package com.better_computer.habitaid.share;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WearMessage
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = WearMessage.class.getSimpleName();

    public WearMessage(Context context) {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void sendSignal(String sPath) {
        sendData(sPath, "");
    }

    public void sendMessage(String sPath, String sText1, String sText2) {
        MessageData messageData = new MessageData();
        messageData.setText1(sText1);
        messageData.setText2(sText2);

        sendData(sPath, messageData.toJsonString());
    }

    public void sendLibrary(String sPath, LibraryData libraryData) {
        sendData(sPath, libraryData.toJsonString());
    }

    public void sendData(String sPath, String sJson) {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        final String fsPath = sPath;
        final String fsJson = sJson;
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (final Node node : getConnectedNodesResult.getNodes()) {
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                                    fsPath, fsJson.getBytes()).setResultCallback(
                                    getSendMessageResultCallback());

                            // apparently another option instead of
                            // .setResultCallback(...) is .await();

                        }
                    }
                });
    }

    private ResultCallback<MessageApi.SendMessageResult> getSendMessageResultCallback() {
        return new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e(LOG_TAG, "Failed to connect to Google Api Client with status "
                            + sendMessageResult.getStatus());
                } else {
                    Log.d(LOG_TAG, "Successful to connect to Google Api Client with status "
                            + sendMessageResult.getStatus());
                }
            }
        };
    }

    /* old send-message
    public void sendMessage(final String path, final String message) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, message.getBytes() ).await();
                }

                // this was commented out before, until
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "m to phon" + message, Toast.LENGTH_SHORT).show();
                    }
                });
                // .. here
            }
        }).start();
    }
    */

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected : " + bundle);
//        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended : " + i);
//        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Failed to connect to Google Api Client with error code "
                + connectionResult.getErrorCode());
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

}