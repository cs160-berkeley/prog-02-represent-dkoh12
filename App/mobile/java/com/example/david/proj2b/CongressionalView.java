package com.example.david.proj2b;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import io.fabric.sdk.android.Fabric;


public class CongressionalView extends AppCompatActivity {

    private static final String TAG = "@>@>@>@>";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String zipCode;

    private static ArrayList<Candidate> candidates = new ArrayList<>();

    private String url = "https://congress.api.sunlightfoundation.com/";
    private String api_key = "92ace5ed4e0247fc81c1607f25d89f9d";
    private String name;
    private String party;
    private String email;
    private String website;
    String watchToData = null;
    protected String county;

    protected Double latitude;
    protected Double longitude;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "rXtvhNR3T0ag8BfXpPPEwD4EW";
    private static final String TWITTER_SECRET = "9SSYcoP5aD68auv8Q1wveYVJwYlqbDq5PSIbfTUtQoaD4oM3JC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.candidate_list);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Intent intent = getIntent();
        zipCode = intent.getStringExtra("zipCode");
        Log.d(TAG, "zipCode is " + zipCode);

        if(intent.getStringExtra("zipcode") != null) {
            zipCode = intent.getStringExtra("zipcode");
            Log.d(TAG, "RESET zipCode is " + zipCode);

            //resend to watch

            try {
                String myUrl = url + "legislators/locate?zip=" + zipCode + "&apikey=" + api_key;
                watchToData = new RetrieveWatchData().execute(myUrl, "sun").get();
            } catch (Exception e) {
                Log.d(TAG, "failed watchToDATA");
            }

            String area = getLatLon(zipCode);
            String[] arr = area.split("@");
            latitude = Double.parseDouble(arr[0]);
            longitude = Double.parseDouble(arr[1]);

            Log.d(TAG, "get county 22");
            Log.d(TAG, "lat: " + latitude);
            Log.d(TAG, "lon: " + longitude);

            county = getCountyCode(latitude, longitude);
            watchToData = zipCode + "@" + county + "@" + watchToData;

            Log.d(TAG, "watchToData: " + watchToData);

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("dataToWatch", watchToData);
            startService(sendIntent);

        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getDataSet(zipCode);

        mAdapter = new MyAdapter(candidates, getBaseContext());
        mRecyclerView.setAdapter(mAdapter);

    }

    private String getLatLon(String zipcode){
        Geocoder mGeocoder = new Geocoder(this, Locale.US);
        String area = "";
        try{
            List<Address> addresses = mGeocoder.getFromLocationName(zipcode, 1);
            Address address = addresses.get(0);

            String lat = "" + address.getLatitude();
            String lon = "" + address.getLongitude();

            area = lat + "@" + lon;

        } catch (Exception e){
            Log.d(TAG, "getLatLon exception");
        }

        return area;
    }

