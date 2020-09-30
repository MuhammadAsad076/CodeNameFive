package com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels;

import android.net.Uri;

public final class DocumentUriModel {
    public static Uri frontDoc;
    public static Uri backDoc;
    public static Uri addressDoc;

    public Uri getFrontDoc() {
        return frontDoc;
    }

    public Uri getBackDoc() {
        return backDoc;
    }


    public Uri getAddressDoc() {
        return addressDoc;
    }

}
