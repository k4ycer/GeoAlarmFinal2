package com.example.k4ycer.geoalarm.model;

import com.google.android.gms.maps.model.LatLng;

public class Element {
    private String name;
    private String description;
    private LatLng latLng;
    private Boolean status;

    public Element(String name, String description, LatLng latLng, Boolean status) {
        this.name = name;
        this.description = description;
        this.latLng = latLng;
        this.status = status;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
