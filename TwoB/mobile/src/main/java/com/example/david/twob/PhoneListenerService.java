package com.example.david.twob;

import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import java.nio.charset.StandardCharsets;

/**
 * Created by david on 3/1/16.
 */
public class PhoneListenerService extends WearableListenerService {
    private static final String TOAST = "/send_toast";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(TOAST) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
