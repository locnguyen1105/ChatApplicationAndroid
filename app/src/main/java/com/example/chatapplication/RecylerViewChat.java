package com.example.chatapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Model.Account;

import java.util.ArrayList;
import java.util.List;

public class RecylerViewChat extends RecyclerView.Adapter<RecylerViewChat.ViewHolder>  {
    Context mContext;
    OnItemLister lister;
    List<Account> mData = new ArrayList<>();
    LayoutInflater mInflater;

    public RecylerViewChat(Context c, List<Account> data, OnItemLister l){
        mInflater = LayoutInflater.from(c);
        mContext = c;
        mData = data;
        lister = l;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.chat_item,
                parent, false);
        return new ViewHolder(mItemView, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Account mCurrent = mData.get(position);
        holder.setIsRecyclable(false);
        holder.txt_name.setText(String.valueOf(mCurrent.getUsername()));
        holder.txt_description.setText(String.valueOf(mCurrent.getEmail()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lister.onListViewClick(mCurrent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView txt_name, txt_description;
            RecylerViewChat recylerV;
            CardView cardView;
        public ViewHolder(@NonNull View itemView, RecylerViewChat testAdapter) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.name);
            txt_description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.img);
            this.recylerV = testAdapter;
        }
    }

    interface OnItemLister {
        void onListViewClick(Account account);
    }
}
