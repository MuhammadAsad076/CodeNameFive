package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itridtechnologies.codenamefive.Models.RegistrationModels.FirstRegisterStep;
import com.itridtechnologies.codenamefive.R;
import com.santalu.maskara.widget.MaskEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFirstStep extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //constants
    private static final String TAG = "RegisterFirstStep";
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int GALLERY_PIC_REQUEST = 200;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSIONS_REQUEST_CODE = 1122;
    //pattern password
    private static final Pattern phoneNumPattern = Pattern.compile("^\\s*(?:\\+" +
            "?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$");
    private boolean mPermissionGranted = false;
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

    //vars
    private Uri mImageUri;
    private String mImageFilePath;
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

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onPause: Activity visible..");

        //set up spinner
        setUpSpinner();
    }

    //methods_______________________________________________________________________________________

    private void AlertDialogUploadOptions() {

        final CharSequence[] items = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose from upload options");

        //on click
        builder.setItems(items, (dialogInterface, i) -> {

            if (items[i].equals("Camera")) {
                //open camera intent
                openCamera();

            } else if (items[i].equals("Gallery")) {
                //open gallery intent
                galleryAddPic();
            }

        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        //build and create
        builder.create().show();

    }//end dialog

    public void requestCameraPermissions() {

        Log.d(TAG, "requestCameraPermissions: getting camera permissions..");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    mPermissionGranted = true;
                    Log.d(TAG, "requestCameraPermissions: permissions granted by user..");
                    AlertDialogUploadOptions();
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
            //TODO: call dialog
            AlertDialogUploadOptions();

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

    private String getRealPathFromURI(Uri contentUri) {

        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        String path = "";

        if (cursor != null) {

            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();


            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null
                    , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

            assert cursor != null;
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();

        }

        return path;
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
                ".jpg",         /* suffix */
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

                Log.d(TAG, "onActivityResult: image result...");
                mUserPhoto.setImageURI(Uri.parse(mImageFilePath));
                Toast.makeText(this, "Img saved at: " + mImageFilePath, Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation cancelled !", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            //load image into imageView
            mUserPhoto.setImageURI(mImageUri);
            mImageFilePath = getRealPathFromURI(mImageUri);
            Log.d(TAG, "onActivityResult: actual gallery img path: " + getRealPathFromURI(mImageUri));
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

        mVehicleId = (int) adapterView.getItemIdAtPosition(i);
        Log.d(TAG, "onItemSelected: item name: "+adapterView.getItemAtPosition(i).toString()+
                "\nitem id: "+mVehicleId);

        //enable disable vehicle reg field
        if (mVehicleId != 0 & mVehicleId != 1) {
            mTableRowVehicleRegNum.setVisibility(View.VISIBLE);
        } else {
            mTableRowVehicleRegNum.setVisibility(View.GONE);
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
                if (inputValidation()) {
                    completeRegistrationStep();
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

        if (mEditTextFirstName.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 1;
            return false;
        } else if (mEditTextLastName.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 2;
            return false;
        } else if (mEditTextEmail.getText().toString().trim().isEmpty() ||
                !(Patterns.EMAIL_ADDRESS.matcher(mEditTextEmail.getText().toString().trim()).matches())) {
            INPUT_ERROR_CODE = 3;
            return false;
        } else if (mEditTextPassword.getText().toString().trim().isEmpty()) {
            INPUT_ERROR_CODE = 4;
            return false;
        } else if (mEditTextPhone.getText().toString().trim().isEmpty() ||
                !phoneNumPattern.matcher(mEditTextPhone.getText().toString()).matches()) {
            INPUT_ERROR_CODE = 5;
            return false;
        } else if (mMaskEditTextVehicleNumber.getUnMasked().trim().isEmpty()) {
            INPUT_ERROR_CODE = 6;
            return false;
        } else if (mVehicleId == 0) {
            INPUT_ERROR_CODE = 7;
            return false;
        } else if (mImageFilePath == null) {
            INPUT_ERROR_CODE = 8;
            return false;
        } else {
            Log.d(TAG, "inputValidation: input validation success!");
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

            default:
                throw new IllegalStateException("Unexpected value: " + errorCode);

        }//switch

    }//end update

    private void completeRegistrationStep() {
        Log.d(TAG, "completeRegistrationStep: completing registration ...");

        //fetch values from input fields
        String firstName = mEditTextFirstName.getText().toString().trim();
        String lastName = mEditTextLastName.getText().toString().trim();
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        String phone = mEditTextPhone.getText().toString().trim();
        String vehicleNum = mMaskEditTextVehicleNumber.getUnMasked();

        if (mVehicleId != 1) {

            //inset data to model
            FirstRegisterStep register = new FirstRegisterStep(
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    vehicleNum,
                    String.valueOf(mVehicleId),
                    mImageFilePath
            );
            Log.d(TAG, "completeRegistrationStep: " + register.toString());
        }
    }//end register

}//end class