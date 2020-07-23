package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class PreviousPaymentsAdapter extends RecyclerView.Adapter<PreviousPaymentsAdapter.PaymentsViewHolder> {

    private ArrayList<PreviousPayments> mPaymentsList;
    private OnItemClickListener mListener;

    //interface
    public interface OnItemClickListener {
        public void OnItemClick(int position);
    }//end interface

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }//end set

    public PreviousPaymentsAdapter(ArrayList<PreviousPayments> mPaymentsList) {
        this.mPaymentsList = mPaymentsList;
    }//super constructor

    public static class PaymentsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView fromDate;
        private TextView toDate;
        private TextView paymentStatus;
        private TextView paymentTotal;

        public PaymentsViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);
            //find views
            mImageView = itemView.findViewById(R.id.img_goto_payment);
            fromDate = itemView.findViewById(R.id.tv_date_from_payments);
            toDate = itemView.findViewById(R.id.tv_date_to_payments);
            paymentStatus = itemView.findViewById(R.id.tv_payment_status);
            paymentTotal = itemView.findViewById(R.id.tv_total_payments);
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
        }
    }//inner class

    //implement methods

    @NonNull
    @Override
    public PaymentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_payment_item, parent, false);
        PaymentsViewHolder pvh= new PaymentsViewHolder(v , mListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsViewHolder holder, int position) {
        PreviousPayments currentItem = mPaymentsList.get(position);
        //get values
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.fromDate.setText(currentItem.getFromDate());
        holder.toDate.setText(currentItem.getToDate());
        holder.paymentStatus.setText(currentItem.getPaymentStatus());
        holder.paymentTotal.setText(String.valueOf(currentItem.getPaymentTotal()));
    }

    @Override
    public int getItemCount() {
        return mPaymentsList.size();
    }
}//end class
