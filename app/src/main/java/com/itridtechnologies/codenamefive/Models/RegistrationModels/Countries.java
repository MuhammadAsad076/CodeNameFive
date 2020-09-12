package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

public class Countries {

    @SerializedName("countryId")
    private int id;
    @SerializedName("countryName")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
