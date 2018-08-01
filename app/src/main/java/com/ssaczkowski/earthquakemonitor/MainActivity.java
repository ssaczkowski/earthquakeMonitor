package com.ssaczkowski.earthquakemonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTask.DownloadEqsInterface{

    private ListView earthquakeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);

        DownloadEqsAsyncTask downloadEqsAsyncTask = null;
        downloadEqsAsyncTask = new DownloadEqsAsyncTask();
        downloadEqsAsyncTask.delegate = this;

        try {
            downloadEqsAsyncTask.execute(new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onEqsDownloaded(String data) {
        ArrayList<Earthquake> eqList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);

            JSONArray featureJsonArray = jsonObject.getJSONArray("features");

            for(int i = 0 ; i < featureJsonArray.length() ; i++) {

                JSONObject featureJsonObject = featureJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject = featureJsonObject.getJSONObject("properties");

                double magnitude = propertiesJsonObject.getDouble("mag");
                String place = propertiesJsonObject.getString("place");

                eqList.add(new Earthquake(magnitude, place));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        EqAdapter eqAdapter = new EqAdapter(this, R.layout.eq_list_item, eqList);
        earthquakeListView.setAdapter(eqAdapter);
    }
}
