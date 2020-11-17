package com.example.chatapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapplication.Model.Account;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {
    Context mContext;
    OnItemLister lister;
    List<Account> mData;

    public ListUserAdapter(Context c, List<Account> data, OnItemLister l) {
        mContext = c;
        mData = data;
        lister = l;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name;
        OnItemLister onItemLister;
        CardView cardView;

        public ViewHolder(@NonNull View itemView, OnItemLister lister) {
            super(itemView);
            name = itemView.findViewById(R.id.usernameA);
            cardView = itemView.findViewById(R.id.container1);
            imageView = itemView.findViewById(R.id.imageadap);
            this.onItemLister = lister;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.list_user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(heroView, lister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Account mCurrent = mData.get(position);
        holder.name.setText(mCurrent.getUsername());
        if (mCurrent.getImage() != "") {
            Glide.with(mContext).load(mCurrent.getImage()).into(holder.imageView);

        }

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


    interface OnItemLister {
        void onListViewClick(Account account);
    }
}

