package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chatapplication.Model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stringee.call.StringeeCall;

import org.json.JSONObject;

public class PhoneCalling extends AppCompatActivity {
    private StringeeCall stringeeCall;
    private ImageView avatarphone;
    private String from , to;
    ImageButton cancel,offmic,onvolume,offvolume,onmic;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_calling);
        cancel = findViewById(R.id.cancelphone);
        avatarphone = findViewById(R.id.avatarphone);
        offmic = findViewById(R.id.offmic);
        onmic = findViewById(R.id.onmic);
        onvolume = findViewById(R.id.onvolume);
        offvolume = findViewById(R.id.offvolume);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.hangup();
                finish();
            }
        });

        offmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.mute(true);
                onmic.setVisibility(View.VISIBLE);
                offmic.setVisibility(View.GONE);
            }
        });

        onmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.mute(false);
                offmic.setVisibility(View.VISIBLE);
                onmic.setVisibility(View.GONE);
            }
        });

        offvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.setSpeakerphoneOn(false);
                onvolume.setVisibility(View.VISIBLE);
                offvolume.setVisibility(View.GONE);
            }
        });

        onvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.setSpeakerphoneOn(true);
                onvolume.setVisibility(View.GONE);
                offvolume.setVisibility(View.VISIBLE);
            }
        });

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account acc = ds.getValue(Account.class);
                    if(acc.getEmail().equals(to)) {
                        try {
                            Picasso.get().load(acc.getImage()).into(avatarphone);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.image_default).into(avatarphone);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        makeCall();
    }

    private void makeCall() {

        stringeeCall = new StringeeCall(PhoneCalling.this,MainActivity.stringeeClient,from,to);


        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                if(signalingState == StringeeCall.SignalingState.ANSWERED){
                    ((MotionLayout)findViewById(R.id.phonecalling)).transitionToEnd();
                }else if(signalingState == StringeeCall.SignalingState.ENDED ) {
                    finish();
                }
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {

            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {

            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {

            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });
        stringeeCall.makeCall();
    }
}