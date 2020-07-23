package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

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

public class TripsDayDataAdapter extends RecyclerView.Adapter<TripsDayDataAdapter.DayDataViewHolder>{

    private ArrayList<TripsDayData> mTripDayList;
    private OnItemClickListener mListener;

    //interface for handling click events
    public interface OnItemClickListener {
        public void OnItemClick (int position);
    }//end interface

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }//end set

    public static class DayDataViewHolder extends RecyclerView.ViewHolder {
        //member variables
        public ImageView mImageView;
        public TextView restaurantName;
        public TextView tripStartTime;
        public TextView tripEndTime;
        public TextView tripTip;
        public TextView earnedPerTrip;

        public DayDataViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);
            //find views
            mImageView = itemView.findViewById(R.id.img_goto_day_detail);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tripStartTime = itemView.findViewById(R.id.tv_trips_start_time);
            tripEndTime = itemView.findViewById(R.id.tv_trips_end_time);
            tripTip = itemView.findViewById(R.id.tv_trip_tip);
            earnedPerTrip = itemView.findViewById(R.id.tv_cost_per_trip);
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

    public TripsDayDataAdapter(ArrayList<TripsDayData> mTripDayList) {
        this.mTripDayList = mTripDayList;
    }//super constructor

    @NonNull
    @Override
    public DayDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_trip_day_data_item, parent, false);
        DayDataViewHolder ddvh= new DayDataViewHolder(v , mListener);
        return ddvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DayDataViewHolder holder, int position) {
        TripsDayData currentItem = mTripDayList.get(position);
        //get values
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.restaurantName.setText(currentItem.getRestaurantName());
        holder.tripStartTime.setText(currentItem.getTripStartTime());
        holder.tripEndTime.setText(currentItem.getTripEndTime());
        holder.tripTip.setText(currentItem.getTripTip());
        holder.earnedPerTrip.setText(String.valueOf(currentItem.getEarnedPerTrip()));
    }

    @Override
    public int getItemCount() {
        return mTripDayList.size();
    }
}//end class
