package com.itridtechnologies.codenamefive.RetrofitInterfaces;

import com.itridtechnologies.codenamefive.Models.RegistrationModels.CountryResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.StatesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PartnerRegistrationApi {

    @GET("country")
    Call<CountryResponse> getAllCountries();

    @GET("state/{id}")
    Call<StatesResponse> getAllStates(@Path("id") int stateId);

}//end interface
