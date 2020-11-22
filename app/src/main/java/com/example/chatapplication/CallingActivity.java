package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeConstant;

import org.json.JSONObject;
import org.webrtc.CameraVideoCapturer;

public class CallingActivity extends AppCompatActivity {
    private StringeeCall stringeeCall;

    ImageButton accept , cancel,reject,onmic,offmic,onvolume,offvolume;

    FrameLayout usercall,receiver;

    MotionLayout motionLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.cancelphonecalling);
        reject = findViewById(R.id.reject);
        receiver = findViewById(R.id.receiver_camera);
        usercall = findViewById(R.id.usercall);
        onmic = findViewById(R.id.onmic);
        offmic = findViewById(R.id.offmic);
        onvolume = findViewById(R.id.onvolume);
        offvolume = findViewById(R.id.offvolume);
        motionLayout = findViewById(R.id.motioncalling);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null){
                    stringeeCall.answer();
                    cancel.setVisibility(View.VISIBLE);
                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);
                    offmic.setVisibility(View.VISIBLE);
                    offvolume.setVisibility(View.VISIBLE);
                    usercall.setVisibility(View.VISIBLE);
                    ((MotionLayout)findViewById(R.id.motioncalling)).setTransition(R.id.start, R.id.end);
                    ((MotionLayout)findViewById(R.id.motioncalling)).transitionToEnd();
                    usercall.setVisibility(View.VISIBLE);

                    receiver.setOnTouchListener(new View.OnTouchListener()  {
                        float dX, dY;
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_CANCEL: {
                                    break;
                                }
                                case MotionEvent.ACTION_DOWN: {

                                    dX = view.getX() - event.getRawX();
                                    dY = view.getY() - event.getRawY();
                                    break;
                                }

                                case MotionEvent.ACTION_MOVE: {
                                    view.animate()
                                            .x(event.getRawX() + dX)
                                            .y(event.getRawY() + dY)
                                            .setDuration(0)
                                            .start();
                                    break;
                                }
                                case MotionEvent.ACTION_UP:{
                                    if(view.getX() < 0 && view.getY() + view.getHeight() > motionLayout.getHeight()){
                                        view.animate()
                                                .x(0)
                                                .y(motionLayout.getHeight() - view.getHeight())
                                                .setDuration(200)
                                                .start();
                                    }else if (view.getX() + view.getWidth() > motionLayout.getWidth() && view.getY() + view.getHeight() > motionLayout.getHeight()){
                                        view.animate()
                                                .x(motionLayout.getWidth() - view.getWidth())
                                                .y(motionLayout.getHeight() - view.getHeight())
                                                .setDuration(200)
                                                .start();
                                    }else if(view.getX() < 0 && view.getY() < 0){
                                        view.animate()
                                                .x(0)
                                                .y(0)
                                                .setDuration(200)
                                                .start();
                                    }else if(view.getX() + view.getWidth() > motionLayout.getWidth() && view.getY() < 0 ){
                                        view.animate()
                                                .x(motionLayout.getWidth() - view.getWidth())
                                                .y(0)
                                                .setDuration(200)
                                                .start();
                                    }else if(view.getX() < 0) {
                                        view.animate()
                                                .x(0)
                                                .y(event.getRawY() + dY)
                                                .setDuration(200)
                                                .start();
                                    }else if(view.getX() + view.getWidth() > motionLayout.getWidth()) {
                                        view.animate()
                                                .x(motionLayout.getWidth() - view.getWidth())
                                                .y(event.getRawY() + dY)
                                                .setDuration(200)
                                                .start();
                                    }else if (view.getY() < 0){
                                        view.animate()
                                                .x(event.getRawX() + dX)
                                                .y(0)
                                                .setDuration(200)
                                                .start();
                                    }else if (view.getY() + view.getHeight() > motionLayout.getHeight()){
                                        view.animate()
                                                .x(event.getRawX() + dX)
                                                .y(motionLayout.getHeight() - view.getHeight())
                                                .setDuration(200)
                                                .start();
                                    }else {
                                        float calculateX = view.getX() + view.getWidth()/2;
                                        if( calculateX < motionLayout.getWidth()/2 ){
                                            view.animate()
                                                    .x(0)
                                                    .y(event.getRawY() + dY)
                                                    .setDuration(200)
                                                    .start();
                                        }else if (calculateX > motionLayout.getWidth()/2 )  {
                                            view.animate()
                                                    .x(motionLayout.getWidth() - view.getWidth())
                                                    .y(event.getRawY() + dY)
                                                    .setDuration(200)
                                                    .start();
                                        }
                                    }
                                }
                                default:
                                    return false;
                            }
                            return true;
                        }
                    });
                }

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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.hangup();
                finish();
            }
        });

        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                    @Override
                    public void onCameraSwitchDone(boolean b) {
                        Toast.makeText(CallingActivity.this,"error switching camera",Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onCameraSwitchError(String s) {

                    }
                });
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stringeeCall != null) {
                    stringeeCall.reject();
                    finish();
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
                if(signalingState == StringeeCall.SignalingState.ENDED ) {
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