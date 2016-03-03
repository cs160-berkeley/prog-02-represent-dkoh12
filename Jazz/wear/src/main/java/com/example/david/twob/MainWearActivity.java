package com.example.david.twob;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Random;

public class MainWearActivity extends Activity implements WearableListView.ClickListener {

    private WearableListView mListView;
    private static final String TAG = "@>@>@>@>";
    private Button mVoteView;
    private String zipCode;

    public static ArrayList<Candidate> mCandidates;
    Boolean check = Boolean.FALSE;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

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

        if(extras != null) {
            zipCode = extras.getString("zipCode");
            //might put updatevoteviewbutton here w/ parameter paramZipCode
            mCandidates = getDataSet(zipCode);
            check = Boolean.TRUE;
        }

        updateVoteViewButton();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                //I believe his function gets called when you shake the watch.
                //generate new zipCode;

                Random rand = new Random();
                int randZipCode = rand.nextInt((100000 - 10000) + 1) + 10000;
                zipCode = Integer.toString(randZipCode);
                updateVoteViewButton();

                Intent shakeIntent = new Intent(getBaseContext(), WatchToPhoneServiceTempSol.class);
                shakeIntent.putExtra("nameOrZip", zipCode);
                startService(shakeIntent);
            }
        });

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void updateVoteViewButton(){
        mVoteView = (Button) findViewById(R.id.vote_view);

        mVoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), VoteView.class);
                if(check) {
                    i.putExtra("zipCode", zipCode);
                }
                startActivity(i);
            }
        });
    }

    //given zipCode retrieve DataSet
    private ArrayList<Candidate> getDataSet(String zipCode) {
        ArrayList<Candidate> candidates = new ArrayList<>();

        if(zipCode.equals("94704")) {
            candidates.add(new Candidate("Kevin McCarthy", "Republican", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Barbara Boxer", "Democrat", R.drawable.senator_barbara_boxer));
            candidates.add(new Candidate("Dianne Feinstein", "Democrat", R.drawable.senator_dianne_feinstein));
        } else { //80001
            candidates.add(new Candidate("Cory Gardner", "Republican", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Michael Bennet", "Democrat", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Mike Coffman", "Republican", R.drawable.kevin_mccarthy));
        }
        //Collections.shuffle(list);

        return candidates;
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

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Intent i = new Intent(this, WatchToPhoneServiceTempSol.class);
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

        private WearAdapter(Context c){
            mLayoutInflater = LayoutInflater.from(c);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
            return new WearableListView.ViewHolder(mLayoutInflater.inflate(R.layout.activity_main_wear, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i){
            TextView name = (TextView) viewHolder.itemView.findViewById(R.id.wear_name);
            String mName = mCandidates.get(i).getName();
            name.setText(mName);

//            TextView party = (TextView) viewHolder.itemView.findViewById(R.id.wear_party);
            String mParty = mCandidates.get(i).getParty();
            //party.setText(mParty);

            if(mParty.equals("Republican")){
                name.setBackgroundColor(getResources().getColor(R.color.newRed));
                //party.setBackgroundColor(getResources().getColor(R.color.newRed));
            } else {
                name.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                //party.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            }

            viewHolder.itemView.setTag(i);
        }

        @Override
        public int getItemCount(){
            if(check)
                return mCandidates.size();
            else
                return 0;
        }
    }
}
