package com.example.david.proj2b;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailedView extends AppCompatActivity {
    public String myName;

    private static final String TAG = "@>@>@>@>";
    private String api_key= "92ace5ed4e0247fc81c1607f25d89f9d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            myName = extras.getString("name");
            Log.d(TAG, "Detailed View name: " + myName);

            TextView nameElement = (TextView) findViewById(R.id.detail_name);
            nameElement.setText(myName);

            String name[] = myName.split(" ");
            String firstName = name[0];
            String lastName = name[1];

            String url = "https://congress.api.sunlightfoundation.com/legislators?first_name="+firstName+"&last_name="+lastName+"&apikey="+api_key;

//            ImageView photoElement = (ImageView) findViewById(R.id.detail_photo);
//            photoElement.setImageResource(R.drawable.kevin_mccarthy);

            new RetrieveDetailedData().execute(url);

        }
    }

    public class RetrieveDetailedData extends AsyncTask<String, Void, ArrayList<String>> {
        private static final String TAG = "@>@>@>@>";
        ArrayList<String> detailedList = new ArrayList<>();
        String bioID= null;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.d(TAG, params[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl(params[0]);

            try {
                JSONArray arr = jsonObject.getJSONArray("results");
                JSONObject json = arr.getJSONObject(0);

                //String chamber = json.getString("chamber");
                bioID = json.getString("bioguide_id");
                Log.d(TAG, "bioID = " + bioID);
                String party = json.getString("party"); //D or R
                String term_end = json.getString("term_end");

                if (party.equals("D")) {
                    party = "Democrat";
                } else {
                    party = "Republican";
                }

                detailedList.add(bioID);
                detailedList.add(party);
                detailedList.add(term_end);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                Log.d(TAG, "JSON array failed");
            }

            try {
                String url = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + bioID + "&apikey=" + api_key;
                JSONObject committeeJSONObject = jsonParser.getJSONFromUrl(url);

                JSONArray arr = committeeJSONObject.getJSONArray("results");

                detailedList.add(arr.length() + "");
                for(int i=0; i < arr.length(); i++){
                    JSONObject committeeJSON= arr.getJSONObject(i);
                    detailedList.add(committeeJSON.getString("name"));
                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, "committee array failed");
            }

            try {
                String url = "https://congress.api.sunlightfoundation.com/bills?sponsor_id="+bioID+"&apikey="+api_key;
                JSONObject billJSONObject = jsonParser.getJSONFromUrl(url);

                JSONArray arr = billJSONObject.getJSONArray("results");

                detailedList.add( 2 * arr.length() + "");
                for(int i=0; i < arr.length(); i++){
                    JSONObject billJSON= arr.getJSONObject(i);
                    detailedList.add(billJSON.getString("introduced_on"));
                    detailedList.add(billJSON.getString("official_title"));
                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, "bill array failed");
            }

            return detailedList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            //delegate.processFinish(result);
//            super.onPostExecute(result);

            TextView endDate = (TextView) findViewById(R.id.end_date);
            endDate.setText(result.get(2));

            TextView partyElement = (TextView) findViewById(R.id.detail_party);
            partyElement.setText(result.get(1));

            View backgroundElement = (ScrollView) findViewById(R.id.background_color);
            //v.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
            if (result.get(1).equals("Republican")) {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.newRed));
            } else {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            }

            String s = detailedList.get(3);

            TableLayout tableLayout = (TableLayout) findViewById(R.id.detail_committee_list);
            TableRow tableRows[] = new TableRow[Integer.parseInt(s)];
            for(int i = 0;i < tableRows.length; i++){
                tableRows[i] = new TableRow(getBaseContext());
                tableRows[i].setLayoutParams(new ActionBar.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));

                TextView text = new TextView(getBaseContext());
                text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
//                text.setTextColor(Color.parseColor("#000000"));
                text.setText(i+1 + " " + detailedList.get(i + 4));
                tableRows[i].addView(text);

                tableLayout.addView(tableRows[i]);
            }

            String bs = detailedList.get(4 + Integer.parseInt(s));
            int start  = 5 + Integer.parseInt(s);

            Log.d(TAG, bs);

            TableLayout billLayout = (TableLayout) findViewById(R.id.detail_bill);
            TableRow billRows[] = new TableRow[Integer.parseInt(bs)];
            for(int i=0; i < billRows.length; i++) {
                billRows[i] = new TableRow(getBaseContext());
                billRows[i].setLayoutParams(new ActionBar.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));

                TextView bill = new TextView(getBaseContext());
                bill.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                billRows[i].setPadding(0, 10, 0, 10);

                if( i%2 == 0 ) {
                    //bill.setTextColor(Color.parseColor("#ffff00"));
                    bill.setTextColor(Color.parseColor("#8aeabd"));
                    bill.setText(detailedList.get(i + start));
                } else {
                    bill.setText((i / 2) + 1 + " " + detailedList.get(i + start));
                }

                billRows[i].addView(bill);
                billLayout.addView(billRows[i]);

            }

            Log.d(TAG, "bio ID: " + result.get(0));

            String photoUrl = "https://theunitedstates.io/images/congress/450x550/"+result.get(0)+".jpg";
            ImageView image = (ImageView) findViewById(R.id.detail_photo);
            Picasso.with(getBaseContext()).load(photoUrl).into(image);

        }
    }
}
