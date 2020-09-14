package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CityResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<Cities> citiesList;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<Cities> getCitiesList() {
        return citiesList;
    }
}
