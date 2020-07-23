package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

public class TripsDayData {
    //member variables
    private int mImageResource;
    private String restaurantName;
    private String tripStartTime;
    private String tripEndTime;
    private String tripTip;
    private double earnedPerTrip;

    public TripsDayData(int mImageResource, String restaurantName, String tripStartTime, String tripEndTime, String tripTip, double earnedPerTrip) {
        this.mImageResource = mImageResource;
        this.restaurantName = restaurantName;
        this.tripStartTime = tripStartTime;
        this.tripEndTime = tripEndTime;
        this.tripTip = tripTip;
        this.earnedPerTrip = earnedPerTrip;
    }//end constructor

    //getter methods

    public int getImageResource() {
        return mImageResource;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getTripStartTime() {
        return tripStartTime;
    }

    public String getTripEndTime() {
        return tripEndTime;
    }

    public String getTripTip() {
        return tripTip;
    }

    public double getEarnedPerTrip() {
        return earnedPerTrip;
    }
}//end class
