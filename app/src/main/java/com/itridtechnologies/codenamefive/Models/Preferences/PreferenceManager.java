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

    public void SaveProfileImgUri(String uri) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY_PROFILE_IMG),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("profile_img", uri);
        editor.apply();
        Log.d(TAG, "SaveProfileImgUri: saved in preferences...");
    }//end fun

    public String RetrieveProfileImg() {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY_PROFILE_IMG),
                Context.MODE_PRIVATE
        );
        return preferences.getString("profile_img", null);
    }//end fun

}
