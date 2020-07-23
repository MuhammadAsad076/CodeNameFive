package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsHistory;
import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsHistoryAdapter;
import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PartnerTripsHistory extends AppCompatActivity {

    private ArrayList<TripsHistory> mRiderTripList;
    private RecyclerView mRecyclerView;
    private TripsHistoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_trips_history);

        //createTripList & buildRecyclerView
        createTripList();
        buildRecyclerView();
    }//onCreate

    //public methods
    public void createTripList() {
        mRiderTripList = new ArrayList<>();
        mRiderTripList.add(new TripsHistory(R.drawable.nav_next , "8 June" , "14 June" , 100.0));
        mRiderTripList.add(new TripsHistory(R.drawable.nav_next , "1 June" , "7 June" , 70.0));
        mRiderTripList.add(new TripsHistory(R.drawable.nav_next , "25 June" , "31 June" , 0.0));
        mRiderTripList.add(new TripsHistory(R.drawable.nav_next , "18 June" , "24 June" , 300.0));
    }
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recView_trips_history);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TripsHistoryAdapter(mRiderTripList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new TripsHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showTripsWeekHistory(position);
            }
        });
    }//end build RecView

    public void showTripsWeekHistory(int position) {
        Intent intent = new Intent(PartnerTripsHistory.this, PartnerTripWeekData.class);
        startActivity(intent);
        //open anim
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}//endClass