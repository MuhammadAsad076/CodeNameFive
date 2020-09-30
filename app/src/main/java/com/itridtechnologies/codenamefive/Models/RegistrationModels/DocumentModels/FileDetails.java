package com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels;

import com.google.gson.annotations.SerializedName;

public class FileDetails {

    @SerializedName("filename")
    private String name;

    public String getName() {
        return name;
    }

}
