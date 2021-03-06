package com.ssaczkowski.earthquakemonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        Earthquake earthquake = extras.getParcelable(MainActivity.SELECTED_EARTHQUAKE);

        if(earthquake != null){
            TextView magnitudeTextView = (TextView) findViewById(R.id.eq_detail_magnitude);
            TextView longitudeTextView = (TextView) findViewById(R.id.eq_detail_longitude);
            TextView latitudeTextView = (TextView) findViewById(R.id.eq_detail_latitude);
            TextView placeTextView = (TextView) findViewById(R.id.eq_detail_place);
            TextView dateTimeTextView = (TextView) findViewById(R.id.eq_detail_dateTime);

            magnitudeTextView.setText(String.valueOf(earthquake.getMagnitude()));
            longitudeTextView.setText(String.valueOf(earthquake.getLongitude()));
            latitudeTextView.setText(String.valueOf(earthquake.getLatitude()));
            placeTextView.setText(String.valueOf(earthquake.getPlace()));
            dateTimeTextView.setText(getStringDateFromTimestamp(earthquake.getTime()));
        }
    }
    private String getStringDateFromTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFromat = new SimpleDateFormat("dd/MMM/yyyy - HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);

        return simpleDateFromat.format(date);
    }

}
