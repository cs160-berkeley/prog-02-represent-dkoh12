package com.example.david.twob;

/**
 * Created by david on 3/1/16.
 */
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;


public class WatchListenerService extends WearableListenerService {
    private static final String TAG = "@>@>@>@>";
    private static final String zipCode = "/zipCode";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "in WatchListenerService, got: " + messageEvent.getPath());

        if( messageEvent.getPath().equalsIgnoreCase(zipCode) ) {
            String zipCode = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(this, MainWearActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("zipCode", zipCode);

            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }

}
