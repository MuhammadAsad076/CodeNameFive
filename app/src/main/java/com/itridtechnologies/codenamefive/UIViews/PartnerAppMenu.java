package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;

import com.itridtechnologies.codenamefive.R;

public class PartnerAppMenu extends AppCompatActivity implements View.OnClickListener {

    //declarations
    private TableRow rowPartnerProfile;
    private TableRow rowTripHistory;
    private TableRow rowMapSettings;
    private TableRow rowEarnings;
    private TableRow rowInbox;
    private Button partnerTrpHistory;
    private Button partnerMapSettings;
    private Button partnerEarnings;
    private Button partnerInbox;
    private Button logoutPartner;
    private ImageView autoAcceptOn;
    private ImageView autoAcceptOff;
    private boolean isOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_app_menu);

        //findViews
        rowPartnerProfile = findViewById(R.id.pt_profile);
        rowTripHistory = findViewById(R.id.tr_pt_history);
        partnerTrpHistory = findViewById(R.id.partner_trip_history);
        rowMapSettings = findViewById(R.id.tr_pt_map_settings);
        rowEarnings = findViewById(R.id.tr_pt_earnings);
        rowInbox = findViewById(R.id.tr_pt_inbox);
        partnerInbox = findViewById(R.id.btn_partnerInbox);
        partnerMapSettings = findViewById(R.id.btn_map_settings);
        partnerEarnings = findViewById(R.id.partner_earnings);
        logoutPartner = findViewById(R.id.btn_logout_partner);
        autoAcceptOn = findViewById(R.id.toggle_on);
        autoAcceptOff = findViewById(R.id.toggle_off);

        //setListeners
        rowPartnerProfile.setOnClickListener(this);
        rowTripHistory.setOnClickListener(this);
        rowEarnings.setOnClickListener(this);
        rowInbox.setOnClickListener(this);
        partnerTrpHistory.setOnClickListener(this);
        partnerEarnings.setOnClickListener(this);
        autoAcceptOn.setOnClickListener(this);
        autoAcceptOff.setOnClickListener(this);
        rowMapSettings.setOnClickListener(this);
        partnerMapSettings.setOnClickListener(this);
        partnerInbox.setOnClickListener(this);
        logoutPartner.setOnClickListener(this);

    }//onCreate

    @Override
    public void onClick(View v) {
        //handling multiple click events
        switch (v.getId()) {
            case R.id.pt_profile:
                Intent intent = new Intent(this, PartnerProfile.class);
                startActivity(intent);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            //merged cases
            case R.id.tr_pt_history:
            case R.id.partner_trip_history:
                Intent intent3 = new Intent(this, PartnerTripsHistory.class);
                startActivity(intent3);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.toggle_on:
                if (isOn) {
                    autoAcceptOn.setVisibility(View.INVISIBLE);
                    autoAcceptOff.setVisibility(View.VISIBLE);
                    isOn = false;
                }
                break;

            case R.id.toggle_off:
                if (!isOn) {
                    autoAcceptOn.setVisibility(View.VISIBLE);
                    autoAcceptOff.setVisibility(View.INVISIBLE);
                    isOn = true;
                }
                break;

            //merged cases
            case R.id.tr_pt_map_settings:
            case R.id.btn_map_settings:
                Intent intent1 = new Intent(this, PartnerMapSettings.class);
                startActivity(intent1);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            //merged cases
            case R.id.partner_earnings:
            case R.id.tr_pt_earnings:
                Intent intent2 = new Intent(this, PartnerEarnings.class);
                startActivity(intent2);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

                //merged cases
            case R.id.tr_pt_inbox:
            case R.id.btn_partnerInbox:
                Intent intent4 = new Intent(this, PartnerInbox.class);
                startActivity(intent4);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            case R.id.btn_logout_partner:
                Intent intent5 = new Intent(this , PartnerLogin.class);
                startActivity(intent5);

        }//end switch
    }//end onClick

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}//endClass