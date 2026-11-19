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

        mGoogleApiClient.connect();
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

    public void sendData(final String sPath, final String sJson) {
        if (mGoogleApiClient.isConnected()) {
            executeSendData(sPath, sJson);
        } else {
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    executeSendData(sPath, sJson);
                    try {
                        mGoogleApiClient.unregisterConnectionCallbacks(this);
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                }
            });
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void executeSendData(final String sPath, final String sJson) {
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
                        }
                    }
                });
    }

    private ResultCallback<MessageApi.SendMessageResult> getSendMessageResultCallback() {
        return new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e(LOG_TAG, "Failed to send message with status "
                            + sendMessageResult.getStatus());
                } else {
                    Log.d(LOG_TAG, "Successfully sent message with status "
                            + sendMessageResult.getStatus());
                }
            }
        };
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected : " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended : " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Failed to connect to Google Api Client with error code "
                + connectionResult.getErrorCode());
    }
}
