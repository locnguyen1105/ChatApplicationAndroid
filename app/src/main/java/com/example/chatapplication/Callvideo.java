package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stringee.call.StringeeCall;

import org.json.JSONObject;
import org.webrtc.CameraVideoCapturer;

public class Callvideo extends AppCompatActivity {
    private StringeeCall stringeeCall;

    private ImageButton cancel,onmic,offmic,onvolume,offvolume;
    private FrameLayout usercamera,receivercamera;
    private String from , to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callvideo);

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        cancel = findViewById(R.id.cancelphonecalling);
        onmic = findViewById(R.id.onmic);
        offmic = findViewById(R.id.onmic);
        onvolume = findViewById(R.id.onmic);
        offvolume = findViewById(R.id.onmic);

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

        usercamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                    @Override
                    public void onCameraSwitchDone(boolean b) {

                    }

                    @Override
                    public void onCameraSwitchError(String s) {
                        Toast.makeText(Callvideo.this,"error switching camera",Toast.LENGTH_LONG);
                    }
                });
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