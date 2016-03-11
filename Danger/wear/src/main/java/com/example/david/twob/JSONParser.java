package com.example.david.twob;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 3/7/16.
 */
public class JSONParser {

    private static final String TAG = "@>@>@>@>";

    private Context mContext;

    JSONArray sJSONArray = null;
    JSONObject sJSONObject = null;
    String result = "";

    public JSONParser() {
    }

    public JSONParser(Context context) {
        mContext = context;
    }

    public JSONObject getJSONFromUrl(String url) {
        try {
            Log.d(TAG, "trying get JSON from url");
            URL link = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) link.openConnection();
            connection.getResponseCode();
            InputStream stream = connection.getErrorStream();
            if(stream == null){
                stream = connection.getInputStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            result = sb.toString();

            sJSONObject = new JSONObject(result);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
            Log.d(TAG, "JSONParser failed");
        }

        return sJSONObject;
    }

    public JSONArray getJSONFromFile(String file) {

        try {
            InputStream stream = mContext.getAssets().open(file);


            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            result = new String(buffer, "UTF-8");

            sJSONArray = new JSONArray(result);

        } catch (Exception e) {
            Log.d(TAG, "getJSONFromFile err");
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }

        return sJSONArray;
    }


}
