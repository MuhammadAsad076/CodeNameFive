package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

public class TripsHistory {

    private int mImageResource;
    private String fromDate;
    private String toDate;
    private double earnedRupees;

    public TripsHistory(int mImageResource, String fromDate, String toDate, double earnedRupees) {
        this.mImageResource = mImageResource;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.earnedRupees = earnedRupees;
    }//constructor

    //getterMethods

    public int getImageResource() {
        return mImageResource;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public double getEarnedRupees() {
        return earnedRupees;
    }
}//end class
