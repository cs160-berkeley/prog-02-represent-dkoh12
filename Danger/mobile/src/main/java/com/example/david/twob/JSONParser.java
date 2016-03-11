package com.example.david.twob;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 3/7/16.
 */
public class JSONParser {

    private static final String TAG = "@>@>@>@>";

    JSONObject sJSONObject = null;
    String result = "";

    public JSONParser() {}

    public JSONObject getJSONFromUrl(String url) {
        Log.d(TAG, "url " + url);

        try {
            Log.d(TAG, "trying get JSON from url");

            URL link = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) link.openConnection();
            int a = connection.getResponseCode();
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
}
