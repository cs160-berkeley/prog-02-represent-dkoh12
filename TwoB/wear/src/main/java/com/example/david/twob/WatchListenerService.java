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
    private static final String party = "/party";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "in WatchListenerService, got: " + messageEvent.getPath());

        if( messageEvent.getPath().equalsIgnoreCase(party) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            String arr[] = value.split(" ");
            String politicianName = arr[0];
            String politicianParty = arr[1];

            Intent intent = new Intent(this, MainWearActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("POLITICIAN_NAME", politicianName);

            if(politicianParty.equals("Republican")) {
                intent.putExtra("POLITICIAN_PARTY", "Republican");
            } else {
                intent.putExtra("POLITICIAN_PARTY", "Democrat");
            }

            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }

}
