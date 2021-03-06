package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Model.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment implements RecylerViewChat.OnItemLister {
    private RecyclerView lv;
    private List<Account> list_acc;
    private RecylerViewChat adapter;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        lv = view.findViewById(R.id.recyclerView2);
        lv.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_acc = new ArrayList<Account>();
        readUsers();
        return view;
    }

    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_acc.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Log.e("user",firebaseUser.getUid());
                    Account acc = snapshot1.getValue(Account.class);
                    assert acc != null;
                    assert firebaseUser != null;
                    if(!firebaseUser.getUid().equals(acc.getUid())){
                        list_acc.add(acc);
                    }
                }
                adapter = new RecylerViewChat(getActivity(), list_acc, ChatFragment.this);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error : ", String.valueOf(error));
            }
        });
    }

    @Override
    public void onListViewClick(Account account) {
        Intent intent = new Intent(getActivity(),ChatActivity.class);
        intent.putExtra("account_id",account.getUid());
        startActivity(intent);
    }
}