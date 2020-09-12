package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StatesResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<States> statesList;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<States> getStatesList() {
        return statesList;
    }
}
