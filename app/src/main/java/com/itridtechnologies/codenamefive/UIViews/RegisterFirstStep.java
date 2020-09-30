package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.EmailPassExistResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.FirstRegisterStep;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;
import com.itridtechnologies.codenamefive.UIViews.Fragments.FragBottomDialog;
import com.itridtechnologies.codenamefive.UIViews.Fragments.FragNoInternetDialog;
import com.santalu.maskara.widget.MaskEditText;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itridtechnologies.codenamefive.Const.Constants.BASE_URL;

public class RegisterFirstStep extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        FragBottomDialog.BottomSheetListener, FragNoInternetDialog.ConnectionErrorDialogListener {

    //constants
    private static final String TAG = "RegisterFirstStep";
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int GALLERY_PIC_REQUEST = 200;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSIONS_REQUEST_CODE = 1122;
    //pattern phone
    private static final Pattern PHONE_NUM_PATTERN = Pattern.compile("^\\s*(?:\\+" +
            "?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$");

    //pattern password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$");
    private static String vehicleNum = "";
    PartnerRegistrationApi mAPI;
    //ui views
    private Spinner mVehicleTypeSpinner;
    private TableRow mUploadPhotoRow;
    private TableRow mChangePhotoRow;
    private TableRow mTableRowVehicleRegNum;
    private CircleImageView mUserPhoto;
    private EditText mEditTextFirstName;
    private EditText mEditTextLastName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextPhone;
    private Button mButtonContinueRegistration;
    private MaskEditText mMaskEditTextVehicleNumber;
    private TextView mTextViewError;
    private ProgressBar mProgressBar;
    //vars
    private Uri mImageUri;
    private String mImageFilePath;
    private String mEmailMessage;
    private String mPhoneMessage;
    private String mEmail;
    private String mPhone;
    private boolean mIsEmail = false;
    private boolean mIsPhone = false;
    private boolean mPermissionGranted = false;
    private int mVehicleId = 0;
    private int INPUT_ERROR_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first_step);

        //find views
        mUploadPhotoRow = findViewById(R.id.row_upload_photo);
        mUserPhoto = findViewById(R.id.img_profile_photo);
        mChangePhotoRow = findViewById(R.id.row_change_photo);
        mTableRowVehicleRegNum = findViewById(R.id.row_vehicle_reg_num);
        mTextViewError = findViewById(R.id.tv_input_error);
        mButtonContinueRegistration = findViewById(R.id.btn_register_first_step);
        mProgressBar = findViewById(R.id.progressFirstStep);

        //editText widgets reference
        mEditTextFirstName = findViewById(R.id.edt_first_name);
        mEditTextLastName = findViewById(R.id.edt_last_name);
        mEditTextEmail = findViewById(R.id.edt_email_address);
        mEditTextPassword = findViewById(R.id.edt_password);
        mEditTextPhone = findViewById(R.id.edt_phone_number);
        mMaskEditTextVehicleNumber = findViewById(R.id.edt_vehicle_number);

        //set listener
        mUploadPhotoRow.setOnClickListener(this);
        mChangePhotoRow.setOnClickListener(this);
        mUserPhoto.setOnClickListener(this);
        mButtonContinueRegistration.setOnClickListener(this);

        //init & build retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mAPI = retrofit.create(PartnerRegistrationApi.class);

        //set up spinner
        setUpSpinner();

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onPause: Activity visible..");

        if (FirstRegisterStep.getImageUri() != null) {
            //render rows
            mUploadPhotoRow.setVisibility(View.GONE);
            mChangePhotoRow.setVisibility(View.VISIBLE);
            //preview img
            mUserPhoto.setImageURI(FirstRegisterStep.getImageUri());
        }
        if (vehicleNum != null) {
            mMaskEditTextVehicleNumber.setText(vehicleNum);
        }
    }

    //methods_______________________________________________________________________________________

    public void requestCameraPermissions() {

        Log.d(TAG, "requestCameraPermissions: getting camera permissions..");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    mPermissionGranted = true;
                    Log.d(TAG, "requestCameraPermissions: permissions granted by user..");
                    bottomDialog();
                }

            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        PERMISSIONS_REQUEST_CODE);
            }
        }//end if
        else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSIONS_REQUEST_CODE);
        }
    }//end request permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called...");

        if (requestCode == PERMISSIONS_REQUEST_CODE) {

            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: permissions failed..");
                        return;
                    }
                }//end for
            }//end if

            Log.d(TAG, "onRequestPermissionsResult: permissions granted !");
            mPermissionGranted = true;
            bottomDialog();

        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }

    }//reqPermissionResult

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            //try to create file from img path
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {

                mImageUri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                FirstRegisterStep.setImageUri(mImageUri);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
            }
        }//if
    }//end openCam

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //mediaScanIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(mediaScanIntent, "Select File"), GALLERY_PIC_REQUEST);
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );

        mImageFilePath = image.getAbsolutePath();
        return image;
    }

    //file chooser request result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {

                //hide upload pho row
                mUploadPhotoRow.setVisibility(View.GONE);
                //show change profile row
                mChangePhotoRow.setVisibility(View.VISIBLE);

                Log.d(TAG, "onActivityResult: image result...");
                mUserPhoto.setImageURI(Uri.parse(mImageFilePath));
                Toast.makeText(this, "Img saved at: " + mImageFilePath, Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation cancelled !", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //hide upload pho row
            mUploadPhotoRow.setVisibility(View.GONE);
            //show change profile row
            mChangePhotoRow.setVisibility(View.VISIBLE);

            mImageUri = data.getData();
            //load image into imageView
            mUserPhoto.setImageURI(mImageUri);

            FirstRegisterStep.setImageUri(mImageUri);

            //mImageFilePath = getRealPathFromURI(mImageUri);
            Log.d(TAG, "onActivityResult: img uri: " + mImageUri);
        }
    }

    private void setUpSpinner() {

        // Spinner element
        mVehicleTypeSpinner = findViewById(R.id.spinner_vehicle_types);

        // Spinner click listener
        mVehicleTypeSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Select your vehicle type");
        categories.add("Bicycle");
        categories.add("Moped");
        categories.add("Car");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        mVehicleTypeSpinner.setAdapter(dataAdapter);

    }//end spinner

    //override methods______________________________________________________________________________

    //spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        mTextViewError.setText("");
        mVehicleId = (int) adapterView.getItemIdAtPosition(i);
        Log.d(TAG, "onItemSelected: item name: " + adapterView.getItemAtPosition(i).toString() +
                "\nitem id: " + mVehicleId);

        //enable disable vehicle reg field
        if (mVehicleId == 2 || mVehicleId == 3) {
            Log.d(TAG, "onItemSelected: here..." + vehicleNum);
            //clear the text field id any previous data
            //mMaskEditTextVehicleNumber.setText(null);
            //vehicleNum = null;

            //set visibility
            mTableRowVehicleRegNum.setVisibility(View.VISIBLE);

        } else {
            //clear data
            Log.d(TAG, "onItemSelected: else:" + vehicleNum);
            mTableRowVehicleRegNum.setVisibility(View.GONE);
            //mMaskEditTextVehicleNumber.setText(null);
            //vehicleNum = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //spinner

    //click listeners
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.row_upload_photo:
            case R.id.row_change_photo:
                requestCameraPermissions();
                break;

            case R.id.btn_register_first_step:
                //clear all error
                mTextViewError.setText("");

                if (inputValidation()) {
                    //check for internet
                    if (isNetworkOk()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        //if input is valid (means not null or unformatted)
                        //call API for email / phone validation
                        ifEmailExists(mEmail);
                    } else {
                        openConnectionErrorDialog();
                    }

                } else {
                    updateUIWithErrorCode(INPUT_ERROR_CODE);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }//switch

    }//end click

    private boolean inputValidation() {
        Log.d(TAG, "inputValidation: validating input...");

        mEmail = mEditTextEmail.getText().toString().trim();
        mPhone = mEditTextPhone.getText().toString().trim();

        if (mEditTextFirstName.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 1;
            return false;
        } else if (mEditTextLastName.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 2;
            return false;
        } else if (mEmail.isEmpty() ||
                !(Patterns.EMAIL_ADDRESS.matcher(mEditTextEmail.getText().toString().trim()).matches())) {
            INPUT_ERROR_CODE = 3;
            return false;
        } else if (mEditTextPassword.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 4;
            return false;
        } else if (mPhone.isEmpty() ||
                !PHONE_NUM_PATTERN.matcher(mEditTextPhone.getText().toString().trim()).matches()) {
            INPUT_ERROR_CODE = 5;
            return false;
        } else if (mVehicleId == 0) {
            INPUT_ERROR_CODE = 7;
            return false;
        } else if (mVehicleId != 1 && mMaskEditTextVehicleNumber.getUnMasked().isEmpty()) {
            INPUT_ERROR_CODE = 6;
            return false;
        } else if (FirstRegisterStep.getImageUri() == null) {
            INPUT_ERROR_CODE = 8;
            return false;
        } else if (!PASSWORD_PATTERN.matcher(mEditTextPassword.getText().toString().trim()).matches()) {
            INPUT_ERROR_CODE = 9;
            return false;
        } else {

            Log.d(TAG, "inputValidation: input validation success!" + vehicleNum);
            //data is valid so save values
            return true;
        }

    }//end validate

    private void updateUIWithErrorCode(int errorCode) {

        switch (errorCode) {

            case 1:
                mTextViewError.setText(R.string.error_first_name);
                break;

            case 2:
                mTextViewError.setText(R.string.error_last_name);
                break;

            case 3:
                mTextViewError.setText(R.string.error_email_address);
                break;

            case 4:
                mTextViewError.setText(R.string.error_password);
                break;

            case 5:
                mTextViewError.setText(R.string.error_phone_num);
                break;

            case 6:
                mTextViewError.setText(R.string.error_vehicle_reg_num);
                break;

            case 7:
                mTextViewError.setText(R.string.error_vehicle_type);
                break;

            case 8:
                mTextViewError.setText(R.string.error_profile_pic);
                break;

            case 9:
                mTextViewError.setText(R.string.error_password_weak);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + errorCode);

        }//switch

    }//end update

    private void completeRegistrationStep() {
        Log.d(TAG, "completeRegistrationStep: completing registration ...");

        //fetch values from input fields
        String firstName = mEditTextFirstName.getText().toString().trim();
        String lastName = mEditTextLastName.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        vehicleNum = mMaskEditTextVehicleNumber.getUnMasked();

        if (mIsEmail) {
            //email is valid, so check for phone now
            if (mIsPhone) {
                //here phone & email are valid
                //so complete registration
                if (mVehicleId == 2 || mVehicleId == 3) {

                    //inset data to model

                    FirstRegisterStep.setFirstName(firstName);
                    FirstRegisterStep.setLastName(lastName);
                    FirstRegisterStep.setEmail(mEmail);
                    FirstRegisterStep.setPassword(password);
                    FirstRegisterStep.setPhone(mPhone);
                    FirstRegisterStep.setVehicleNum(vehicleNum);
                    FirstRegisterStep.setVehicleId(String.valueOf(mVehicleId));
                    FirstRegisterStep.setImageUri(mImageUri);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTextViewError.setText("");
                    navToNextScreen();
                    Log.d(TAG, "completeRegistrationStep: oky for cars.."+vehicleNum);

                } else {
                    //inset data to model in bicycle case
                    FirstRegisterStep.setFirstName(firstName);
                    FirstRegisterStep.setLastName(lastName);
                    FirstRegisterStep.setEmail(mEmail);
                    FirstRegisterStep.setPassword(password);
                    FirstRegisterStep.setPhone(mPhone);
                    FirstRegisterStep.setVehicleId(String.valueOf(mVehicleId));
                    FirstRegisterStep.setImageUri(mImageUri);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTextViewError.setText("");
                    navToNextScreen();
                    Log.d(TAG, "completeRegistrationStep: oky..");
                }

            } else {
                mTextViewError.setText(mPhoneMessage);
            }

        } else {
            mTextViewError.setText(mEmailMessage);
        }

    }//end register

    private void ifEmailExists(String email) {
        Log.d(TAG, "ifEmailExists: checking...");

        //create a json object for raw parameter
        //use gson object
        JsonObject emailParams = new JsonObject();

        //put values
        emailParams.addProperty("key", "email");
        emailParams.addProperty("value", email);

        //pass raw json to api
        Call<EmailPassExistResponse> call = mAPI.isExistingEmail(emailParams);

        //execute call
        call.enqueue(new Callback<EmailPassExistResponse>() {
            @Override
            public void onResponse(@NotNull Call<EmailPassExistResponse> call, @NotNull Response<EmailPassExistResponse> response) {

                Log.d(TAG, "onResponse: code: " + response.code());

                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().isSuccess()) {

                            Log.d(TAG, "onResponse: api call success: " + response.body().isSuccess());
                            Log.d(TAG, "onResponse: msg: " + response.body().getMsg());
                            mIsEmail = true;
                            //call api phone
                            ifPhoneExists(mPhone);

                        } else {

                            mEmailMessage = response.body().getMsg();
                            mIsEmail = false;
                            Log.d(TAG, "onResponse: api call success: " + response.body().isSuccess() +
                                    "\nmsg: " + mPhoneMessage);
                        }
                    }
                } else if (response.code() == 500) {
                    Toast.makeText(RegisterFirstStep.this, "Email Already Exists..."
                            , Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<EmailPassExistResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }//end exists email

    private void ifPhoneExists(String phone) {
        Log.d(TAG, "ifPhoneExists: checking...");

        //create a json object for raw parameter
        //use gson object
        JsonObject phoneParams = new JsonObject();

        //put values
        phoneParams.addProperty("key", "phoneNumber");
        phoneParams.addProperty("value", phone);

        //pass raw json to api
        Call<EmailPassExistResponse> call = mAPI.isExistingPhone(phoneParams);

        //execute call
        call.enqueue(new Callback<EmailPassExistResponse>() {


            @Override
            public void onResponse(@NotNull Call<EmailPassExistResponse> call, @NotNull Response<EmailPassExistResponse> response) {

                Log.d(TAG, "onResponse: code: " + response.code());

                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().isSuccess()) {

                            Log.d(TAG, "onResponse: api call success: " + response.body().isSuccess());
                            Log.d(TAG, "onResponse: msg: " + response.body().getMsg());
                            mIsPhone = true;
                            completeRegistrationStep();

                        } else {

                            mPhoneMessage = response.body().getMsg();
                            Log.d(TAG, "onResponse: api call success: " + response.body().isSuccess() +
                                    "\nmsg: " + mPhoneMessage);
                            mIsPhone = false;
                        }
                    }
                } else if (response.code() == 500) {

                    Log.d(TAG, "onResponse: server: " + response.code());
                    Toast.makeText(RegisterFirstStep.this, "Phone Already Exists..."
                            , Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<EmailPassExistResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }//end exists email

    private void navToNextScreen() {
        startActivity(new Intent(this, RegisterSecondStep.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void bottomDialog() {
        FragBottomDialog dialog = new FragBottomDialog();
        dialog.show(getSupportFragmentManager(), "bottomDialog");
    }

    //listening to bottom dialog codes
    @Override
    public void onImageButtonClicked(int requestCode) {
        if (requestCode == 1) {
            openCamera();
        } else if (requestCode == 2) {
            galleryAddPic();
        }
    }

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

    private void openConnectionErrorDialog() {
        FragNoInternetDialog dialog = new FragNoInternetDialog();
        dialog.show(getSupportFragmentManager(), "ErrorDialog");
    }

    @Override
    public void onDialogButtonClicked(int actionCode) {
        if (actionCode == 1) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }
}//end class