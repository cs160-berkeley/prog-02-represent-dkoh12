package com.example.david.twob;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.widget.TextView;

public class MainWearActivity extends Activity {

    private static final String TAG = "@>@>@>@>";
    private Button mVoteView;
    private TextView mName;
    private TextView mParty;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    //    http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main_wear);

        mName = (TextView) findViewById(R.id.wear_name);
        mParty = (TextView) findViewById(R.id.wear_party);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Log.d(TAG, "main wear");

        if(extras != null) {
            for (String key : extras.keySet()) {
                Log.d(TAG, key + " is a key in bundle");
            }

            Log.d(TAG, "please");
            String politician_name = extras.getString("POLITICIAN_NAME");
            mName.setText(politician_name);

            String politician_party = extras.getString("POLITICIAN_PARTY");
            mParty.setText(politician_party);
        }

        mVoteView = (Button) findViewById(R.id.vote_view);

        mVoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), VoteView.class);
                startActivity(i);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                // Do stuff!
            }
        });

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
