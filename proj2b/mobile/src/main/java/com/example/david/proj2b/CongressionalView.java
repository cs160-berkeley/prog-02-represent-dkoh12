package com.example.david.proj2b;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

//import com.twitter.sdk.android.Twitter;
//import com.twitter.sdk.android.core.*;
//import com.twitter.sdk.android.core.identity.TwitterLoginButton;
//import com.twitter.sdk.android.core.models.Tweet;
//import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import io.fabric.sdk.android.Fabric;

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

    //private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
//
//        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
//            @Override
//            public void success(Result<AppSession> appSessionResult) {
//                AppSession session = appSessionResult.data;
//                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
//                twitterApiClient.getStatusesService().userTimeline(null, "elonmusk", 10, null, null, false, false, false, true, new Callback<List<Tweet>>() {
//                    @Override
//                    public void success(Result<List<Tweet>> listResult) {
//                        for (Tweet tweet : listResult.data) {
//                            Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
//                        }
//                    }
//
//                    @Override
//                    public void failure(TwitterException e) {
//                        e.printStackTrace();
//                    }
//                });
//            }
//
//            @Override
//            public void failure(TwitterException e) {
//                e.printStackTrace();
//            }
//        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getDataSet(zipCode);
        mAdapter = new MyAdapter(candidates);
        mRecyclerView.setAdapter(mAdapter);


//        TwitterSession session = Twitter.getSessionManager().getActiveSession();
//        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
//            @Override
//            public void success(Result<User> result) {
//                User user = userResult.data;
//                twitterImage = user.profileImageUrl;
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

        public String[] temp_id;
        public ArrayList<String> store_tweets;
        public String c;

        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

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
                    String first_name = json.getString("first_name");
                    String last_name = json.getString("last_name");
                    name = first_name + " " + last_name;
                    party = json.getString("party");
                    email = json.getString("oc_email");
                    website = json.getString("website");
                    //String cTweet = json.getString("twitter_id");

                    if (party.equals("D")) {
                        party = "Democrat";
                    } else {
                        party = "Republican";
                    }

                    //Log.d(TAG, "name: " + name + ", party: " + party + ",email: " + email + ",website: " + website);
                    Candidate c = new Candidate(name, party, email, website, "hi", R.drawable.kevin_mccarthy);
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
//            final String[] ids = new String[result.size()];
//
//            for(int i =0; i<result.size(); i++){
//                ids[i] = result.get(i).getTweet();
//            }
//
//            Fabric.with(CongressionalView.this, new Twitter(authConfig));
//
////            TwitterSession session = Twitter.getSessionManager().getActiveSession();
////            Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
////                @Override
////                public void success(Result<User> result) {
////                    User user = userResult.data;
////                    twitterImage = user.profileImageUrl;
////                }
////
////                @Override
////                public void failure(TwitterException e) {
////
////                }
////            });
//
//            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//            StatusesService statusesService = twitterApiClient.getStatusesService();
//
//            for (int i=0; i<ids.length; i++) {
//                final String s = ids[i];
//
//                statusesService.userTimeline(null, s, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
//                    @Override
//                    public void success(Result<List<Tweet>> listResult) {
//
//                        for (int i = 0; i < listResult.data.size(); i++) {
//                            Tweet tweet = listResult.data.get(i);
//
//
//                            Log.d(TAG, "tweet: " + tweet.text + " " + tweet.createdAt);
//                        }
//
//                    }
//
//                    @Override
//                    public void failure(TwitterException e) {
//                        Log.d(TAG, "failed");
//                    }
//                });
//            }
//
//            Log.d(TAG, "end");

        }
    }

}




//            //delegate.processFinish(result);
////            super.onPostExecute(result);
//            temp_id = new String[result.size()];
//            store_tweets = new ArrayList<>();
//            final String[][] help = new String[10][2];
//
//            final Store_Tweet[] ahh = new Store_Tweet[5];
//
//            final String[] blackpig = new String[5];
//
//            //final String[] tuple = new String[2];
//
//            Log.d(TAG, "result received: " + result);
//            for(int j=0; j<result.size(); j++){
//                c = result.get(j).getTweet();
//                temp_id[j] = c;
//                Log.d(TAG, "tweet_id: " + c);
//            }
//
//            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//            Fabric.with(CongressionalView.this, new Twitter(authConfig));
//
//            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
//
//                @Override
//                public void success(Result<AppSession> appSessionResult) {
//                    AppSession session = appSessionResult.data;
//                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
//
//                    final String[][] fuck = new String[10][2];
//                    final String[] shit = new String[5];
//                    final String[] barbie = new String[5];
//
//                    final Store_Tweet st = new Store_Tweet();
//
//                    for (int j = 0; j < temp_id.length; j++) {
//                        String s = temp_id[j];
//                        final String x = s;
//                        final int g = j;
//                        shit[g] = s;
//
//                        st.setName(s);
//
//                        //UID, screen_name, county, since_id, max_id, trim_user, exclude_replies, contribute_details, include_rts, callback
//                        twitterApiClient.getStatusesService().userTimeline(null, s, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
//
//
//                            @Override
//                            public void success(Result<List<Tweet>> listResult) {
//                                Log.d(TAG, "String s: " + x);
//                                store_tweets.add(x);
//
//                                st.setTweet(x);
//                                help[g][0] = x;
//                                fuck[g][0] = x;
//
//                                Log.d(TAG, "iteration g: " + g);
//
//                                for (int k = 0; k < listResult.data.size(); k++) {
//                                    Tweet tweet = listResult.data.get(k);
//
//                                    fuck[g][1] = tweet.text + " " + tweet.createdAt;
//                                    help[g][1] = tweet.text + " " + tweet.createdAt;
//
//                                    store_tweets.add(tweet.text + " " + tweet.createdAt);
//                                    Log.d(TAG, "fabric stuff: " + tweet.text + " " + tweet.createdAt);
//                                }
//
//                            }
//
//                            @Override
//                            public void failure(TwitterException e) {
//                                e.printStackTrace();
//                                Log.d(TAG, "fail");
//                            }
//                        });
//
////
//////                        for (int bullshit=0;bullshit<5; bullshit++){
//////                            Log.d(TAG, "shit!! " + shit[bullshit]);
//////                        }
////
////                        ahh[j] = st;
////
//                    }
////
////                    for (int bullshit = 0; bullshit < 5; bullshit++) {
////                        blackpig[bullshit] = shit[bullshit];
////                        Log.d(TAG, "shit!! " + shit[bullshit]);
////                    }
////
////                    Log.d(TAG, "inner");
////                    for(int l=0; l<5;l++){
////                        Log.d(TAG, ahh[l].toString());
////                    }
////
//                }
//
//                //
//                @Override
//                public void failure(TwitterException e) {
//                    e.printStackTrace();
//                }
//            }
//            });