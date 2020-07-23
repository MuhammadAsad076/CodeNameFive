package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

public class TripsWeekData {

    private int mImageResource;
    private String tripDate;
    private String tripsCompleted;
    private double tripCost;

    public TripsWeekData(int mImageResource, String tripDate, String tripsCompleted, double tripCost) {
        this.mImageResource = mImageResource;
        this.tripDate = tripDate;
        this.tripsCompleted = tripsCompleted;
        this.tripCost = tripCost;
    }//constructor

    //getter methods

    public int getImageResource() {
        return mImageResource;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getTripsCompleted() {
        return tripsCompleted;
    }

    public double getTripCost() {
        return tripCost;
    }
}//end class
