package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.itridtechnologies.codenamefive.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RegisterFirstStep extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //constants
    private static final String TAG = "RegisterFirstStep";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int GALLERY_PIC_REQUEST = 2;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSIONS_REQUEST_CODE = 1122;
    private boolean mPermissionGranted = false;

    //ui views
    private Spinner mVehicleTypeSpinner;
    private TableRow mUploadPhotoRow;
    private ImageView mUserPhoto;

    //vars
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first_step);

        //find views
        mUploadPhotoRow = findViewById(R.id.row_upload_photo);
        mUserPhoto = findViewById(R.id.img_profile_photo);

        //set listener
        mUploadPhotoRow.setOnClickListener(this);
        mUserPhoto.setOnClickListener(this);

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

        }).setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

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
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mediaScanIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(mediaScanIntent , "Select File") , GALLERY_PIC_REQUEST);
    }

    //file chooser request result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            //load image into imageView
            mUserPhoto.setImageURI(mImageUri);

        } else if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            //load image into imageView
            mUserPhoto.setImageURI(mImageUri);
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
        categories.add("Car");
        categories.add("Bike");
        categories.add("Helicopter");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mVehicleTypeSpinner.setAdapter(dataAdapter);

    }//end spinner


    //override methods______________________________________________________________________________

    //spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
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
                requestCameraPermissions();
                break;

        }//switch

    }//end click

}//end class