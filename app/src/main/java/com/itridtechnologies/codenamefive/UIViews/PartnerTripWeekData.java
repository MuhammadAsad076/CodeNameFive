package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsWeekData;
import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.TripsWeekDataAdapter;
import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PartnerTripWeekData extends AppCompatActivity {
    //declarations
    private RecyclerView recyclerViewTripWeekData;
    private TripsWeekDataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TripsWeekData> mTripWeekList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_trip_week_data);

        //Create Weekly Trip List & Build Recycler View
        createTripWeekList();
        buildRecyclerView();
    }//onCreate

    //public methods
    public void createTripWeekList() {
        mTripWeekList = new ArrayList<>();
        //add items
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "14 June" , "42 trips" , 100.0));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "13 June" , "42 trips" , 160.0));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "12 June" , " " , 0.0));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "11 June" , "46 trips" , 10.0));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "10 June" , "42 trips" , 2.86));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "9 June" , "42 trips" , 2000.87));
        mTripWeekList.add(new TripsWeekData(R.drawable.nav_next , "8 June" , "42 trips" , 48.67));

    }//end create

    public void buildRecyclerView() {
        recyclerViewTripWeekData = findViewById(R.id.recView_trip_week_data);
        recyclerViewTripWeekData.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TripsWeekDataAdapter(mTripWeekList);
        recyclerViewTripWeekData.setLayoutManager(mLayoutManager);
        recyclerViewTripWeekData.setAdapter(mAdapter);

        //click event
        mAdapter.setOnItemClickListener(new TripsWeekDataAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(PartnerTripWeekData.this, PartnerTripDayData.class);
                startActivity(intent);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }//end build RecView

}//endClass