package com.example.david.proj2b;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VoteView extends Activity {

    private static final String TAG = "@>@>@>@>";

    private String location;
    protected String state;
    protected String county;
    protected int obamaPercent;
    protected int romneyPercent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_vote_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            location = extras.getString("location");

            String arr[] = location.split("@");
            county = arr[0];
            state = arr[1];

            TextView stateElement = (TextView) findViewById(R.id.state);
            stateElement.setText(state);
            TextView countyElement = (TextView) findViewById(R.id.county);
            countyElement.setText(county);

            try {
                new RetrieveData().execute("vote2012.json");
            } catch (Exception e) {
                Log.d(TAG, "exception Retrieve Data");
            }

        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "VoteView Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.david.proj2b/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "VoteView Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.david.proj2b/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }


    public class RetrieveData extends AsyncTask<String, Void, Void> {
        private static final String TAG = "@>@>@>@>";

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "params " + params[0]);

            Log.d(TAG, "my state: " + state);
            Log.d(TAG, "my county: " + county);

            try {
                JSONParser parser = new JSONParser(getBaseContext());
                JSONArray jArr = (JSONArray) parser.getJSONFromFile(params[0]);

                Log.d(TAG, "JARR length: " + jArr.length());

                //why does proj2b stop?

                for (int i=0; i<jArr.length(); i++) {

                    JSONObject jsonObject = jArr.getJSONObject(i);

                    if (state.equals(jsonObject.getString("state-postal"))){

                        Log.d(TAG, "state: " + jsonObject.getString("state-postal"));
                        Log.d(TAG, "county: " + jsonObject.getString("county-name"));


                        if(county.equals(jsonObject.getString("county-name"))) {
                            obamaPercent = jsonObject.getInt("obama-percentage");
                            romneyPercent = jsonObject.getInt("romney-percentage");

                            Log.d(TAG, "obama: " + obamaPercent);
                            Log.d(TAG, "romney: " + romneyPercent);

                            break;
                        }
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, "unexpected JSON exception" + e.toString());
            }

            Log.d(TAG, "obama: " + obamaPercent);
            Log.d(TAG, "romney: " + romneyPercent);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //delegate.processFinish(result);
//            Log.d(TAG, "result received ");
//            super.onPostExecute(result);

            Log.d(TAG, "on post execute");
            Log.d(TAG, "obama %: " + obamaPercent);
            Log.d(TAG, "romney %: " + romneyPercent);

            TextView obama = (TextView) findViewById(R.id.obama_percentage);
            obama.setText("" + obamaPercent + "%");

            TextView romney = (TextView) findViewById(R.id.romney_percentage);
            romney.setText("" + romneyPercent + "%");

            View backgroundElement = (View) findViewById(R.id.vote_view_background);
            if (obamaPercent > romneyPercent) {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            } else {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.newRed));
            }

        }
    }
}
