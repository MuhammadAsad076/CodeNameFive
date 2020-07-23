package com.itridtechnologies.codenamefive.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

public final class NetworkConnectionState {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isNetworkConnected(Context context) {

        boolean isConnected = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isConnected = networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isConnected = networkInfo.isConnected();
            }
        }//end for
        return isConnected;
    }
}//end class
