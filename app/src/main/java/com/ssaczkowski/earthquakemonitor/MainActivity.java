package com.ssaczkowski.earthquakemonitor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTask.DownloadEqsInterface {

    public static String SELECTED_EARTHQUAKE = "selectedEarthquake";
    private ListView earthquakeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);

        if (Utils.isNetworkAvailable(this)) {
            downloadEarthquakes();
        }else{
            getEarthquakesFromDb();
        }

    }

    private void getEarthquakesFromDb() {
        EqDbHelper eqDbHelper = new EqDbHelper(this);
        SQLiteDatabase database = eqDbHelper.getReadableDatabase();

        Cursor cursor = database.query(EqContract.EqColumns.TABLE_NAME,null,null,null,null,null,null);

        ArrayList<Earthquake> eqList = new ArrayList<>();

        while(cursor.moveToNext()){
            double magnitude = cursor.getDouble(EqContract.EqColumns.MAGNITUDE_COLUMN_INDEX);
            String place = cursor.getString(EqContract.EqColumns.PLACE_COLUMN_INDEX);
            Long time = cursor.getLong(EqContract.EqColumns.TIMESTAMP_COLUMN_INDEX);
            String longitude = cursor.getString(EqContract.EqColumns.LONGITUDE_COLUMN_INDEX);
            String latitude = cursor.getString(EqContract.EqColumns.LATITUDE_COLUMN_INDEX);
        }
        cursor.close();
        fillEqList(eqList);
    }

    private void downloadEarthquakes() {
        DownloadEqsAsyncTask downloadEqsAsyncTask = null;
        downloadEqsAsyncTask = new DownloadEqsAsyncTask(this);
        downloadEqsAsyncTask.delegate = this;

        try {
            downloadEqsAsyncTask.execute(new URL(getString(R.string.usgs_all_hour_earthquakes_url)));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onEqsDownloaded(ArrayList<Earthquake> eqList) {
       fillEqList(eqList);
    }

    private void fillEqList(ArrayList<Earthquake> eqList) {
        final EqAdapter eqAdapter = new EqAdapter(this, R.layout.eq_list_item, eqList);
        earthquakeListView.setAdapter(eqAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake selectedEarthquake = eqAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(SELECTED_EARTHQUAKE, selectedEarthquake);
                startActivity(intent);
            }
        });
    }
}
