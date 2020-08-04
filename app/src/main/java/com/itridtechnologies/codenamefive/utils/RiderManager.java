package com.itridtechnologies.codenamefive.utils;

public final class RiderManager {
    private static boolean RIDER_CONNECTED = false;

    //setters and getters

    public static boolean getRiderConnected() {
        return RIDER_CONNECTED;
    }

    public static void setRiderConnected(boolean riderConnected) {
        RiderManager.RIDER_CONNECTED = riderConnected;
    }
}
