package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeConstant;

import org.json.JSONObject;

public class CallingActivity extends AppCompatActivity {
    private StringeeCall stringeeCall;

    ImageButton accept , cancel;

    FrameLayout usercall;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.cancel);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null){
                    stringeeCall.answer();
                    accept.setVisibility(View.GONE);
                }

            }
        });
        usercall = findViewById(R.id.user_call);

        initAnswer();

    }

    private void initAnswer() {
        String callId = getIntent().getStringExtra("call_id");
        stringeeCall = ChatActivity.callMap.get(callId);
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
                        usercall.addView(stringeeCall.getLocalView());
                        stringeeCall.renderLocalView(true);
                    }
                });
            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        receivercamera.addView(stringeeCall.getRemoteView());
//                        stringeeCall.renderRemoteView(false);
//                    }
//                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });
        stringeeCall.initAnswer(CallingActivity.this,ChatActivity.stringeeClient);
    }
}