package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.itridtechnologies.codenamefive.R;

public class PartnerLoginSecurityCode extends AppCompatActivity {

    private static final String TAG = "PartnerLoginSecurityCod";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private TextInputLayout partnerSecurityCode;
    private Button loginAsPartner;
    private CardView partnerSecurityCardView;
    private Animation animation;
    private String LOGIN_PASSWORD = null;
    private TextView infoMessage;
    private TextView displayPhone;

    private String msg = "We sent a code to";
    private String msg2 = "Enter your password to login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_login_security_code);

        //findViews
        partnerSecurityCode = findViewById(R.id.partner_login_password);
        loginAsPartner = findViewById(R.id.btn_confirm_login);
        partnerSecurityCardView = findViewById(R.id.partner_security_code_cardView);
        infoMessage = findViewById(R.id.msg2);
        displayPhone = findViewById(R.id.tv_display_phone);
        //loadAnimation
        animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_slide);
        partnerSecurityCardView.setAnimation(animation);
        //setIntentValues
        getIntentValues();
        //...........................................................
        //LISTENERS
        loginAsPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSecurityCode()) {
                    if (checkGooglePlayServices()) {
                        Intent intent = new Intent(PartnerLoginSecurityCode.this, PartnerDashboardMain.class);
                        startActivity(intent);
                        finish();
                        //open anim
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }//end if
                }//end if
            }
        });//end listener
        //...........................................................
    }//onCreate

    //public methods

    public boolean checkGooglePlayServices() {
        Log.d(TAG, "checkGooglePlayServices: Checking Google Services Version...");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PartnerLoginSecurityCode.this);
        //check for correct version
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkGooglePlayServices: Google Play Services Is Working!");
            //user can now make make request
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but can be fixed
            Log.d(TAG, "checkGooglePlayServices: Error Occurred But Google Can Fix It!");
            //open a dialog from google to resolve issue
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PartnerLoginSecurityCode.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Map Request Cannot Be Made...", Toast.LENGTH_SHORT).show();
        }
        return false;
    }//end check play services

    public boolean validateSecurityCode() {
        //getInput
        try {
            LOGIN_PASSWORD = partnerSecurityCode.getEditText().getText().toString().trim();
        } catch (Exception ex) {
            System.out.println("Input Error");
        }
        if (LOGIN_PASSWORD.isEmpty()) {
            partnerSecurityCode.setError("Field can't be empty");
            return false;
        } else {
            partnerSecurityCode.setError(null);
            return true;
        }
    }// end validate

    public void getIntentValues() {
        Intent intent = getIntent();
        String EXTRA_PHONE = intent.getStringExtra("riderPhone");
        String EXTRA_KEYBOARD = intent.getStringExtra("keyboardType");

        if (!EXTRA_PHONE.isEmpty() && EXTRA_KEYBOARD.equals("phone")) {

            this.infoMessage.setText(this.msg);
            this.partnerSecurityCode.getEditText().setHint("Security code");
            this.displayPhone.setText(EXTRA_PHONE);
            this.partnerSecurityCode.getEditText().setInputType(3);

        } else if (EXTRA_KEYBOARD.equals("password")) {
            this.infoMessage.setText(this.msg2);
            this.partnerSecurityCode.getEditText().setHint("Enter your password");
            this.partnerSecurityCode.getEditText().setInputType(1);
        }
    }//end getValues

    @Override
    public void finish() {
        super.finish();
        //close anim
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}//endClass