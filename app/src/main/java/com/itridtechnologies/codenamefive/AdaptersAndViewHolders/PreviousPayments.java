package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

public class PreviousPayments {

    //member variables
    private int mImageResource;
    private String fromDate;
    private String toDate;
    private String paymentStatus;
    private double paymentTotal;

    public PreviousPayments(int mImageResource, String fromDate, String toDate, String paymentStatus, double paymentTotal) {
        this.mImageResource = mImageResource;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.paymentStatus = paymentStatus;
        this.paymentTotal = paymentTotal;
    }//constructor

    //member functions
    //getter methods

    public int getImageResource() {
        return mImageResource;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public double getPaymentTotal() {
        return paymentTotal;
    }
}//end class
