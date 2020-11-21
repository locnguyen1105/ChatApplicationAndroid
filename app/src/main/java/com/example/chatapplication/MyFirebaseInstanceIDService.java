package com.example.chatapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stringee.listener.StatusListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String CHANEL_ID = "com.example.chatapplication.notification" ;
    private static final CharSequence CHANEL_NAME = "Call notify" ;


    @Override
    public void onNewToken(@NonNull String s) {
        MainActivity.stringeeClient.registerPushToken(s, new StatusListener() {
            @Override
            public void onSuccess() {

            }
        });
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e("aa","aaa");
        if (remoteMessage.getData().size() > 0) {
            Log.d("Stringee", "Message data payload: " + remoteMessage.getData());
            String pushFromStringee = remoteMessage.getData().get("stringeePushNotification");
            if (pushFromStringee != null) { // Receive push notification from Stringee Server
                // Connect to Stringee Server here
                String data = remoteMessage.getData().get("data");
                try {
                    JSONObject jsonObject= new JSONObject(data);
                    Log.e("DEBUG","MESSAGE REMOTE :" + remoteMessage.getData());
                    String callStatus = jsonObject.getString("callStatus");
                    if(callStatus != null && callStatus.equals("started")){
                        showNotification();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        super.onMessageReceived(remoteMessage);
    }

    private void showNotification() {
        NotificationManager notificationManager;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Have a call from AlarME");
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }else {
            Log.e("DEBUG ANDROID","DEBUG");
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("Incoming call from AlarME")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1,notification);
    }
}
