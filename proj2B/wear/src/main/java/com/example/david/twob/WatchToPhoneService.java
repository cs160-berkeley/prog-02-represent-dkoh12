package com.example.david.twob;

/**
 * Created by david on 3/3/16.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 3/1/16.
 */
public class WatchToPhoneService extends Service {

    Boolean isZipCode = Boolean.FALSE;
    private String name;
    private String sZipCode;

    private static final String TAG = "@>@>@>@>";
    private GoogleApiClient mWatchApiClient;
    final Service _this = this;

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                }).build();
//        mWatchApiClient.connect();
        Log.d(TAG, "in watch to phone");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        Log.d(TAG, "WtoT: on start command");

        String thing = extras.getString("nameOrZip");

        try {
            int iZipCode = Integer.parseInt(thing);
            sZipCode = isZipCode.toString();
            isZipCode = Boolean.TRUE;
            Log.d(TAG, "is it getting through? " + sZipCode);
        } catch (NumberFormatException e) {
            name = thing;
            isZipCode = Boolean.FALSE;
            Log.d(TAG, "is it getting through? " + name);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mWatchApiClient.connect();
                if(isZipCode){
                    sendMessage("/send_zipcode", sZipCode);
                } else {
                    sendMessage("/send_name", name);
                }
                Log.wtf(TAG, "sent");
            }
        }).start();

        //_this.stopSelf();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void sendMessage(final String path, final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mWatchApiClient, node.getId(), path, text.getBytes()).await();
                }
            }
        }).start();
    }

}