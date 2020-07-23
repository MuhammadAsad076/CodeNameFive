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

public class TripsHistoryAdapter extends RecyclerView.Adapter<TripsHistoryAdapter.TripsViewHolder> {

    private ArrayList<TripsHistory> mTripList;
    private OnItemClickListener mListener;

    //listener interface
    public interface OnItemClickListener {
        public void onItemClick(int position);
    }//end interface

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }//onClick

    public static class TripsViewHolder extends RecyclerView.ViewHolder {
        //declarations
        public ImageView mImageView;
        public TextView mFromDate;
        public TextView mToDate;
        public TextView mEarnedRupees;

        public TripsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            //find views
            mImageView = itemView.findViewById(R.id.img_goto_detail);
            mFromDate = itemView.findViewById(R.id.tv_from_date);
            mToDate = itemView.findViewById(R.id.tv_to_date);
            mEarnedRupees = itemView.findViewById(R.id.tv_earned_rupees);
            //setListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }//inner class

    public TripsHistoryAdapter(ArrayList<TripsHistory> mTripList) {
        this.mTripList = mTripList;
    }//super constructor

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_trip_history_item, parent, false);
        TripsViewHolder tvh = new TripsViewHolder(v , mListener);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        TripsHistory currentItem = mTripList.get(position);
        //getValues
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mFromDate.setText(currentItem.getFromDate());
        holder.mToDate.setText(currentItem.getToDate());
        holder.mEarnedRupees.setText(String.valueOf(currentItem.getEarnedRupees()));
    }

    @Override
    public int getItemCount() {
        return mTripList.size();
    }
}//end class
