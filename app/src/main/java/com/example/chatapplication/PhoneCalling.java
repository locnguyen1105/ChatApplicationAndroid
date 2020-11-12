package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.stringee.call.StringeeCall;

import org.json.JSONObject;

public class PhoneCalling extends AppCompatActivity {
    private StringeeCall stringeeCall;
    private String from , to;
    ImageButton cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_calling);
        cancel = findViewById(R.id.cancelphone);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.hangup();
                finish();
            }
        });

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        makeCall();
    }

    private void makeCall() {

        stringeeCall = new StringeeCall(PhoneCalling.this,MainActivity.stringeeClient,from,to);


        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                if(signalingState == StringeeCall.SignalingState.ANSWERED){
                    ((MotionLayout)findViewById(R.id.motionlayout)).transitionToEnd();
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