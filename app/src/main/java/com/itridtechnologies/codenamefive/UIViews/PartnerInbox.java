package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.InboxItems;
import com.itridtechnologies.codenamefive.AdaptersAndViewHolders.InboxItemsAdapter;
import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PartnerInbox extends AppCompatActivity {

    private RecyclerView InboxItemRecyclerView;
    private InboxItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<InboxItems> mInboxArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_inbox);

        //create inbox list and build recycler view
        createInboxList();
        buildRecyclerView();
    }//onCreate

    private void createInboxList() {
        mInboxArrayList = new ArrayList<>();
        //set values
        mInboxArrayList.add(new InboxItems("This is a promotion message click here to get free discount, hurry up before time runs out"
        , "17 June 2020" , "14:30"));
        mInboxArrayList.add(new InboxItems("This is a promotion message click here to get free discount, hurry up before time runs out"
                , "17 June 2020" , "14:30"));
        mInboxArrayList.add(new InboxItems("This is a promotion message click here to get free discount, hurry up before time runs out"
                , "17 June 2020" , "14:30"));
        mInboxArrayList.add(new InboxItems("This is a promotion message click here to get free discount, hurry up before time runs out"
                , "17 June 2020" , "14:30"));
    }//end create

    private void buildRecyclerView() {
        InboxItemRecyclerView = findViewById(R.id.recView_partner_inbox);
        InboxItemRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new InboxItemsAdapter(mInboxArrayList);
        InboxItemRecyclerView.setLayoutManager(mLayoutManager);
        InboxItemRecyclerView.setAdapter(mAdapter);

        //click event
        mAdapter.setOnItemClickListener(new InboxItemsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Toast.makeText(PartnerInbox.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }//end build recView

}//endClass