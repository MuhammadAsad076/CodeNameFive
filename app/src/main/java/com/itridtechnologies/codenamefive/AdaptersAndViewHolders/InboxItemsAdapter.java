package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itridtechnologies.codenamefive.R;

import java.util.ArrayList;

public class InboxItemsAdapter extends RecyclerView.Adapter<InboxItemsAdapter.InboxItemViewHolder> {

    //data members
    private ArrayList<InboxItems> mInboxItemsList;
    private OnItemClickListener mListener;

    public InboxItemsAdapter(ArrayList<InboxItems> mInboxItemsList) {
        this.mInboxItemsList = mInboxItemsList;
    }//super constructor

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public InboxItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_inbox_item, parent, false);
        return new InboxItemViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxItemViewHolder holder, int position) {
        InboxItems currentItem = mInboxItemsList.get(position);
        //get values
        holder.inboxMessage.setText(currentItem.getInboxMessage());
        holder.messageTime.setText(currentItem.getTimeOfMessage());
        holder.messageDate.setText(currentItem.getDateOfMessage());
    }

    //override methods

    @Override
    public int getItemCount() {
        return mInboxItemsList.size();
    }

    //interface
    public interface OnItemClickListener {
        void OnItemClick(int position);
    }//end interface

    public static class InboxItemViewHolder extends RecyclerView.ViewHolder {

        private TextView inboxMessage;
        private TextView messageDate;
        private TextView messageTime;

        public InboxItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            //find views
            inboxMessage = itemView.findViewById(R.id.tv_message);
            messageDate = itemView.findViewById(R.id.tv_message_date);
            messageTime = itemView.findViewById(R.id.tv_message_time);
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
}//end class
