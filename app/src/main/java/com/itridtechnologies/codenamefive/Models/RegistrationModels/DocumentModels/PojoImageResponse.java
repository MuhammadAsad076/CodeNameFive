package com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels;

import com.google.gson.annotations.SerializedName;

public class PojoImageResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ImgData imgData;

    @SerializedName("message")
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public ImgData getImgData() {
        return imgData;
    }

    public String getMsg() {
        return msg;
    }
}//mainClass
