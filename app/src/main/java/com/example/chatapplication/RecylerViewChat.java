package com.example.chatapplication;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stringee.messaging.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecylerViewChat extends RecyclerView.Adapter<RecylerViewChat.ViewHolder>  {
    Context mContext;
    OnItemLister lister;
    List<Account> mData = new ArrayList<>();
    LayoutInflater mInflater = null;
    private DatabaseReference databaseReference;
    FirebaseUser s;
    Chat chat_tmp = new Chat();
    List<Chat> list_chat;


    public RecylerViewChat(Context c, List<Account> data, OnItemLister l) {
        if (c != null) {
            mInflater = LayoutInflater.from(c);
        }
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
        s = FirebaseAuth.getInstance().getCurrentUser();
        readMessages(s.getUid(),mCurrent.getUid(),"",holder);
        holder.setIsRecyclable(false);
        try {
            Picasso.get().load(mCurrent.getImage()).into(holder.imageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.image_default).into(holder.imageView);
        }
        holder.txt_name.setText(String.valueOf(mCurrent.getUsername()));
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
            CircleImageView imageView;
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

        private void readMessages(final String myid, final String userid, String imageurl,@NonNull ViewHolder holder){
        list_chat = new ArrayList<Chat>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_chat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        list_chat.add(chat);
                    }
                }
                int i = 0;
                for (Chat chat2 : list_chat) {
                    Log.e("chat : ",chat2.getMessage());
                    if (i == 0) {
                        chat_tmp.setMessage(chat2.getMessage());
                        chat_tmp.setDate(chat2.getDate());
                        chat_tmp.setReceiver(chat2.getReceiver());
                        chat_tmp.setSender(chat2.getSender());
                        i++;
                    }
                    if (chat2.getReceiver().equals(myid) && chat2.getSender().equals(userid) ||
                            chat2.getReceiver().equals(userid) && chat2.getSender().equals(myid)) {
                        if(chat2 != null) {
                            System.out.println(chat_tmp.getDate());
                            if (chat_tmp.getDate() < chat2.getDate()) {
                                chat_tmp.setMessage(chat2.getMessage());
                                chat_tmp.setDate(chat2.getDate());
                                chat_tmp.setReceiver(chat2.getReceiver());
                                chat_tmp.setSender(chat2.getSender());
                            }
                        }
                    }
                }
                holder.txt_description.setText(chat_tmp.getMessage());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
