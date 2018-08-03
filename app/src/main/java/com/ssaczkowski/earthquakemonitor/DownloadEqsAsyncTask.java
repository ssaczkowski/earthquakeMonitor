package com.ssaczkowski.earthquakemonitor;

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

public class DownloadEqsAsyncTask extends AsyncTask<URL,Void,ArrayList<Earthquake>> {

    public DownloadEqsInterface delegate;

    public interface DownloadEqsInterface{
        void onEqsDownloaded(ArrayList<Earthquake> eqList);
    }

    @Override
    protected ArrayList<Earthquake> doInBackground(URL... urls) {
        String data = "";
        ArrayList<Earthquake> eqList = null;
        try {
            data = downloadData(urls[0]);
            Log.d("LOG SABRI:",data);
            eqList = parseDataFromJson(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eqList;
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
