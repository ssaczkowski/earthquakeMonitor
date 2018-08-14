package com.ssaczkowski.earthquakemonitor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTask.DownloadEqsInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    public static String SELECTED_EARTHQUAKE = "selectedEarthquake";
    private ListView earthquakeListView;
    private GoogleApiClient googleApiClient;
    @Nullable
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLocationServices();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);

        if (Utils.isNetworkAvailable(this)) {
            downloadEarthquakes();
        } else {
            getEarthquakesFromDb();
        }

    }

    private void addLocationServices() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void getEarthquakesFromDb() {
        EqDbHelper eqDbHelper = EqDbHelper.getInstance(this);
        SQLiteDatabase database = eqDbHelper.getReadableDatabase();

        Cursor cursor = database.query(EqContract.EqColumns.TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Earthquake> eqList = new ArrayList<>();

        while (cursor.moveToNext()) {
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
        if (eqList.isEmpty()) {
            Toast.makeText(this, String.valueOf(R.string.error_msg_list_earthquake_empty), Toast.LENGTH_SHORT).show();
        } else {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                getUserLastLocation(userLocation);
            } else {
                final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            getUserLastLocation(userLocation);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {

            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_access_location_permission);
                builder.setMessage(R.string.info_msg_access_location_permission);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                        requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });

                builder.show();
            }
        }
    }

    private void getUserLastLocation(Location userLocation) {

        if (userLocation != null) {
            TextView locationTextView = (TextView) findViewById(R.id.main_activity_location_textView);
            String longitude = String.valueOf(userLocation.getLongitude());
            String latitude = String.valueOf(userLocation.getLatitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // LatLng userLatLong = new LatLng(MainActivity.getUserLocation().getLatitude(), MainActivity.getUserLocation().getLongitude());
        // mMap.addMarker(new MarkerOptions().position(userLatLong).title("Your Location !"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));
        Log.d("SA_LOG","Entre al metodo onMapReady");
        Log.i("SA_LOG","Entre al metodo onMapReady");
        Log.v("SA_LOG","Entre al metodo onMapReady");

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
