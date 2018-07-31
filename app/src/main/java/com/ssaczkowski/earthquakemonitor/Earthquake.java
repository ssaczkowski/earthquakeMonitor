package com.ssaczkowski.earthquakemonitor;

public class Earthquake {

    private String magnitude;
    private String place;

    public Earthquake(String magnitude, String place) {
        this.magnitude = magnitude;
        this.place = place;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
