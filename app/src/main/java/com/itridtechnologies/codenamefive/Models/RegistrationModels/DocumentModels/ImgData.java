package com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels;

import com.google.gson.annotations.SerializedName;

public class ImgData {

    @SerializedName("fileName")
    private FileDetails fileDetails;

    public FileDetails getFileDetails() {
        return fileDetails;
    }
}
