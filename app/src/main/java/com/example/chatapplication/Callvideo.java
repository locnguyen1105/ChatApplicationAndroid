package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stringee.call.StringeeCall;

import org.json.JSONObject;
import org.webrtc.CameraVideoCapturer;

public class Callvideo extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{
    private static final int INVALID_POINTER_ID = 0;
    private static final String DEBUG_TAG = "DEBUG" ;
    private StringeeCall stringeeCall;
    private MotionLayout motionLayout;
    private ImageButton cancel,onmic,offmic,onvolume,offvolume;
    private FrameLayout usercamera,receivercamera;
    private String from , to;
    private int mActivePointerId;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callvideo);
        mDetector = new GestureDetectorCompat(this, this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        cancel = findViewById(R.id.cancelphonecalling);
        onmic = findViewById(R.id.onmic);
        offmic = findViewById(R.id.offmic);
        onvolume = findViewById(R.id.onvolume);
        offvolume = findViewById(R.id.offvolume);
        motionLayout = findViewById(R.id.motionlayout);

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
        makeCall();

    }

    private void makeCall() {
        stringeeCall = new StringeeCall(Callvideo.this,MainActivity.stringeeClient,from,to);

        stringeeCall.setVideoCall(true);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                if(signalingState == StringeeCall.SignalingState.ANSWERED){
                    motionLayout.setTransition(R.id.start, R.id.end);
                    motionLayout.transitionToEnd();
                    usercamera.setOnTouchListener(new View.OnTouchListener()  {
                        float dX, dY;
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_CANCEL: {
                                    Log.e(DEBUG_TAG, "CANCEL");
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
                                    Log.e(DEBUG_TAG,"dY:" + dY +  "height : " + view.getHeight() + " " + "y : " + view.getY() + " " +"rawy : " +  event.getRawY()  + " "+motionLayout.getHeight());
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
                    cancel.setVisibility(View.VISIBLE);
                }else if(signalingState == StringeeCall.SignalingState.ENDED ) {
                    finish();
                }else if(signalingState == StringeeCall.SignalingState.BUSY ) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}