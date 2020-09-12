package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import com.google.gson.annotations.SerializedName;

public class States {

    @SerializedName("stateId")
    private int id;

    @SerializedName("stateName")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
