package com.itridtechnologies.codenamefive.Models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class GoogleMapsClusterItem implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int markerIcon;
    //private Partner partner;

    //empty constructor
    public GoogleMapsClusterItem() {
    }

    //constructor with params
    public GoogleMapsClusterItem(LatLng position, String title, String snippet, int markerIcon) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.markerIcon = markerIcon;
        //this.partner = partner;
    }

    //getters and setters...........................................................................
    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(int markerIcon) {
        this.markerIcon = markerIcon;
    }

//    public Partner getPartner() {
//        return partner;
//    }
//
//    public void setPartner(Partner partner) {
//        this.partner = partner;
//    }
}//end class
