package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.itridtechnologies.codenamefive.Const.Constants;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.Cities;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.CityResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.Countries;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.CountryResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.SecondRegisterStep;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.States;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.StatesResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;
import com.itridtechnologies.codenamefive.UIViews.Fragments.FragBottomDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterSecondStep extends AppCompatActivity implements View.OnClickListener {

    //constants
    private static final String TAG = "RegisterSecondStep";
    //vars
    PartnerRegistrationApi mPartnerRegistrationApi;
    //ui views
    private EditText mEditTextBirthDate;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private ProgressBar mProgressBar;
    private Spinner mSpinnerCountries;
    private Spinner mSpinnerStates;
    private Spinner mSpinnerCities;
    private Button mButtonContinueRegistration;
    private ProgressBar mProgressBarLoadingButton;
    private EditText mEditTextZipCode;
    private EditText mEditTextAddressLine1;
    private EditText mEditTextAddressLine2;
    private TextView mTextViewError;
    private List<Countries> mCountries;
    private List<String> mCountryNames;

    private List<States> mStates;
    private List<String> mStateNames;

    private List<Cities> mCities;
    private List<String> mCityNames;

    private int mCountryId = 0;
    private int mStateId = 0;
    private int mCityId = 0;
    private String mDate;
    private int INPUT_ERROR_CODE = 0;
    private int userBirthYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_step);

        //find views
        mEditTextBirthDate = findViewById(R.id.edt_date_of_birth);
        mSpinnerCountries = findViewById(R.id.spinner_countries);
        mSpinnerStates = findViewById(R.id.spinner_states);
        mSpinnerCities = findViewById(R.id.spinner_cities);
        mProgressBar = findViewById(R.id.progress_bar);
        mEditTextZipCode = findViewById(R.id.edt_zip_code);
        mEditTextAddressLine1 = findViewById(R.id.edt_address_line_1);
        mEditTextAddressLine2 = findViewById(R.id.edt_address_line_2);
        mTextViewError = findViewById(R.id.tv_error_msg);
        mButtonContinueRegistration = findViewById(R.id.btn_register_second_step);
        mProgressBarLoadingButton = findViewById(R.id.progress_bar_btn_continue);

        //setting styles
        mEditTextBirthDate.setInputType(InputType.TYPE_NULL);
        mStateNames = new ArrayList<>();
        mCityNames = new ArrayList<>();

        //set listeners
        mEditTextBirthDate.setOnClickListener(this);
        //button listener
        mButtonContinueRegistration.setOnClickListener(this);

        mOnDateSetListener = (view, year, month, dayOfMonth) -> {
            mDate = dayOfMonth + "-" + (month + 1) + "-" + year;
            userBirthYear = year;
            mEditTextBirthDate.setText(mDate);
        };

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
                    Log.d(TAG, "onItemSelected: country id " + mCountryId);
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

                    mCityNames.add("City");
                    setUpCitySpinner();

                } else {
                    Log.d(TAG, "onItemSelected: State Name: " + adapterView.getItemAtPosition(i).toString() +
                            "\nSelected State Id: " + mStateId);

                    getCities(mStateId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //city spinner listener
        mSpinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mCityId = getSelectedCityId(
                        parent.getItemAtPosition(position).toString()
                );// get city id for selected city

                if (mCityId == 0) {
                    Log.d(TAG, "onItemSelected: i am default city..");
                    //mCityNames.add("City");

                } else {
                    Log.d(TAG, "onItemSelected: City Name: " + parent.getItemAtPosition(position).toString() +
                            "\nSelected City Id: " + mCityId);
                }

            }//on Select

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }//on create

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume....");

        //check for network
        if (isNetworkOk()) {
            makeNetworkRequests();
        } else {
            networkErrorDialog();
        }
    }//onStart

    private void networkErrorDialog() {
        //network error dialog
        new AlertDialog.Builder(this)
                .setTitle("You are offline")
                .setMessage("Please turn on WiFi.")
                .setPositiveButton("Turn on", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .create().show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.edt_date_of_birth:
                openDatePickerDialog();
                break;

            case R.id.btn_register_second_step:
                //validate
                if (validateInput()) {

                    //show processing
                    showButtonLoading();

                    //handler
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        hideButtonLoading();// hide processing
                        completeRegistration();
                    }, 1000);

                } else {
                    UpdateUIWithErrorCode(INPUT_ERROR_CODE);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());

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
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return connected;
    }//end method

    private void makeNetworkRequests() {
        Log.d(TAG, "makeNetworkRequests: making request...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPartnerRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

        //get list of countries
        getCountries();

    }// end calls


    private void getCountries() {

        Call<CountryResponse> call = mPartnerRegistrationApi.getAllCountries();
        mProgressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(@NotNull Call<CountryResponse> call, @NotNull Response<CountryResponse> response) {
                Log.d(TAG, "onResponse: Country Response is: " + response.isSuccessful());
                mProgressBar.setVisibility(View.INVISIBLE);

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
                mProgressBar.setVisibility(View.INVISIBLE);
                networkErrorDialog();
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
                mProgressBar.setVisibility(View.INVISIBLE);
                networkErrorDialog();
            }
        });//end call

    }//end get state

    private void getCities(int id) {
        Log.d(TAG, "getCities: getting cities...");
        mProgressBar.setVisibility(View.VISIBLE);

        Call<CityResponse> call = mPartnerRegistrationApi.getAllCities(id);

        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                Log.d(TAG, "onResponse: city response: " + response.isSuccessful());
                mProgressBar.setVisibility(View.INVISIBLE);

                if (response.body() != null) {
                    mCities = response.body().getCitiesList();
                }
                //clear list
                mCityNames.clear();

                //def val
                mCityNames.add("City");

                //get values from api
                for (Cities cities : mCities) {
                    mCityNames.add(cities.getName());
                }

            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }//end get cities

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

    private void setUpCitySpinner() {

        mCityNames.clear();
        mCityNames.add("City");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mCityNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerCities.setAdapter(dataAdapter);

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

    private int getSelectedCityId(String name) {
        Log.d(TAG, "getSelectedCityId: getting id..");
        int id = 0;

        if (mCities != null) {
            for (int i = 0; i < mCities.size(); i++) {
                if (mCities.get(i).getName().equals(name)) {
                    id = mCities.get(i).getId();
                    break;
                }
            }
        }//if

        Log.d(TAG, "getSelectedCityId: " + id);
        return id;
    }

    private boolean validateInput() {
        Log.d(TAG, "validateInput: validating...");

        if (mEditTextBirthDate.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 1;
            return false;
        } else if (!is18YearsOld(userBirthYear)) {
            INPUT_ERROR_CODE = 8;
            return false;
        } else if (mEditTextAddressLine1.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 2;
            return false;
        } else if (mEditTextAddressLine2.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 3;
            return false;
        } else if (mCountryId == 0) {
            INPUT_ERROR_CODE = 4;
            return false;
        } else if (mStateId == 0) {
            INPUT_ERROR_CODE = 5;
            return false;
        } else if (mCityId == 0) {
            INPUT_ERROR_CODE = 6;
            return false;
        } else if (mEditTextZipCode.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 7;
            return false;
        } else {
            Log.d(TAG, "validateInput: input is valid...");
            return true;
        }

    }//end validate

    private void UpdateUIWithErrorCode(int errorCode) {
        Log.d(TAG, "UpdateUIWithErrorCode: updating...");

        switch (errorCode) {

            case 1:
                mTextViewError.setText(R.string.error_birth_date);
                break;

            case 2:
            case 3:
                mTextViewError.setText(R.string.error_address);
                break;

            case 4:
                mTextViewError.setText(R.string.error_country);
                break;

            case 5:
                mTextViewError.setText(R.string.error_state);
                break;

            case 6:
                mTextViewError.setText(R.string.error_city);
                break;

            case 7:
                mTextViewError.setText(R.string.error_zip_code);
                break;

            case 8:
                mTextViewError.setText(R.string.error_age_restriction);
                break;

        }//switch

    }// end update UI

    private void completeRegistration() {
        Log.d(TAG, "completeRegistration: completing registration...");

        String zip = mEditTextZipCode.getText().toString().trim();

        //insert data into model
        SecondRegisterStep.dateOfBirth = mDate;
        SecondRegisterStep.address1 = mEditTextAddressLine1.getText().toString().trim();
        SecondRegisterStep.address2 = mEditTextAddressLine2.getText().toString().trim();
        SecondRegisterStep.country = String.valueOf(mCountryId);
        SecondRegisterStep.state = String.valueOf(mStateId);
        SecondRegisterStep.city = String.valueOf(mCityId);
        SecondRegisterStep.zipCode = zip;

        mTextViewError.setText("");
        Log.d(TAG, "completeRegistration: step 2 is oky!! ");
        navToNextScreen();

    }

    private boolean is18YearsOld(int year) {

        //get current calender year
        int currentYear =
                Calendar.getInstance().get(Calendar.YEAR);

        //compare with user entered year
        return (currentYear - year) >= 18;
    }

    private void navToNextScreen() {
        startActivity(new Intent(this, RegisterFinalStep.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void showButtonLoading() {
        //hide text
        mButtonContinueRegistration.setText("");
        //change color
        mButtonContinueRegistration.setBackgroundColor(
                getResources().getColor(R.color.backgroundLightGrey)
        );
        //show loading
        mProgressBarLoadingButton.setVisibility(View.VISIBLE);
    }

    private void hideButtonLoading() {
        //hide text
        mButtonContinueRegistration.setText(getResources().getString(R.string.continue_reg));
        //change color
        mButtonContinueRegistration.setBackground(
                ResourcesCompat.getDrawable(getResources(), R.drawable.btn_round_rect, null)
        );
        //show loading
        mProgressBarLoadingButton.setVisibility(View.GONE);
    }

}//end class