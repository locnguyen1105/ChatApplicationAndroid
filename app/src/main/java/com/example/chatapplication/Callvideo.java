package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.stringee.call.StringeeCall;

import org.json.JSONObject;

public class Callvideo extends AppCompatActivity {
    private StringeeCall stringeeCall;

    private ImageButton cancel;
    private FrameLayout usercamera,receivercamera;
    private String from , to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callvideo);

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        cancel = findViewById(R.id.cancel);

        usercamera = findViewById(R.id.usercamera);
        receivercamera = findViewById(R.id.receivercamera);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null){
                    stringeeCall.hangup();
                    finish();
                }
            }
        });
        makeCall();

    }

    private void makeCall() {
        stringeeCall = new StringeeCall(Callvideo.this,MainActivity.stringeeClient,from,to);

        stringeeCall.setVideoCall(true);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                if(signalingState == StringeeCall.SignalingState.ANSWERED){
                    ((MotionLayout)findViewById(R.id.motionlayout)).transitionToEnd();
                    cancel.setVisibility(View.VISIBLE);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usercamera.addView(stringeeCall.getLocalView());
                        stringeeCall.renderLocalView(true);
                    }
                });
            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receivercamera.addView(stringeeCall.getRemoteView());
                        stringeeCall.renderRemoteView(false);
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });
        stringeeCall.makeCall();
    }
}