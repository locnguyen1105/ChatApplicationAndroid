package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Model.Account;
import com.example.chatapplication.Model.Chat;
import com.example.chatapplication.Stringee.GenAccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//GenAccessToken.genAccessToken("SKfbWu6u2A8CfUckQZDNlbLC9TKvTa5oU", "TzNCaAlJac3NWUVROd2ZPNE9PTEc1Mk9iZVI0QW5NQ0E=", 360000)
public class ChatActivity extends AppCompatActivity implements RecylerViewMessage.OnItemLister {

    private ImageView imageView,phone,camera;
    private TextView textView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private Intent intent;
    private ImageButton imageButton;
    private EditText editText;
    private Account receiver;
    private RecyclerView recyclerView;
    private RecylerViewMessage adapter;
    List<Chat> list_chat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        phone = findViewById(R.id.phone);
        camera = findViewById(R.id.camera);

        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.name);
        toolbar = findViewById(R.id.toolbar);
        imageButton = findViewById(R.id.btn_send);
        editText = findViewById(R.id.message);
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if(!"".equals(msg)){
                    sendMessage(firebaseUser.getUid(),receiver.getUid(),msg);
                }else{

                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        intent = getIntent();
        final String acc_id = intent.getStringExtra("account_id");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(acc_id);

        //get infor receiver && read image
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiver = snapshot.getValue(Account.class);
                textView.setText(receiver.getUsername());

                readMessages(firebaseUser.getUid(),receiver.getUid(),null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // call video
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,Callvideo.class);
                intent.putExtra("from",MainActivity.stringeeClient.getUserId());
                intent.putExtra("to",receiver.getEmail());

                startActivity(intent);
            }
        });
    }



    private void sendMessage(String sender , String receiver , String message){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        databaseReference.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(final String myid, final String userid, String imageurl){
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
                adapter = new RecylerViewMessage(ChatActivity.this,list_chat,ChatActivity.this);
                recyclerView.setAdapter(adapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onListViewClick(Chat account) {

    }
}