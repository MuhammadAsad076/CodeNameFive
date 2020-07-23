package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.PreviousPayments;
import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.PreviousPaymentsAdapter;
import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PartnerPreviousPayments extends AppCompatActivity {

    private RecyclerView previousPaymentsRecyclerView;
    private PreviousPaymentsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<PreviousPayments> mPreviousPaymentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_previous_payments);

        //create list and build recycler view
        createPreviousPaymentsList();
        buildRecyclerView();
    }//onCreate

    private void createPreviousPaymentsList() {
        mPreviousPaymentsList = new ArrayList<>();
        //set values
        mPreviousPaymentsList.add(new PreviousPayments(R.drawable.nav_next , "2 June" , "7 June",
                "Unpaid" , 278.87));
        mPreviousPaymentsList.add(new PreviousPayments(R.drawable.nav_next , "26 May" , "31 May",
                "Paid" , 305.00));
        mPreviousPaymentsList.add(new PreviousPayments(R.drawable.nav_next , "3 July" , "8 July",
                "Unpaid" , 45.58));
    }//end create payment list

    private void buildRecyclerView() {
        previousPaymentsRecyclerView = findViewById(R.id.recView_previous_payments);
        previousPaymentsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new PreviousPaymentsAdapter(mPreviousPaymentsList);
        previousPaymentsRecyclerView.setLayoutManager(mLayoutManager);
        previousPaymentsRecyclerView.setAdapter(mAdapter);

        //click event
        mAdapter.setOnItemClickListener(new PreviousPaymentsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(PartnerPreviousPayments.this, PartnerInvoiceDetails.class);
                startActivity(intent);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }//end build recView
}//end class