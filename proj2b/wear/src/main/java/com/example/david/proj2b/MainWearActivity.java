package com.example.david.proj2b;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainWearActivity extends Activity implements WearableListView.ClickListener, SensorEventListener {

    private WearableListView mListView;
    private static final String TAG = "@>@>@>@>";
    private Button mVoteView;
    private String zipCode;

    ArrayList<Candidate> mCandidates = new ArrayList<>();
    String arr[];
    String county;
    Boolean check = Boolean.FALSE;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    //    http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_list_view);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) findViewById(R.id.listView);
                mListView.setAdapter(new WearAdapter(MainWearActivity.this));
                mListView.setClickListener(MainWearActivity.this);
            }

        });

        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String s = extras.getString("dataToWatch");

            arr = s.split("@");

            Log.d(TAG, "arr.length: " + arr.length);

            for (int i = 0; i < arr.length; i++) {
                Log.d(TAG, i + " " + arr[i]);
            }

            zipCode = arr[0];
            county = arr[1];

            for (int i = 2; i < arr.length; i += 3) {
                Candidate c = new Candidate(arr[i], arr[i + 1], arr[i + 2]);
                mCandidates.add(c);
            }

            check = Boolean.TRUE;
        }

        updateVoteViewButton();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void updateVoteViewButton() {
        mVoteView = (Button) findViewById(R.id.vote_view);

        mVoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), VoteView.class);
                if (check) {
                    String location = county + "@" + arr[4];
                    i.putExtra("location", location);
                }
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Intent i = new Intent(this, WatchToPhoneService.class);
        TextView nameTemp = (TextView) viewHolder.itemView.findViewById(R.id.wear_name);
        String name = nameTemp.getText().toString();

        Log.d(TAG, "name: " + name);
        i.putExtra("nameOrZip", name);
        startService(i);
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    private class WearAdapter extends WearableListView.Adapter {

        private final LayoutInflater mLayoutInflater;

        private WearAdapter(Context c) {
            mLayoutInflater = LayoutInflater.from(c);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(mLayoutInflater.inflate(R.layout.activity_main_wear, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            TextView name = (TextView) viewHolder.itemView.findViewById(R.id.wear_name);

            String mName = mCandidates.get(i).getName();
            name.setText(mName);

            String mParty = mCandidates.get(i).getParty();

            if (mParty.equals("Republican")) {
                name.setBackgroundColor(getResources().getColor(R.color.newRed));
            } else {
                name.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            }

            viewHolder.itemView.setTag(i);
        }

        @Override
        public int getItemCount() {
            if (check)
                return mCandidates.size();
            else
                return 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Log.d(TAG, "accuracy changed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    whenShake();
                }

                last_x = x;
                last_y = y;
                last_z = z;

            }
        }
    }


    private void whenShake() {
        Random rand = new Random();
        int randZipCode = rand.nextInt(90000) + 10000; // +1 ?
        zipCode = Integer.toString(randZipCode);

        Intent shakeIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        shakeIntent.putExtra("nameOrZip", zipCode);
        startService(shakeIntent);

    }

}