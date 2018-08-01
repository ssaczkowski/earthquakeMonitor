package com.ssaczkowski.earthquakemonitor;

public class Earthquake {

    private double magnitude;
    private String place;

    public Earthquake(double magnitude, String place) {
        this.magnitude = magnitude;
        this.place = place;
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
}
