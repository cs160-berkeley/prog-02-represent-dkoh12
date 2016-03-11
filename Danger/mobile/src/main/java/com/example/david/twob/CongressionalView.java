package com.example.david.twob;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

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


//    RetrieveData asynchTask = new RetrieveData(new RetrieveData.AsyncResponse() {
//        @Override
//        public void processFinish(ArrayList<Candidate> output) {
//            //here will receive result from asynch class
//        }
//    }).execute();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.candidate_list);

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

        getDataSet(zipCode); //set candidates
//        for(Candidate c : candidates){
//            //Log.d(TAG, "name: " + c.getName());
//        }
        mAdapter = new MyAdapter(candidates);
        mRecyclerView.setAdapter(mAdapter);

//        // EMBED TWEET
//        final LinearLayout myLayout = (LinearLayout) findViewById(R.id.tweet);
//        // TODO: Use a more specific parent
//        //final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//        // TODO: Base this Tweet ID on some data from elsewhere in your app
//        long tweetId = 631879971628183552L;
//        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                TweetView tweetView = new TweetView(CongressionalView.this, result.data);
//                myLayout.addView(tweetView);
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Log.d("TwitterKit", "Load Tweet failure", exception);
//            }
//        });

//        TwitterSession session = Twitter.getSessionManager().getActiveSession();
//        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
//            @Override
//            public void success(Result<User> result) {
////                User user = userResult.data;
////                twitterImage = user.profileImageUrl;
//            }
//
//            @Override
//            public void failure(TwitterException e) {
//
//            }
//        });
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

        //Log.d(TAG, "back to getDataSet");

        //why is size = 0? when size = 3 when RetrieveData.execute(myUrl) is called
        // possibly threads run asynchronously?
        //Log.d(TAG, "size " + candidates.size());

//        for(Candidate c : candidates){
//            Log.d(TAG, "@@@ name: " + c.getName());
//        }
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

//        private Activity activity;
//
//        public RetrieveData(Activity activity) {
//            this.activity = activity;
//        }
//        public interface AsyncResponse {
//            void processFinish(ArrayList<Candidate> output);
//        }
//
//        public AsyncResponse delegate = null;
//
//        public RetrieveData(AsyncResponse delegate) {
//            this.delegate = delegate;
//        }

        @Override
        protected ArrayList<Candidate> doInBackground(String... params) {
            //Log.d(TAG, "params " + params[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(params[0]);

            try {
                JSONArray arr = jsonObject.getJSONArray("results");

                for (int i = 0; i < arr.length(); i++) {
                    //Log.d(TAG, "iteration " + i);
                    JSONObject json = arr.getJSONObject(i);

                    String chamber = json.getString("chamber");
                    String first_name = json.getString("first_name");
                    String last_name = json.getString("last_name");
                    name = first_name + " " + last_name;
                    party = json.getString("party"); //D or R
                    email = json.getString("oc_email");
                    website = json.getString("website");

                    if (party.equals("D")) {
                        party = "Democrat";
                    } else {
                        party = "Republican";
                    }

                    //Log.d(TAG, "name: " + name + ", party: " + party + ",email: " + email + ",website: " + website);
                    Candidate c = new Candidate(name, party, email, website, "first tweet", R.drawable.kevin_mccarthy);
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
        protected void onPostExecute(ArrayList<Candidate> result) {
            //delegate.processFinish(result);
//            for(Candidate c : result){
//                Log.d(TAG, "name: " + c.getName());
//            }
            //size = 3 here
            //Log.d(TAG, "post size " + result.size());

//            Log.d(TAG, "result received ");
//            super.onPostExecute(result);

        }
    }

}
