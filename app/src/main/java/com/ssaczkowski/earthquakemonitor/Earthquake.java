package com.ssaczkowski.earthquakemonitor;

import android.os.Parcel;
import android.os.Parcelable;

public class Earthquake implements Parcelable {

    private double magnitude;
    private String place;
    private Long time;
    private String longitude;
    private String latitude;

    public Earthquake(double magnitude, String place, Long time, String longitude, String latitude) {
        this.magnitude = magnitude;
        this.place = place;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Earthquake(Parcel in) {
        this.magnitude = in.readDouble();
        this.place = in.readString();
        this.time = in.readLong();
        this.longitude = in.readString();
        this.latitude = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(magnitude);
        dest.writeString(place);
        dest.writeLong(time);
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
