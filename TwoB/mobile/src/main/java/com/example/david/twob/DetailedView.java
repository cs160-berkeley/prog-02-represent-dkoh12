package com.example.david.twob;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedView extends AppCompatActivity {
    public String myName;
    public String myParty;
    public int myPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            myName = extras.getString("name");
            myParty = extras.getString("party");
            myPhoto = extras.getInt("photo");
        }

        ImageView photoElement = (ImageView) findViewById(R.id.detail_photo);
        photoElement.setImageResource(myPhoto);

        TextView nameElement = (TextView) findViewById(R.id.detail_name);
        nameElement.setText(myName);

        TextView partyElement = (TextView) findViewById(R.id.detail_party);
        partyElement.setText(myParty);


        View backgroundElement = (View) findViewById(R.id.background_color);
        //v.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
        if(myParty.equals("Republican")) {
            backgroundElement.setBackgroundColor(getResources().getColor(R.color.newRed));
        } else if(myParty.equals("Democrat")) {
            backgroundElement.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        }


    }

}
