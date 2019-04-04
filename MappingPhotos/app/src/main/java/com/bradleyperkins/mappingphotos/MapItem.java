package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// MapItem.Java

import java.io.Serializable;

public class MapItem implements Serializable {

    private double currLongitude;
    private double currLatitude;
    private String photoTaken;
    private String note;
    private String title;


    public MapItem(double currLongitude, double currLatitude, String note, String photoTaken, String title) {
        this.currLongitude = currLongitude;
        this.currLatitude = currLatitude;
        this.note = note;
        this.photoTaken = photoTaken;
        this.title = title;
    }

    public double getCurrLongitude() {
        return currLongitude;
    }

    public void setCurrLongitude(double currLongitude) {
        this.currLongitude = currLongitude;
    }

    public double getCurrLatitude() {
        return currLatitude;
    }

    public void setCurrLatitude(double currLatitude) {
        this.currLatitude = currLatitude;
    }

    public String getPhotoTaken() {
        return photoTaken;
    }

    public void setPhotoTaken(String photoTaken) {
        this.photoTaken = photoTaken;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
