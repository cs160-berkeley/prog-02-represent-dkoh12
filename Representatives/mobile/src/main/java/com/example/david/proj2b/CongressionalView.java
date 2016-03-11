package com.example.david.proj2b;

import android.content.Intent;
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
            Log.d(TAG, "RESET zipcode");
            zipCode = intent.getStringExtra("zipcode");

            //resend to watch
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("zipCode", zipCode);
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

            String picUrl = "https://theunitedstates.io/images/congress/"; //[size]/[bioguide].jpg"
            //new getPhoto().execute(picUrl);

        }
    }

//    public class getPhoto extends AsyncTask<String, Void, ArrayList<Candidate>> {
//        private static final String TAG = "@>@>@>@>";
//
//        @Override
//        protected ArrayList<Candidate> doInBackground(String... params) {
//            Log.d(TAG, "params: " + params[0]);
//
//            String size = "225x275";
//
//
//
//
//            return null;
//        }
//    }
}

