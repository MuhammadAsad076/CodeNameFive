package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

import com.itridtechnologies.codenamefive.R;

public class PartnerEarnings extends AppCompatActivity implements View.OnClickListener {

    private TableRow partnerAccountStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_earnings);

        //find views
        partnerAccountStatement = findViewById(R.id.tr_partner_payments);

        //set listener
        partnerAccountStatement.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View v) {
        //handling click events
        switch (v.getId()) {

            case R.id.tr_partner_payments:
                Intent intent = new Intent(PartnerEarnings.this, PartnerPreviousPayments.class);
                startActivity(intent);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }//end switch
    }//end onClick
}//end class