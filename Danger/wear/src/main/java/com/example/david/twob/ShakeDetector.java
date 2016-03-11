//package com.example.david.twob;
//
///**
// * Created by david on 3/1/16.
// */
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.support.wearable.view.WearableListView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.Random;
//
//public class ShakeDetector implements SensorEventListener {
//
//    private static final String TAG = "@>@>@>@>";
//    //private final LayoutInflater mLayoutInflater;
//
//    private OnShakeListener mShakeListener;
//
//    private long lastUpdate = 0;
//    private float last_x, last_y, last_z;
//    private static final int SHAKE_THRESHOLD = 600;
//
//
//    public ShakeDetector(OnShakeListener shakeListener) {
//        mShakeListener = shakeListener;
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//        Log.d(TAG, "accuracy changed");
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        Log.d(TAG, "on sensor changed");
//
//        Sensor mySensor = event.sensor;
//
//        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            long curTime = System.currentTimeMillis();
//
//            if((curTime - lastUpdate) > 100) {
//                long diffTime = (curTime - lastUpdate);
//                lastUpdate = curTime;
//
//                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
//
//                if(speed > SHAKE_THRESHOLD) {
//                    whenShake();
//                }
//
//                last_x = x;
//                last_y = y;
//                last_z = z;
//
//            }
//        }
//    }
//
//    public interface OnShakeListener {
//        public void onShake();
//    }
//
//
//    private void whenShake() {
//        Random rand = new Random();
//        int randZipCode = rand.nextInt(90000) + 10000; // +1 ?
//        String zipCode = Integer.toString(randZipCode);
//
//        Intent shakeIntent = new Intent(Context, WatchToPhoneService.class);
//        shakeIntent.putExtra("nameOrZip", zipCode);
//        startService(shakeIntent);
//
//    }
//}