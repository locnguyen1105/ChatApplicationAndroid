package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.stringee.StringeeClient;
import com.stringee.listener.StatusListener;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.ConversationOptions;
import com.stringee.messaging.Message;
import com.stringee.messaging.StringeeChange;
import com.stringee.messaging.User;
import com.stringee.messaging.listeners.CallbackListener;
import com.stringee.messaging.listeners.ChangeEventListenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//GenAccessToken.genAccessToken("SKfbWu6u2A8CfUckQZDNlbLC9TKvTa5oU", "TzNCaAlJac3NWUVROd2ZPNE9PTEc1Mk9iZVI0QW5NQ0E=", 360000)
public class ChatActivity extends AppCompatActivity implements RecylerViewMessage.OnItemLister {
    private String token;

    private CircleImageView imageView;
    private ImageView phone,camera;
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
    StringeeClient stringeeClient;
    ConversationOptions options;
    List<User> userList;



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
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        userList = new ArrayList<>();


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();

                stringeeClient.createConversation( userList, options, new CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        Conversation conversation1 = new Conversation();
                        Message message = new Message(msg);
                        conversation.sendMessage(stringeeClient, message, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                if(!"".equals(msg)){
                                    sendMessage(firebaseUser.getUid(),receiver.getUid(),msg);
                                    editText.setText("");
                                    Log.e("CONVERSTATION : ",conversation.getLastMsg());
                                }else{

                                }
                            }
                        });
                    }
                });
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
                try {
                    Picasso.get().load(receiver.getImage()).into(imageView);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.image_default).into(imageView);
                }
                User user = new User(receiver.getEmail());
                user.setName(receiver.getUsername());
                user.setAvatarUrl(receiver.getImage());
                user.setRole("member");
                userList.add(user);

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
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,PhoneCalling.class);
                intent.putExtra("from",MainActivity.stringeeClient.getUserId());
                intent.putExtra("to",receiver.getEmail());

                startActivity(intent);
            }
        });

        // string ee chat
        stringeeClient = MainActivity.stringeeClient;

        //Stringee


        options = new ConversationOptions();
        options.setName("Chat App");
        options.setGroup(false);
        options.setDistinct(true);
        stringeeClient = MainActivity.stringeeClient;

//        Message message = (Message)stringeeChange.getObject();
//        message.markAsRead(stringeeClient, new StatusListener() {
//            @Override
//            public void onSuccess() {
//            }
//        });

    }



    private void sendMessage(String sender , String receiver , String message){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("date",System.currentTimeMillis());

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