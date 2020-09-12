package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.itridtechnologies.codenamefive.Models.RegistrationModels.Countries;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.CountryResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.States;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.StatesResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterSecondStep extends AppCompatActivity implements View.OnClickListener {

    //constants
    private static final String TAG = "RegisterSecondStep";
    private static final String BASE_URL = "http://ec2-18-222-200-202.us-east-2.compute.amazonaws.com:3000/api/v1/";
    //vars
    PartnerRegistrationApi mPartnerRegistrationApi;
    //ui views
    private EditText mEditTextBirthDate;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private ProgressBar mProgressBar;
    private Spinner mSpinnerCountries;
    private Spinner mSpinnerStates;

    private List<Countries> mCountries;
    private List<String> mCountryNames;

    private List<States> mStates;
    private List<String> mStateNames;

    private int mCountryId = 0;
    private int mStateId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_step);

        //find views
        mEditTextBirthDate = findViewById(R.id.edt_date_of_birth);
        mSpinnerCountries = findViewById(R.id.spinner_countries);
        mSpinnerStates = findViewById(R.id.spinner_states);
        mProgressBar = findViewById(R.id.progress_bar);

        //setting styles
        mEditTextBirthDate.setInputType(InputType.TYPE_NULL);
        mStateNames = new ArrayList<>();

        //set listeners
        mEditTextBirthDate.setOnClickListener(this);

        String s;
        //date set listener
        mOnDateSetListener = (datePicker, i, i1, i2) ->
        mEditTextBirthDate.setText(String.format("%d-%d-%d", i, i1, i2)
        );

        //country spinner listener
        mSpinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mCountryId = (int) adapterView.getItemIdAtPosition(i);
                //get country id by name

                if (mCountryId == 0) {
                    Log.d(TAG, "onItemSelected: country id: " + mCountryId);
                    mStateNames.add("State");
                    setUpStatesSpinner();

                } else {
                    Log.d(TAG, "onItemSelected: country id after increment: " + mCountryId);
                    //call api
                    //get list of countries
                    getState(mCountryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //states spinner
        mSpinnerStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mStateId = getSelectedSateId(adapterView.getItemAtPosition(i).toString());
                //get state id for selected state

                if (mStateId == 0) {
                    Log.d(TAG, "onItemSelected: I'm default...");

                } else {

                    Log.d(TAG, "onItemSelected: State Name: " + adapterView.getItemAtPosition(i).toString() +
                            "\nSelected State Id: " + mStateId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        makeNetworkRequests();

    }//on create

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume....");

        //check for network
        if (isNetworkOk()) {
            //TODO: call api
            //makeNetworkRequests();

        } else {
            //network error dialog
            new AlertDialog.Builder(this)
                    .setTitle("You are offline")
                    .setMessage("Please turn on WiFi.")
                    .setPositiveButton("Turn on", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .create().show();
        }//else
    }//onStart

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.edt_date_of_birth:
                //TODO: date dialog for different API Levels
                openDatePickerDialog();
                break;

        }//switch

    }// click listener

    private void openDatePickerDialog() {

        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // date picker dialog
        DatePickerDialog dialog = new DatePickerDialog
                (
                        RegisterSecondStep.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnDateSetListener,
                        year, month, day
                );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }// end PickerDialog

    public boolean isNetworkOk() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            Log.d(TAG, "isNetworkOk: " + connected);

        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }//end method

    private void makeNetworkRequests() {
        Log.d(TAG, "makeNetworkRequests: making request...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPartnerRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

        //get list of countries
        getCountries();

    }// end calls


    private void getCountries() {

        Call<CountryResponse> call = mPartnerRegistrationApi.getAllCountries();

        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                Log.d(TAG, "onResponse: Country Response is: " + response.isSuccessful());

                mCountryNames = new ArrayList<>();
                mCountryNames.clear();

                //default params
                mCountryNames.add("Country");

                assert response.body() != null;
                //assign
                mCountries = response.body().getCountries();

                //fetch data from List of country
                for (Countries countries : mCountries) {
                    mCountryNames.add(countries.getName());
                }

                //load countries in spinner
                setUpCountrySpinner();
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });// end enqueue countries
    }//end get countries

    private void getState(int id) {
        Log.d(TAG, "getState: getting states...");
        mProgressBar.setVisibility(View.VISIBLE);

        Call<StatesResponse> call = mPartnerRegistrationApi.getAllStates(id);

        call.enqueue(new Callback<StatesResponse>() {
            @Override
            public void onResponse(Call<StatesResponse> call, Response<StatesResponse> response) {
                Log.d(TAG, "onResponse: states: " + response.isSuccessful());
                mProgressBar.setVisibility(View.GONE);

                mStateNames.clear();

                //default params
                mStateNames.add("State");

                //assign
                if (response.body() != null) {
                    mStates = response.body().getStatesList();
                }

                //fetch data from List of country
                for (States states : mStates) {
                    mStateNames.add(states.getName());
                }

                //load spinner with list of states
                setUpStatesSpinner();
            }

            @Override
            public void onFailure(Call<StatesResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });//end call

    }//end get state

    private void setUpCountrySpinner() {

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mCountryNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerCountries.setAdapter(dataAdapter);

    }//end spinner

    private void setUpStatesSpinner() {

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mStateNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerStates.setAdapter(dataAdapter);

    }//end spinner

    private int getSelectedSateId(String name) {
        Log.d(TAG, "getSelectedStateId: getting id..");
        int id = 0;

        if (mStates != null) {
            for (int i = 0; i < mStates.size(); i++) {
                if (mStates.get(i).getName().equals(name)) {
                    id = mStates.get(i).getId();
                    break;
                }
            }
        }//if

        Log.d(TAG, "getSelectedSateId: " + id);
        return id;
    }


}//end class