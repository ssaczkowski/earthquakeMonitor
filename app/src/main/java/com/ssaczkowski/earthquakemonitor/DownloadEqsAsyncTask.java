package com.ssaczkowski.earthquakemonitor;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ssaczkowski.earthquakemonitor.R.*;

public class DownloadEqsAsyncTask extends AsyncTask<URL,Void,ArrayList<Earthquake>> {

    public DownloadEqsInterface delegate;
    private Context context;

    public DownloadEqsAsyncTask(Context context) {
        this.context = context;
    }

    public interface DownloadEqsInterface{
        void onEqsDownloaded(ArrayList<Earthquake> eqList);
    }

    @Override
    protected ArrayList<Earthquake> doInBackground(URL... urls) {
        String data = "";
        ArrayList<Earthquake> eqList = null;
        try {
            data = downloadData(urls[0]);

            eqList = parseDataFromJson(data);

            saveEqsOnDatabase(eqList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return eqList;
    }

    private void saveEqsOnDatabase(ArrayList<Earthquake> eqList) {
        EqDbHelper eqDbHelper = new EqDbHelper(context);
        SQLiteDatabase database = eqDbHelper.getWritableDatabase();

        for (Earthquake earthquake : eqList){
            ContentValues contentValues = new ContentValues();
            contentValues.put(EqContract.EqColumns.MAGNITUDE,earthquake.getMagnitude());
            contentValues.put(EqContract.EqColumns.LATITUDE,earthquake.getLatitude());
            contentValues.put(EqContract.EqColumns.LONGITUDE,earthquake.getLongitude());
            contentValues.put(EqContract.EqColumns.PLACE,earthquake.getPlace());
            contentValues.put(EqContract.EqColumns.TIMESTAMP,earthquake.getTime());

            database.insert(EqContract.EqColumns.TABLE_NAME,null,contentValues);
        }

    }

    private ArrayList<Earthquake> parseDataFromJson(String data) {
        ArrayList<Earthquake> eqList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);

            JSONArray featureJsonArray = jsonObject.getJSONArray("features");

            for(int i = 0 ; i < featureJsonArray.length() ; i++) {

                JSONObject featureJsonObject = featureJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject = featureJsonObject.getJSONObject("properties");

                double magnitude = propertiesJsonObject.getDouble("mag");
                String place = propertiesJsonObject.getString("place");
                long time = propertiesJsonObject.getLong("time");


                JSONObject geometryJsonObject = featureJsonObject.getJSONObject("geometry");
                JSONArray coordinatesJsonArray = geometryJsonObject.getJSONArray("coordinates");

                String longitude = coordinatesJsonArray.getString(0);
                String latitude = coordinatesJsonArray.getString(1);

                eqList.add(new Earthquake(magnitude, place, time, longitude, latitude));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eqList;
    }

    @Override
    protected void onPostExecute(ArrayList<Earthquake> eqList) {
        super.onPostExecute(eqList);

        delegate.onEqsDownloaded(eqList);
    }

    private String downloadData(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStrem) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStrem != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStrem, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
