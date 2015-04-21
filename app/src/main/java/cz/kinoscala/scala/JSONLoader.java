package cz.kinoscala.scala;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by petr on 20.4.2015.
 */
public class JSONLoader {
    public JSONObject getJsonFromUrl(String url){
        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        StringBuilder jsonString = new StringBuilder();

        try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                InputStream inputStreamContent = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamContent));

                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
            } else {
                Log.e("JSON Loader", "Couldn't load url. Got status code: " + Integer.toString(statusCode));
            }

        } catch (IOException e) {
            Log.e("JSON Loader", e.toString());
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString.toString());
        } catch (JSONException e) {
            Log.e("JSON Loader", "Incorrect JSON format: " + e.toString());
        }

        return jsonObject;
    }
}
