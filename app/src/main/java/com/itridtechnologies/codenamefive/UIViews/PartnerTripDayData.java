package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsDayData;
import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsDayDataAdapter;
import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PartnerTripDayData extends AppCompatActivity {

    private RecyclerView tripDayDataRecyclerView;
    private TripsDayDataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TripsDayData> mDayTripList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_trip_day_data);

        //Create Weekly Trip List & Build Recycler View
        createTripDayList();
        buildRecyclerView();
    }//onCreate

    private void createTripDayList() {
        mDayTripList = new ArrayList<>();
        //add items
        mDayTripList.add(new TripsDayData(R.drawable.nav_next , "McDonalds" , "21:21"
        , "21:40" , "Tip" , 3.87));
        mDayTripList.add(new TripsDayData(R.drawable.nav_next , "Golden Chicken" , "21:21"
                , "21:40" , "Tip" , 2.64));
        mDayTripList.add(new TripsDayData(R.drawable.nav_next , "Hardees" , "21:21"
                , "21:40" , "" , 5.07));
    }//end create list

    private void buildRecyclerView() {

        tripDayDataRecyclerView = findViewById(R.id.recView_trip_day_data);
        tripDayDataRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TripsDayDataAdapter(mDayTripList);
        tripDayDataRecyclerView.setLayoutManager(mLayoutManager);
        tripDayDataRecyclerView.setAdapter(mAdapter);

        //click event
        mAdapter.setOnItemClickListener(new TripsDayDataAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(PartnerTripDayData.this, PartnerOrderDetail.class);
                startActivity(intent);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }//end build recView
}//endClass