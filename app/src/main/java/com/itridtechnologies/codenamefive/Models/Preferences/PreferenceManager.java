package com.itridtechnologies.codenamefive.Models.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.itridtechnologies.codenamefive.R;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";

    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public void saveVehicleNumber(String number) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY_VEHICLE_NUM),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("profile_img", number);
        editor.apply();
        Log.d(TAG, "SaveProfileImgUri: saved in preferences...");
    }//end fun

    public String retrieveVehicleNumber() {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY_VEHICLE_NUM),
                Context.MODE_PRIVATE
        );
        return preferences.getString("vehicle_num", null);
    }//end fun

}
