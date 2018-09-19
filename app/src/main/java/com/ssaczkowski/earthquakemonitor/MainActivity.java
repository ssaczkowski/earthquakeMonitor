package com.ssaczkowski.earthquakemonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTask.DownloadEqsInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    public static String SELECTED_EARTHQUAKE = "selectedEarthquake";
    private ListView earthquakeListView;
    private GoogleApiClient googleApiClient;
    @Nullable
    private GoogleMap mMap;
    private Location userLocation;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLocationServices();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);

        //if (Utils.isNetworkAvailable(this)) {
            downloadEarthquakes();
        //} else {
          //  getEarthquakesFromDb();
        //}

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
        DownloadEqsAsyncTask downloadEqsAsyncTask = new DownloadEqsAsyncTask(this);
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
        verifyUserProximity(eqList);
        loadLastEarthquakesOnMap(eqList);
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
                userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
               // getUserLastLocation(userLocation);

            } else {
                final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            //getUserLastLocation(userLocation);

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
                builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
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
           // TextView locationTextView = (TextView) findViewById(R.id.main_activity_location_textView);

            Double longitude = userLocation.getLongitude();
            Double latitude = userLocation.getLatitude();
            try {
                geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();


               // locationTextView.setText(R.string.your_ubication + state + "|"+ address + "|"+postalCode+"|"+ knownName +" |" +city + " |" + country );

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    }

    private void loadLastEarthquakesOnMap(ArrayList<Earthquake> eqList) {
        if (eqList != null) {
            for (int i = 0; i < eqList.size(); i++) {
                LatLng latLong = new LatLng(eqList.get(i).getLatitude(), eqList.get(i).getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLong).title("Marker in " + eqList.get(i).getPlace()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            }
        }
    }

    private void verifyUserProximity(ArrayList<Earthquake> eqList) {
        if (userLocation != null) {
            Boolean proximity = false;
            int i = 0;

            while (i < eqList.size() || proximity != true) {

                Location locationActual = new Location("");
                locationActual.setLatitude(eqList.get(i).getLatitude());
                locationActual.setLongitude(eqList.get(i).getLongitude());

                int distanceToEarthquake = Math.round(locationActual.distanceTo(userLocation));

                if (distanceToEarthquake < 2000) {
                    //TextView proximityTextView = (TextView) findViewById(R.id.main_activity_proximity);

                    proximity = true;

                    //proximityTextView.setText("¿ Proximity?" + proximity);
                }


            }
        }

    }
}
