package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels.DocumentUriModel;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels.FileDetails;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels.PojoImageResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.EmailPassExistResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.FirstRegisterStep;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.SecondRegisterStep;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;
import com.itridtechnologies.codenamefive.UIViews.Fragments.FragBottomDialog;
import com.itridtechnologies.codenamefive.utils.UniversalDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itridtechnologies.codenamefive.Const.Constants.ALLOWED_FILE_SIZE;
import static com.itridtechnologies.codenamefive.Const.Constants.BASE_URL;
import static com.itridtechnologies.codenamefive.Const.Constants.ONE_MB_IN_BYTES;

public class RegisterFinalStep extends AppCompatActivity implements View.OnClickListener, FragBottomDialog.BottomSheetListener,
        UniversalDialog.DialogListener {

    //constants
    private static final String TAG = "RegisterFinalStep";
    private static final int GALLERY_PIC_REQUEST = 100;
    private static final int PICK_IMAGE_REQUEST = 200;
    private static int selectedPhotoType = 0;
    private static boolean img1 = false;
    private static boolean img2 = false;
    private static boolean img3 = false;
    private static boolean hasUserAgreed = false;
    MultipartBody.Part file1Parts;
    MultipartBody.Part file2Parts;
    MultipartBody.Part file3Parts;
    MultipartBody.Part file4Parts;
    PartnerRegistrationApi mRegistrationApi;
    MediaPlayer player;
    Animation fadeIn;
    //ui views
    private TextView mTextViewDocumentError;
    private TextView mTextViewAdrProofError;
    private TextView mTextViewAgreementError;
    private TableRow mTableRowFrontDoc;
    private TableRow mTableRowBackDoc;
    private TableRow mTableRowAddressDoc;
    private ImageView mImageViewDoneFront;
    private ImageView mImageViewDoneBack;
    private ImageView mImageViewDoneAddress;
    private Button mButtonSubmitApplication;
    private CheckBox mCheckBoxUserAgreement;
    private Animation bounce;
    private CardView uploadProgressView;
    private ProgressBar mProgressBar;
    private TextView mTextViewProgress;
    //vars
    private Uri mFrontDocUri;
    private Uri mBackDocUri;
    private Uri mAddressUri;
    private Uri mProfileUri;
    private String mAbsolutePath = "";
    private String FRONT_DOCUMENT_PATH = "";
    private String BACK_DOCUMENT_PATH = "";
    private String ADDRESS_DOCUMENT_PATH = "";
    private String mFileName1;
    private String mFileName2;
    private String mFileName3;
    private String mFileName4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_final_step);

        //find views
        mTextViewDocumentError = findViewById(R.id.tv_error_documents);
        mTextViewAdrProofError = findViewById(R.id.tv_error_address);
        mTextViewAgreementError = findViewById(R.id.tv_error_agreement);

        mImageViewDoneFront = findViewById(R.id.img_done_front);
        mImageViewDoneBack = findViewById(R.id.img_done_back);
        mImageViewDoneAddress = findViewById(R.id.img_done_address);

        mTableRowFrontDoc = findViewById(R.id.row_document_front);
        mTableRowBackDoc = findViewById(R.id.row_document_back);
        mTableRowAddressDoc = findViewById(R.id.row_address_proof);

        mButtonSubmitApplication = findViewById(R.id.btn_submit_application);
        mCheckBoxUserAgreement = findViewById(R.id.checkbox_terms_of_use);
        uploadProgressView = findViewById(R.id.cardViewUploadProgress);
        mProgressBar = findViewById(R.id.uploadProgress);
        mTextViewProgress = findViewById(R.id.tv_upload_progress);

        //set listeners
        mTableRowAddressDoc.setOnClickListener(this);
        mTableRowBackDoc.setOnClickListener(this);
        mTableRowFrontDoc.setOnClickListener(this);
        mButtonSubmitApplication.setOnClickListener(this);

        //check box listener user agreement
        mCheckBoxUserAgreement.setOnCheckedChangeListener((buttonView, isChecked) -> hasUserAgreed = true);

    }//on create

    @Override
    protected void onResume() {
        Log.d(TAG, "onPause: activity showing..");
        super.onResume();
        setSelectedImageFeedback();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.row_document_front:
                selectedPhotoType = 1;
                //dialogUploadOptions();
                bottomDialog();
                break;

            case R.id.row_document_back:
                selectedPhotoType = 2;
                //dialogUploadOptions();
                bottomDialog();
                break;

            case R.id.row_address_proof:
                selectedPhotoType = 3;
                //dialogUploadOptions();
                bottomDialog();
                break;

            case R.id.btn_submit_application:

                //clear error screens
                mTextViewDocumentError.setText("");
                mTextViewAdrProofError.setText("");
                mTextViewAgreementError.setText("");

                //validation
                if (updateUIWithError()) {
                    //show progress bar
                    bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
                    uploadProgressView.setVisibility(View.VISIBLE);
                    uploadProgressView.setAnimation(bounce);
                    /*upload data to server
                     * file / documents are built successfully then, upload it
                     * */
                    if (prepareFileParts()) {

                        //files are good, but check for internet to start upload
                        if (isNetworkOk()) {
                            //final go...
                            buildRetrofit();
                            uploadDocument1();
                        } else {
                            //no internet
                            uploadProgressView.setVisibility(View.GONE);
                            showInternetErrorDialog();
                        }

                    } else {
                        Toast.makeText(this, "Internal Error: code rfs", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }//switch

    }// onClick


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

                switch (selectedPhotoType) {

                    case 1:
                        mFrontDocUri = FileProvider.getUriForFile(
                                this,
                                getApplicationContext().getPackageName() + ".provider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFrontDocUri);
                        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
                        break;

                    case 2:
                        mBackDocUri = FileProvider.getUriForFile(
                                this,
                                getApplicationContext().getPackageName() + ".provider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mBackDocUri);
                        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
                        break;

                    case 3:
                        mAddressUri = FileProvider.getUriForFile(
                                this,
                                getApplicationContext().getPackageName() + ".provider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mAddressUri);
                        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
                        break;

                }//switch

            }//if
        }//if
    }//end openCam

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mediaScanIntent, "Select File"), GALLERY_PIC_REQUEST);
    }//end open gallery

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

        mAbsolutePath = image.getAbsolutePath();
        return image;
    }//end create image

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //file chooser request result
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: selected photo type: " + selectedPhotoType);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: image result...");
                //get uri
                int code = isValidFile(Uri.parse(mAbsolutePath));

                switch (selectedPhotoType) {

                    case 1:
                        if (code == 0) {
                            FRONT_DOCUMENT_PATH = mAbsolutePath;

                            //set img uri to model
                            DocumentUriModel.frontDoc = Uri.parse(FRONT_DOCUMENT_PATH);

                            //set a boolean to true
                            img1 = true;
                            Toast.makeText(this, "Success: Valid File !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onActivityResult: " + "\nfront doc: " + FRONT_DOCUMENT_PATH);

                        } else if (code == 1) {
                            //set a boolean to false
                            img1 = false;
                            Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                        } else if (code == 2) {
                            //set a boolean to false
                            img1 = false;
                            Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                    , Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 2:
                        if (code == 0) {
                            BACK_DOCUMENT_PATH = mAbsolutePath;

                            DocumentUriModel.backDoc = Uri.parse(BACK_DOCUMENT_PATH);

                            //set a boolean to true
                            img2 = true;
                            Toast.makeText(this, "Success: Valid File !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onActivityResult: " + "\nfront doc: " + BACK_DOCUMENT_PATH);

                        } else if (code == 1) {
                            //set a boolean to false
                            img2 = false;
                            Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                        } else if (code == 2) {
                            //set a boolean to false
                            img2 = false;
                            Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                    , Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 3:
                        if (code == 0) {
                            ADDRESS_DOCUMENT_PATH = mAbsolutePath;

                            DocumentUriModel.addressDoc = Uri.parse(ADDRESS_DOCUMENT_PATH);

                            //set a boolean to true
                            img3 = true;
                            Toast.makeText(this, "Success: Valid File !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onActivityResult: " + "\nfront doc: " + ADDRESS_DOCUMENT_PATH);

                        } else if (code == 1) {
                            //set a boolean to false
                            img3 = false;
                            Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                        } else if (code == 2) {
                            //set a boolean to false
                            img3 = false;
                            Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                    , Toast.LENGTH_LONG).show();
                        }
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + selectedPhotoType);

                }//switch

                //camera cancelled
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation cancelled !", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: is selecting from gallery...");
            int code = 0;

            switch (selectedPhotoType) {

                case 1:
                    mFrontDocUri = data.getData();
                    //validate file
                    code = isValidFile(mFrontDocUri);

                    if (code == 0) {
                        //set uri model
                        DocumentUriModel.frontDoc = mFrontDocUri;

                        //get real image path for selected file fromm gallery
                        FRONT_DOCUMENT_PATH = getRealPathFromURI(mFrontDocUri);

                        Log.d(TAG, "onActivityResult: actual gallery img path: " + FRONT_DOCUMENT_PATH);
                        img1 = true;

                    } else if (code == 1) {
                        //set a boolean to false
                        img1 = false;
                        Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                    } else if (code == 2) {
                        //set a boolean to false
                        img1 = false;
                        Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                , Toast.LENGTH_LONG).show();
                    }
                    break;

                case 2:

                    mBackDocUri = data.getData();
                    //validate file
                    code = isValidFile(mBackDocUri);

                    if (code == 0) {
                        //set uri model
                        DocumentUriModel.backDoc = mBackDocUri;

                        //get real image path for selected file fromm gallery
                        BACK_DOCUMENT_PATH = getRealPathFromURI(mBackDocUri);

                        Log.d(TAG, "onActivityResult: actual gallery img path: " + BACK_DOCUMENT_PATH);
                        img2 = true;

                    } else if (code == 1) {
                        //set a boolean to false
                        img2 = false;
                        Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                    } else if (code == 2) {
                        //set a boolean to false
                        img2 = false;
                        Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                , Toast.LENGTH_LONG).show();
                    }
                    break;

                case 3:

                    mAddressUri = data.getData();
                    //validate file
                    code = isValidFile(mAddressUri);

                    if (code == 0) {
                        //set uri model
                        DocumentUriModel.addressDoc = mAddressUri;

                        //get real image path for selected file fromm gallery
                        ADDRESS_DOCUMENT_PATH = getRealPathFromURI(mAddressUri);

                        Log.d(TAG, "onActivityResult: actual gallery img path: " + ADDRESS_DOCUMENT_PATH);
                        img3 = true;

                    } else if (code == 1) {
                        //set a boolean to false
                        img3 = false;
                        Toast.makeText(this, "Error: Maximum allowed file size is 5MB", Toast.LENGTH_LONG).show();

                    } else if (code == 2) {
                        //set a boolean to false
                        img3 = false;
                        Toast.makeText(this, "Error: Unsupported file type\nSupported formats are .png .jpg .jpeg .pdf"
                                , Toast.LENGTH_LONG).show();
                    }
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + selectedPhotoType);
            }//switch

        }//if gallery

    }//end actResult

    private int isValidFile(Uri returnUri) {
        Log.d(TAG, "isValidFile: validating file...");

        int imageErrorCode = 0;
        String fileExtension = null;
        double fileSize = 0.0;

        ContentResolver Cr = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        File file;

        fileExtension = typeMap.getExtensionFromMimeType(Cr.getType(returnUri));
        Log.d(TAG, "isValidFile: file extension from mime: " + fileExtension);

        file = new File(getRealPathFromURI(returnUri));
        fileSize = (double) file.length() / ONE_MB_IN_BYTES;
        Log.d(TAG, "isValidFile: file size in MB's: " + fileSize);

        //check file size
        if (fileSize > ALLOWED_FILE_SIZE) {
            imageErrorCode = 1;

        } else if (fileExtension != null) {

            switch (fileExtension) {

                case "jpeg":
                case "jpg":
                case "png":
                case "pdf":
                    imageErrorCode = 0;
                    break;

                default:
                    //invalid file type
                    imageErrorCode = 2;
                    break;

            }//switch

        }//else if

        return imageErrorCode;
    }//end file

    private void setSelectedImageFeedback() {

        if (img1) {
            mImageViewDoneFront.setVisibility(View.VISIBLE);
        }
        if (img2) {
            mImageViewDoneBack.setVisibility(View.VISIBLE);
        }
        if (img3) {
            mImageViewDoneAddress.setVisibility(View.VISIBLE);
        }
    }// end feedback

    private boolean updateUIWithError() {

        if (!img1) {

            fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            mTextViewDocumentError.setText(R.string.front_document);
            mTextViewDocumentError.setAnimation(fadeIn);
            return false;

        } else if (!img3) {

            fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            mTextViewAdrProofError.setText(R.string.address_proof);
            mTextViewAdrProofError.setAnimation(fadeIn);
            return false;

        } else if (!hasUserAgreed) {

            fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            mTextViewAgreementError.setText(R.string.agreement_error);
            mTextViewAgreementError.setAnimation(fadeIn);
            return false;

        } else {
            return true;
        }
    }//end update ui

    private JsonObject fetchOldData() {
        Log.d(TAG, "fetchOldData: preparing data...");

        //prepare JsonObject for POST request
        JsonObject registrationParams = new JsonObject();

        //put values in json format
        registrationParams.addProperty("firstName", FirstRegisterStep.getFirstName());
        registrationParams.addProperty("lastName", FirstRegisterStep.getLastName());
        registrationParams.addProperty("email", FirstRegisterStep.getEmail());
        registrationParams.addProperty("password", FirstRegisterStep.getPassword());
        registrationParams.addProperty("phoneNumber", FirstRegisterStep.getPhone());
        registrationParams.addProperty("dob", SecondRegisterStep.getDateOfBirth());
        registrationParams.addProperty("city", SecondRegisterStep.getCity());
        registrationParams.addProperty("state", SecondRegisterStep.getState());
        registrationParams.addProperty("zipCode", SecondRegisterStep.getZipCode());
        registrationParams.addProperty("country", SecondRegisterStep.getCountry());
        registrationParams.addProperty("vehicleReg", FirstRegisterStep.getVehicleNum());
        registrationParams.addProperty("vehicle", FirstRegisterStep.getVehicleId());
        registrationParams.addProperty("profilePhoto", mFileName4);
        registrationParams.addProperty("address1", SecondRegisterStep.getAddress1());
        registrationParams.addProperty("address2", SecondRegisterStep.getAddress2());
        registrationParams.addProperty("frontDocument", mFileName1);
        registrationParams.addProperty("backDocument", mFileName2);
        registrationParams.addProperty("addressProof", mFileName3);

        return registrationParams;
    }

    private boolean prepareFileParts() {
        Log.d(TAG, "prepareFileParts: preparing file parts...");

        mProfileUri = FirstRegisterStep.getImageUri();
        Log.d(TAG, "fetchOldData: profile img uri: " + mProfileUri);

        File file1 = new File(getRealPathFromURI(DocumentUriModel.frontDoc));
        File file2;

        //if there is exists back document
        //only then we want to prepare it for upload
        if (img2) {
            file2 = new File(getRealPathFromURI(DocumentUriModel.backDoc));
        } else {
            file2 = null;
        }

        File file3 = new File(getRealPathFromURI(DocumentUriModel.addressDoc));
        File file4 = new File(getRealPathFromURI(mProfileUri));

        String mediaType1 = getContentResolver().getType(DocumentUriModel.frontDoc);
        String mediaType3 = getContentResolver().getType(DocumentUriModel.addressDoc);
        String mediaType4 = getContentResolver().getType(mProfileUri);
        Log.d(TAG, "prepareFileParts: media type of profile pic: " + mediaType4);

        if (mediaType1 != null && mediaType3 != null && mediaType4 != null) {

            //part 1
            file1Parts = MultipartBody.Part.createFormData("file",
                    file1.getName(),
                    RequestBody.create(
                            MediaType.parse(mediaType1),
                            file1
                    )
            );

            //part 2
            if (file2 != null) {
                try {
                    String mediaType2 = getContentResolver().getType(DocumentUriModel.backDoc);
                    assert mediaType2 != null;
                    file2Parts = MultipartBody.Part.createFormData("file",
                            file2.getName(),
                            RequestBody.create(
                                    MediaType.parse(mediaType2),
                                    file2
                            )
                    );
                } catch (NullPointerException ex) {
                    Log.d(TAG, "prepareFileParts: " + ex);
                }

            }//if not optional case

            //part 3
            file3Parts = MultipartBody.Part.createFormData("file",
                    file3.getName(),
                    RequestBody.create(
                            MediaType.parse(mediaType3),
                            file3
                    )
            );

            //part 4
            file4Parts = MultipartBody.Part.createFormData("file",
                    file3.getName(),
                    RequestBody.create(
                            MediaType.parse(mediaType4),
                            file4
                    )
            );

            return true;
        }//if success

        else {
            return false;
        }
    }//file parts

    private void buildRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);
    }

    private void uploadDocument1() {
        Log.d(TAG, "uploadDocument1: uploading front document...");
        Call<PojoImageResponse> call = mRegistrationApi.uploadDocument1(file1Parts);

        call.enqueue(new Callback<PojoImageResponse>() {
            @Override
            public void onResponse(@NotNull Call<PojoImageResponse> call, @NotNull Response<PojoImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        //update progress
                        updateUploadProgress(20, R.string.progress_20_percent);

                        FileDetails details = response.body().getImgData().getFileDetails();
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: file name: " + details.getName());
                        //set file name coming from api
                        mFileName1 = details.getName();
                        //next api
                        //but if img is optionally null, then
                        if (img2) {
                            uploadDocument2();
                        } else {
                            uploadDocument3();
                        }
                    } else if (response.code() == 500) {
                        uploadProgressView.setVisibility(View.GONE);
                        showServerErrorDialog();
                    }
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<PojoImageResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                uploadProgressView.setVisibility(View.GONE);
                showServerErrorDialog();
            }
        });
    }//doc 1

    private void uploadDocument2() {
        Log.d(TAG, "uploadDocument2: uploading back document...");
        Call<PojoImageResponse> call = mRegistrationApi.uploadDocument2(file2Parts);

        call.enqueue(new Callback<PojoImageResponse>() {
            @Override
            public void onResponse(@NotNull Call<PojoImageResponse> call, @NotNull Response<PojoImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        //update progress
                        updateUploadProgress(40, R.string.progress_40_percent);

                        FileDetails details = response.body().getImgData().getFileDetails();
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: file name: " + details.getName());
                        //set file name coming from api
                        mFileName2 = details.getName();
                        //next api
                        uploadDocument3();

                    } else if (response.code() == 500) {
                        uploadProgressView.setVisibility(View.GONE);
                        showServerErrorDialog();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PojoImageResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                uploadProgressView.setVisibility(View.GONE);
                showServerErrorDialog();
            }
        });
    }//doc 2

    private void uploadDocument3() {
        Log.d(TAG, "uploadDocument3: uploading address document...");
        Call<PojoImageResponse> call = mRegistrationApi.uploadDocument3(file3Parts);

        call.enqueue(new Callback<PojoImageResponse>() {
            @Override
            public void onResponse(@NotNull Call<PojoImageResponse> call, @NotNull Response<PojoImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        //update progress
                        updateUploadProgress(60, R.string.progress_60_percent);

                        FileDetails details = response.body().getImgData().getFileDetails();
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: file name: " + details.getName());
                        //set file name coming from api
                        mFileName3 = details.getName();
                        //last api
                        uploadDocument4();

                    } else if (response.code() == 500) {
                        uploadProgressView.setVisibility(View.GONE);
                        showServerErrorDialog();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PojoImageResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                //dismiss upload progress bar
                uploadProgressView.setVisibility(View.GONE);
                showServerErrorDialog();
            }
        });
    }//doc 3

    private void uploadDocument4() {
        Log.d(TAG, "uploadDocument4: uploading profile document...");
        Call<PojoImageResponse> call = mRegistrationApi.uploadProfilePhoto(file4Parts);

        call.enqueue(new Callback<PojoImageResponse>() {
            @Override
            public void onResponse(@NotNull Call<PojoImageResponse> call, @NotNull Response<PojoImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        //update progress
                        updateUploadProgress(80, R.string.progress_80_percent);

                        FileDetails details = response.body().getImgData().getFileDetails();
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: file name: " + details.getName());
                        //set file name coming from api
                        mFileName4 = details.getName();
                        //next api
                        Log.d(TAG, "onResponse: partner data:" + fetchOldData().toString());
                        completePartnerRegistration(fetchOldData());

                    } else if (response.code() == 500) {
                        uploadProgressView.setVisibility(View.GONE);
                        showServerErrorDialog();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PojoImageResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                //dismiss upload progress bar
                uploadProgressView.setVisibility(View.GONE);
                showServerErrorDialog();
            }
        });
    }//doc 4

    private void completePartnerRegistration(JsonObject object) {
        Log.d(TAG, "completePartnerRegistration: uploading data...");

        Call<EmailPassExistResponse> call = mRegistrationApi.registerPartner(object);

        call.enqueue(new Callback<EmailPassExistResponse>() {
            @Override
            public void onResponse(@NotNull Call<EmailPassExistResponse> call, @NotNull Response<EmailPassExistResponse> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body() != null) {
                        //update progress
                        updateUploadProgress(100, R.string.progress_100_percent);
                        uploadProgressView.setElevation(0);


                        Log.d(TAG, "onResponse: partner response code: " + response.code());
                        Log.d(TAG, "onResponse: partner was successful: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: partner msg: " + response.body().getMsg());

                        uploadProgressView.setVisibility(View.GONE);
                        createVibeAlertWithSound();
                        showApplicationSubmittedDialog();
                    }
                } else if (response.code() == 500) {
                    uploadProgressView.setVisibility(View.GONE);
                    showServerErrorDialog();
                }
            }//onResponse

            @Override
            public void onFailure(@NotNull Call<EmailPassExistResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                uploadProgressView.setVisibility(View.GONE);
                showServerErrorDialog();
            }
        });

    }//end complete registration

    private void updateUploadProgress(int progress, int msg) {
        if (mProgressBar.getProgress() < 100) {
            mProgressBar.setProgress(progress);
            mTextViewProgress.setText(msg);
        }
    }//

    private void bottomDialog() {
        FragBottomDialog dialog = new FragBottomDialog();
        dialog.show(getSupportFragmentManager(), "bottomDialog");
    }

    @Override
    public void onImageButtonClicked(int requestCode) {
        if (requestCode == 1) {
            openCamera();
        } else if (requestCode == 2) {
            galleryAddPic();
        }
    }

    private void showInternetErrorDialog() {
        UniversalDialog dialog = new UniversalDialog(
                getResources().getString(R.string.dialog_con_error),
                getResources().getString(R.string.connection_warning),
                getResources().getString(R.string.turn_on),
                getResources().getString(R.string.dialog_cancel),
                R.drawable.icon_no_internet,
                1,
                0
        );
        dialog.show(getSupportFragmentManager(), "No Internet");
    }

    private void showServerErrorDialog() {
        UniversalDialog dialog = new UniversalDialog(
                "Could'nt connect to server",
                "Server unreachable, try again later.",
                "Retry",
                getResources().getString(R.string.dialog_cancel),
                R.drawable.icon_error,
                2,
                0
        );
        dialog.show(getSupportFragmentManager(), "Server Error");
    }

    private void showApplicationSubmittedDialog() {
        UniversalDialog dialog = new UniversalDialog(
                "Application submitted",
                "Your application has been successfully submitted",
                "Continue",
                "",
                R.drawable.icon_cloud_done,
                3,
                0
        );
        dialog.show(getSupportFragmentManager(), "Application Done");
    }

    @Override
    public void onDialogButtonClicked(int code) {
        if (code != 0) {
            if (code == 1) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else if (code == 2) {
                //server unreachable (clear text communication not possible)
                if (!isNetworkOk()) {
                    showInternetErrorDialog();
                } else {
                    uploadDocument1();
                }

            } else if (code == 3) {
                navToLogin();
                finish();
                finishAffinity();
            }
        }
    }

    private void navToLogin() {
        startActivity(new Intent(this, PartnerLogin.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

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

    private void createVibeAlertWithSound() {
        Log.d(TAG, "createVibeAlertWithSound: trying to give haptic feedback...");
        //init vibe
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //check for hardware
        if (vibe.hasVibrator() && player == null) {

            if (Build.VERSION.SDK_INT >= 26) {
                //vibrate
                vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                player = MediaPlayer.create(this, R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
            } else {
                player = MediaPlayer.create(this, R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
                vibe.vibrate(500);
            }
        }//end if

    }//end vibration

}//end class