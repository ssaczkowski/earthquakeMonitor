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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);
        ArrayList<Earthquake> eqList = new ArrayList<>();

        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("4.6", "97 km S of Wonosari, Indonesia"));
        eqList.add(new Earthquake("2.3", "16 km S of Joshua Tree, CA"));
        eqList.add(new Earthquake("3.1", "97 km S of Wonosari, Indonesia"));


        EqAdapter eqAdapter = new EqAdapter(this, R.layout.eq_list_item, eqList);
        earthquakeListView.setAdapter(eqAdapter);

        DownloadEqsAsyncTask downloadEqsAsyncTask = null;
        downloadEqsAsyncTask = new DownloadEqsAsyncTask();
        downloadEqsAsyncTask.delegate = this;

        try {
            downloadEqsAsyncTask.execute(new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_hour.geojson"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onEqsDownloaded(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            JSONArray featureJsonArray = jsonObject.getJSONArray("features");

            for(int i = 0 ; i < featureJsonArray.length() ; i++) {
                JSONObject featureJsonObject = featureJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject = featureJsonObject.getJSONObject("properties");
                double magnitude = propertiesJsonObject.getDouble("mag");
                String place = propertiesJsonObject.getString("place");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
