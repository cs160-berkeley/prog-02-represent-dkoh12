package com.example.david.twob;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class DetailedView extends AppCompatActivity {
    public String myName;
    public String myParty;
    public int myPhoto;

    private static final String TAG = "@>@>@>@>";
    private static Map<String, Integer> photoMap;
    private static Map<String, String> partyMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        photoMap = PicMap();
        partyMap = PartyMap();

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            myName = extras.getString("name");
            Log.d(TAG, "name: " + myName);

            ImageView photoElement = (ImageView) findViewById(R.id.detail_photo);
            myPhoto = photoMap.get(myName);
            photoElement.setImageResource(myPhoto);

            TextView nameElement = (TextView) findViewById(R.id.detail_name);
            nameElement.setText(myName);

            TextView partyElement = (TextView) findViewById(R.id.detail_party);
            myParty = PartyMap().get(myName);
            partyElement.setText(myParty);


            View backgroundElement = (View) findViewById(R.id.background_color);
            //v.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
            if (myParty.equals("Republican")) {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.newRed));
            } else if (myParty.equals("Democrat")) {
                backgroundElement.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            }
        }
    }

    private HashMap<String, Integer> PicMap() {
        HashMap<String, Integer> dic = new HashMap<>();
        dic.put("Kevin McCarthy", R.drawable.kevin_mccarthy);
        dic.put("Barbara Boxer", R.drawable.senator_barbara_boxer);
        dic.put("Dianne Feinstein", R.drawable.senator_dianne_feinstein);
        dic.put("Cory Gardner", R.drawable.cory_gardner);
        dic.put("Michael Bennet", R.drawable.michael_bennet);
        dic.put("Mike Coffman", R.drawable.mike_coffman);
        return dic;
    }

    private HashMap<String, String> PartyMap() {
        HashMap<String, String> dic = new HashMap<>();
        dic.put("Kevin McCarthy", "Republican");
        dic.put("Barbara Boxer", "Democrat");
        dic.put("Dianne Feinstein", "Democrat");
        dic.put("Cory Gardner", "Republican");
        dic.put("Michael Bennet", "Democrat");
        dic.put("Mike Coffman", "Republican");
        return dic;
    }

}
