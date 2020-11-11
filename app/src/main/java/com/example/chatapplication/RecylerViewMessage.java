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
import com.example.chatapplication.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RecylerViewMessage extends RecyclerView.Adapter<RecylerViewMessage.ViewHolder> {
    Context mContext;
    OnItemLister lister;
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    List<Chat> mData = new ArrayList<>();
    LayoutInflater mInflater;

    FirebaseUser firebaseUser;

    public RecylerViewMessage(Context c, List<Chat> data, OnItemLister l){
        mInflater = LayoutInflater.from(c);
        mContext = c;
        mData = data;
        lister = l;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        if(viewType == MSG_RIGHT) {
            mItemView = mInflater.inflate(R.layout.chat_item_right,
                    parent, false);
        }else {
            mItemView = mInflater.inflate(R.layout.chat_item_left,
                    parent, false);
        }
        return new ViewHolder(mItemView, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chat mCurrent = mData.get(position);
        holder.setIsRecyclable(false);
//        holder.image.setText(String.valueOf(mCurrent.getUsername()));
        holder.show_message.setText(String.valueOf(mCurrent.getMessage()));
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lister.onListViewClick(mCurrent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView show_message;
        RecylerViewMessage recylerV;
        CardView cardView;
        public ViewHolder(@NonNull View itemView, RecylerViewMessage testAdapter) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            image = itemView.findViewById(R.id.image);
            this.recylerV = testAdapter;
        }
    }

    interface OnItemLister {
        void onListViewClick(Chat account);
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mData.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT;
        }else{
            return MSG_LEFT;
        }
    }
}
