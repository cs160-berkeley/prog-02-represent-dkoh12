package com.example.david.proj2b;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String TAG = "@>@>@>@>";

    private String url = "https://congress.api.sunlightfoundation.com/";
    private String api_key = "92ace5ed4e0247fc81c1607f25d89f9d";
    String GoogleAPIkey = "AIzaSyADuYrTKJx0a1jZ2JPB9QFYi4z7JK92Uhg";
    private Button mEnterButton;
    private EditText mZipCode;
    private CheckBox mCurrentLocation;
    protected String zipCode;
    protected String county;
    private Boolean check = Boolean.FALSE;

    protected Double latitude;
    protected Double longitude;

    String watchToData = null;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZipCode = (EditText) findViewById(R.id.EnterZipCode);
        mCurrentLocation = (CheckBox) findViewById(R.id.checkBox);

        buildGoogleApiClient();

        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    check = Boolean.TRUE;

                    Log.d(TAG, "latitude: " + latitude);
                    Log.d(TAG, "longitude: " + longitude);

                    zipCode = getZipCode(latitude, longitude);
                    Log.d(TAG, "current zipcode is " + zipCode);
                    Toast.makeText(MainActivity.this, "Set current location", Toast.LENGTH_SHORT).show();
                } else {
                    zipCode = "";
                    check = Boolean.FALSE;
                }
            }
        });


        mEnterButton = (Button) findViewById(R.id.enter);

        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check)
                    zipCode = mZipCode.getText().toString();

                if (zipCode.length() != 5) {
                    Toast.makeText(MainActivity.this, "Not a valid ZipCode", Toast.LENGTH_SHORT).show();
                } else {
                    startCongress();

                    try {
                        String myUrl = url + "legislators/locate?zip=" + zipCode + "&apikey=" + api_key;
                        watchToData = new RetrieveData().execute(myUrl, "sun").get();
                    } catch (Exception e) {
                        Log.d(TAG, "failed watchToDATA");
                    }
                    startWatch();
                }
            }
        });
    }

    private void startCongress() {
        Intent i = new Intent(MainActivity.this, CongressionalView.class);
        i.putExtra("zipCode", zipCode);
        startActivity(i);
    }

    private void startWatch() {
        county = getCountyCode(latitude, longitude);
        watchToData = zipCode + "@" + county + "@" + watchToData;

        Log.d(TAG, "watchToData: " + watchToData);

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra("dataToWatch", watchToData);
        startService(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            Log.d(TAG, "need permission");
        }

        if (mLastLocation != null) {
            Log.d(TAG, "last location" + mLastLocation.toString());

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "building Google API client");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private String getZipCode(double mLatitude, double mLongitude) {
        String myZipcode = null;
        Geocoder mGeocoder = new Geocoder(this, Locale.US);
        try {
            List<Address> mAddresses = mGeocoder.getFromLocation(mLatitude, mLongitude, 1);

            myZipcode = mAddresses.get(0).getPostalCode();
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }

        return myZipcode;
    }

    private String getCountyCode(double mLatitude, double mLongitude) {
        String mCounty = null;

        String myUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+mLatitude+","+mLongitude;

        try {
            mCounty = new RetrieveData().execute(myUrl, "google").get();
            String s[] = mCounty.split(" ");
            if(s[s.length-1].equals("County")){
                mCounty = "";
                for(int i=0; i<s.length-1; i++){
                    mCounty += s[i];
                    if(i != s.length-2)
                        mCounty += " ";
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "getting county failed");
        }

        Log.d(TAG, "mCounty: " + mCounty);
        return mCounty;
    }

    public class RetrieveData extends AsyncTask<String, Void, String> {
        private static final String TAG = "@>@>@>@>";

        StringBuilder sb = new StringBuilder();
        String data = null;

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "params 0: " + params[0] + " params 1: " + params[1]);

            JSONParser jsonParser = new JSONParser();

            if(params[1].equals("sun")) {
                JSONObject jsonObject = jsonParser.getJSONFromUrl(params[0]);

                try {
                    JSONArray arr = jsonObject.getJSONArray("results");

                    for (int i = 0; i < arr.length(); i++) {
                        //Log.d(TAG, "iteration " + i);
                        JSONObject json = arr.getJSONObject(i);

                        //String chamber = json.getString("chamber");
                        String first_name = json.getString("first_name");
                        String last_name = json.getString("last_name");
                        String name = first_name + " " + last_name;
                        String party = json.getString("party"); //D or R
                        String state = json.getString("state");

                        if (party.equals("D")) {
                            party = "Democrat";
                        } else {
                            party = "Republican";
                        }

                        //Log.d(TAG, "name: " + name + ", party: " + party + ", state: " + state);
                        sb.append(name + "@" + party + "@" + state);

                        if (i != arr.length())
                            sb.append("@");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                    Log.d(TAG, "getting data for watch");
                }
            } else {
                JSONObject jsonObject = jsonParser.getJSONFromUrl(params[0]);

                Log.d(TAG, "in google");

                try {
                    JSONArray arr = jsonObject.getJSONArray("results");

                    String county = null;
                    String type = null;

                    Log.d(TAG, "arr length: " + arr.length());

                    for (int i = 0; i < arr.length(); i++) {

                        Log.d(TAG, "iteration " + i);

                        JSONObject json = arr.getJSONObject(i);
                        JSONArray jArr = json.getJSONArray("address_components");

                        for(int j=0; j<jArr.length(); j++){
                            JSONObject o = jArr.getJSONObject(i);

                            type = o.getString("types");
                            Log.d(TAG, "type: " + type);

                            if((type.equals("[\"administrative_area_level_2\"]") ||
                                    (type.equals("[\"administrative_area_level_2\",\"political\"]")))) {
                                county = o.getString("long_name");
                                Log.d(TAG, "county: " + county);
                                break;
                            }
                        }

                        if(county != null && ((type.equals("[\"administrative_area_level_2\"]") ||
                                (type.equals("[\"administrative_area_level_2\",\"political\"]"))))){
                            break;
                        }
                    }

                    Log.d(TAG, "outside");
                    Log.d(TAG, "county: " + county);

                    sb.append(county);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                    Log.d(TAG, "getting data from google");
                }
            }

            data = sb.toString();

            Log.d(TAG, "data: " + data);

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "result received ");
        }
    }


}
