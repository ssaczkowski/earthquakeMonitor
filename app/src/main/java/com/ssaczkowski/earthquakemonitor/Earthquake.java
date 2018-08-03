package com.ssaczkowski.earthquakemonitor;

import android.os.Parcel;
import android.os.Parcelable;

public class Earthquake implements Parcelable {

    private double magnitude;
    private String place;
    private long time;
    private String longitude;
    private String latitude;


    public Earthquake(double magnitude, String place, long time, String longitude, String latitude) {
        this.magnitude = magnitude;
        this.place = place;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long dateTime) {
        this.time = dateTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    protected Earthquake(Parcel in) {
        magnitude = in.readDouble();
        place = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(magnitude);
        dest.writeString(place);
        dest.writeDouble(time);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}
