package com.itridtechnologies.codenamefive.Models;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolyLinesData {

    private Polyline polyline;
    private DirectionsLeg leg;

    public PolyLinesData(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    @Override
    public String toString() {
        return "PolylineData{" +
                "polyline=" + polyline +
                ", leg=" + leg +
                '}';
    }
}