    private String getCountyCode(double mLatitude, double mLongitude) {
        String mCounty = null;

        String myUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+mLatitude+","+mLongitude;

        try {
            mCounty = new RetrieveWatchData().execute(myUrl, "google").get();
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

    //from zip code get data
    public void getDataSet(String zipCode) {
        String myUrl = url + "legislators/locate?zip=" + zipCode + "&apikey=" + api_key;

        candidates = new ArrayList<>();

        try {
            candidates = new RetrieveData().execute(myUrl).get();
        } catch (Exception e) {
            Log.d(TAG, "getDataSet failed");
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyAdapter) mAdapter).setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                candidates = new ArrayList<Candidate>();
                getDataSet(zipCode);

                Intent i = new Intent(CongressionalView.this, DetailedView.class);

                Log.d(TAG, "calling Intent");
                Log.d(TAG, candidates.get(position).getName());

                i.putExtra("name", candidates.get(position).getName());
                startActivity(i);
            }
        });
    }

    public class RetrieveData extends AsyncTask<String, Void, ArrayList<Candidate>> {
        private static final String TAG = "@>@>@>@>";


        @Override
        protected ArrayList<Candidate> doInBackground(String... params) {
            //Log.d(TAG, "params " + params[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(params[0]);

            try {
                JSONArray arr = jsonObject.getJSONArray("results");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json = arr.getJSONObject(i);

                    String chamber = json.getString("chamber");

                    String bioguide_id = json.getString("bioguide_id");

                    String first_name = json.getString("first_name");
                    String last_name = json.getString("last_name");
                    name = first_name + " " + last_name;
                    party = json.getString("party");
                    email = json.getString("oc_email");
                    website = json.getString("website");
                    String cTweet = json.getString("twitter_id");

                    if (party.equals("D")) {
                        party = "Democrat";
                    } else {
                        party = "Republican";
                    }

                    String url = "https://theunitedstates.io/images/congress/450x550/" + bioguide_id + ".jpg";

                    //Log.d(TAG, "name: " + name + ", party: " + party + ",email: " + email + ",website: " + website);
                    Candidate c = new Candidate(name, party, email, website, cTweet, url);
                    candidates.add(c);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                Log.d(TAG, "JSON array failed");
            }
            return candidates;
        }

        @Override
        protected void onPostExecute(final ArrayList<Candidate> result) {
            final String[] ids = new String[result.size()];

            for(int i =0; i<result.size(); i++){
                ids[i] = result.get(i).getTweet();
            }

            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
            Fabric.with(CongressionalView.this, new Twitter(authConfig));

            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override
                public void success(Result<AppSession> appSessionResult) {
                    AppSession session = appSessionResult.data;
                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);

                    for (int i = 0; i < result.size(); i++) {
                        final String s = ids[i];
                        final int g = i;
                        final String name = result.get(g).getName();
                        final String tweet_name = result.get(g).getTweet();

                        Log.d(TAG, "g: " + g);
                        Log.d(TAG, "name: " + name);

                        twitterApiClient.getStatusesService().userTimeline(null, tweet_name, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> listResult) {

                                for (int j = 0; j < listResult.data.size(); j++) {
                                    Tweet tweet = listResult.data.get(j);

                                    String help = tweet.text + " " + tweet.createdAt;
                                    result.get(g).setTweet(help);

                                    Log.d(TAG, "name: " + name);
                                    Log.d(TAG, "result Name: " + result.get(g).getName());

                                    Log.d(TAG, "Name: " + name + " Tweet: " + result.get(g).getTweet());

                                    mAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void failure(TwitterException e) {
                                Log.d(TAG, "fail");
                                e.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                }
            });

            Log.d(TAG, "end");
        }
    }

    public class RetrieveWatchData extends AsyncTask<String, Void, String> {
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


                Log.d(TAG, "Google gets zip Code: " + zipCode);
                Log.d(TAG, "in google");

                try {
                    //feel like something is weird with this array
                    JSONArray arr = jsonObject.getJSONArray("results");

                    String county = null;
                    String type = null;

                    for (int i = 0; i < arr.length(); i++) {

                        Log.d(TAG, "iteration i: " + i);

                        JSONObject json = arr.getJSONObject(i);
                        JSONArray jArr = json.getJSONArray("address_components");

                        Log.d(TAG, "jARR length: " + jArr.length());

                        for(int j=0; j<jArr.length(); j++){
                            Log.d(TAG, "iteration j: " + j);

                            JSONObject o = jArr.getJSONObject(j);

                            String long_name = o.getString("long_name");
                            Log.d(TAG, long_name);

                            type = o.getString("types");
                            Log.d(TAG, "type: " + type);

                            if((type.equals("[\"administrative_area_level_2\"]") ||
                                    (type.equals("[\"administrative_area_level_2\",\"political\"]")))) {
                                county = o.getString("long_name");
                                Log.d(TAG, "county: " + county);
                                //break;
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

