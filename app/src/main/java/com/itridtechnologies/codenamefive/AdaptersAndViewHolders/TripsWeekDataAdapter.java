package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class TripsWeekDataAdapter extends RecyclerView.Adapter<TripsWeekDataAdapter.TripWeekViewHolder> {
    
    private ArrayList<TripsWeekData> mTripWeekList;
    private OnItemClickListener mListener;
    //interface for click events
    public interface OnItemClickListener {
        public void OnItemClick (int position);
        }//end interface

    public void setOnItemClickListener (OnItemClickListener listener) {
        mListener = listener;
    }

    public static class TripWeekViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView tripDate;
        public TextView tripsCompleted;
        public TextView tripCost;

        public TripWeekViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);
            //find views
            mImageView = itemView.findViewById(R.id.img_goto_week_detail);
            tripDate = itemView.findViewById(R.id.tv_trip_date);
            tripsCompleted = itemView.findViewById(R.id.tv_trips_completed);
            tripCost = itemView.findViewById(R.id.tv_trip_cost);
            //set listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });

        }//end constructor
    }//inner class

    public TripsWeekDataAdapter(ArrayList<TripsWeekData> mTripWeekList) {
        this.mTripWeekList = mTripWeekList;
    }//super constructor

    @NonNull
    @Override
    public TripWeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_trip_week_data_item, parent, false);
        TripWeekViewHolder twvh= new TripWeekViewHolder(v , mListener);
        return twvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TripWeekViewHolder holder, int position) {
        TripsWeekData currentItem = mTripWeekList.get(position);
        //getValues
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.tripDate.setText(currentItem.getTripDate());
        holder.tripsCompleted.setText(currentItem.getTripsCompleted());
        holder.tripCost.setText(String.valueOf(currentItem.getTripCost()));
    }

    @Override
    public int getItemCount() {
        return mTripWeekList.size();
    }
}//end class
