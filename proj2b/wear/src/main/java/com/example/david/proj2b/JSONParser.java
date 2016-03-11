package com.example.david.proj2b;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
