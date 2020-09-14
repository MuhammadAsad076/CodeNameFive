package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

public class Cities {

    @SerializedName("cityId")
    private int id;

    @SerializedName("cityName")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
