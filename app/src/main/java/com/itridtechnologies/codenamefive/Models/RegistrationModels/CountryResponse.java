package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Countries> countries;

    public boolean isSuccess() {
        return success;
    }

    public List<Countries> getCountries() {
        return countries;
    }
}//end model

