package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeConstant;

import org.json.JSONObject;

public class CallingActivity extends AppCompatActivity {
    private StringeeCall stringeeCall;

    ImageButton accept , cancel,reject;

    FrameLayout usercall,receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.cancel);
        reject = findViewById(R.id.reject);
        receiver = findViewById(R.id.receiver_camera);
        usercall = findViewById(R.id.usercall);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null){
                    stringeeCall.answer();
                    cancel.setVisibility(View.VISIBLE);
                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);


                    ((MotionLayout)findViewById(R.id.motioncalling)).transitionToEnd();
                    usercall.setVisibility(View.VISIBLE);
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null) {
                    stringeeCall.reject();
                }
            }
        });

        initAnswer();

    }

    private void initAnswer() {
        String callId = getIntent().getStringExtra("call_id");
        stringeeCall = MainActivity.callMap.get(callId);
        stringeeCall.enableVideo(true);
        stringeeCall.setQuality(StringeeConstant.QUALITY_FULLHD);
        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {

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
                        receiver.addView(stringeeCall.getLocalView());
                        stringeeCall.renderLocalView(true);
                    }
                });
            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usercall.addView(stringeeCall.getRemoteView());
                        stringeeCall.renderRemoteView(false);
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });
        stringeeCall.initAnswer(CallingActivity.this,MainActivity.stringeeClient);
    }
}