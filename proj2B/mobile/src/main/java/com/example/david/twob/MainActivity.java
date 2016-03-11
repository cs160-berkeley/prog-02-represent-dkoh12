package com.example.david.twob;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@>@>@>@>";

    private Button mEnterButton;
    private EditText mZipCode;
    private CheckBox mCurrentLocation;
    private String zipCode;
    private Boolean check = Boolean.FALSE;

//    public static ArrayList<Candidate> sCandidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZipCode = (EditText) findViewById(R.id.EnterZipCode);
        mCurrentLocation = (CheckBox) findViewById(R.id.checkBox);

        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    check = Boolean.TRUE;
                    zipCode = "94704";
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
                    Log.d(TAG, "zipCode: " + zipCode);
                    Toast.makeText(MainActivity.this, "Not a valid ZipCode", Toast.LENGTH_SHORT).show();
                } else {
                    startCongress();
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
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra("zipCode", zipCode);
        startService(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
