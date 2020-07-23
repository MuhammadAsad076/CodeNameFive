package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;

import com.itridtechnologies.codenamefive.R;

public class PartnerProfile extends AppCompatActivity implements View.OnClickListener {

    private TableRow rowEditProfile;
    private TableRow rowVehicleInfo;
    private TableRow rowBankInfo;
    private ImageView closeProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_profile);

        //findViews
        rowEditProfile = findViewById(R.id.tr_pt_edit_profile);
        rowVehicleInfo = findViewById(R.id.tr_pt_vehicle_info);
        rowBankInfo = findViewById(R.id.tr_pt_bank_info);
        closeProfile = findViewById(R.id.close_profile);
        //setListeners
        rowEditProfile.setOnClickListener(this);
        rowVehicleInfo.setOnClickListener(this);
        rowBankInfo.setOnClickListener(this);
        closeProfile.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View v) {
        //handling click events
        switch (v.getId()) {

            case R.id.tr_pt_edit_profile:
                Intent intent1 = new Intent(this, PartnerEditProfile.class);
                startActivity(intent1);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.tr_pt_vehicle_info:
                Intent intent2 = new Intent(this, PartnerVehicleInfo.class);
                startActivity(intent2);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.tr_pt_bank_info:
                Intent intent3 = new Intent(this, PartnerBankInfo.class);
                startActivity(intent3);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }//switch
    }//end OnClick
}//endClass