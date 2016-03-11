package com.example.david.twob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class VoteView extends Activity {

    private static final String TAG = "@>@>@>@>";
    private TextView mTextView;
    private String zipCode;
    private String state;
    private int obamaPercent;
    private int romneyPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_vote_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            zipCode = extras.getString("zipCode");

            TextView stateElement = (TextView) findViewById(R.id.state);
            TextView countyElement = (TextView) findViewById(R.id.county);

            if(zipCode.equals("94704")) {
                state = "CA";
                stateElement.setText("CA");
                countyElement.setText("San Francisco County");
            } else {
                state = "CO";
                stateElement.setText("CO");
                countyElement.setText("Denver County");
            }

            Log.d(TAG, state);

            if(state.equals("CA")){
                TextView obama = (TextView) findViewById(R.id.obama_percentage);
                obamaPercent = 52;
                obama.setText("52%");
                TextView romney = (TextView) findViewById(R.id.romney_percentage);
                romneyPercent = 48;
                romney.setText("48%");
            } else {
                TextView obama = (TextView) findViewById(R.id.obama_percentage);
                obamaPercent = 49;
                obama.setText("49%");
                TextView romney = (TextView) findViewById(R.id.romney_percentage);
                romneyPercent = 51;
                romney.setText("51%");
            }

            View backgroundElement = (View) findViewById(R.id.vote_view_background);
            if(obamaPercent > romneyPercent) {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            } else {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.newRed));
            }
        }
    }
}
