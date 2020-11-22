package com.example.chatapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapplication.Stringee.GenAccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.listener.StringeeConnectionListener;
import com.stringee.messaging.Message;
import com.stringee.messaging.StringeeChange;
import com.stringee.messaging.StringeeObject;
import com.stringee.messaging.listeners.ChangeEventListenter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  {
    private static final String apikey = "SKT1mOSpmkNB1HOyLz1U7eeLBCSMvMpFYd";
    private static final String secret_key = "cFExQjI0YWIzVnZRWXNlalRHYXRobFFqQW1MV3E5eTA=";

    public static StringeeClient stringeeClient;
    public static Map<String,StringeeCall> callMap = new HashMap<>();
    private String firebaseToken;

    private String token ;
    Button _logout;
    TextView _user, _email, _profilename;
    CircleImageView _profileimage;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        Query query = databaseReference.orderByChild("Email").equalTo(firebaseUser.getEmail());

        token = GenAccessToken.genAccessToken(apikey,secret_key,36000,firebaseUser.getEmail());
        initStringee();
        requirePermission();

        BottomNavigationView navigationView = findViewById(R.id.bottomnav);
        navigationView.setOnNavigationItemSelectedListener(selectListioner);

        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, chatFragment, "");
        ft1.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectListioner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_chat:
                            getSupportActionBar().setTitle("Chat");
                            ChatFragment chatFragment = new ChatFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, chatFragment, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            getSupportActionBar().setTitle("Profile");
                            ProfileFragment profileFragment = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, profileFragment, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_friend:
                            getSupportActionBar().setTitle("Users");
                            UsersFragment usersFragment = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, usersFragment, "");
                            ft3.commit();
                            return true;

                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.logout: {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            case R.id.create_group:{
                return true;
            }
        }
        return false;
    }

    private void requirePermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        },1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(MainActivity.this,"Permission denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initStringee() {
        stringeeClient = new StringeeClient(MainActivity.this);
        stringeeClient.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        firebaseToken = FirebaseInstanceId.getInstance().getToken();
                        Log.e("REGISTER TOKEN",firebaseToken);
                        Log.e("StringeeClient",stringeeClient.getUserId());

                        stringeeClient.registerPushToken(firebaseToken, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                Log.e("REGISTER TOKEN",firebaseToken);
                                Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(StringeeError stringeeError) {
                                Toast.makeText(MainActivity.this, "Register error: " + stringeeError.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(int i) {
                                super.onProgress(i);
                            }
                        });

                    }
                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {

            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callMap.put(stringeeCall.getCallId(),stringeeCall);
                        if(stringeeCall.isVideoCall()) {
                            Intent intent = new Intent(MainActivity.this, CallingActivity.class);
                            intent.putExtra("call_id", stringeeCall.getCallId());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this, AnswerPhoneCalling.class);
                            intent.putExtra("call_id", stringeeCall.getCallId());
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {

            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
        stringeeClient.setChangeEventListenter(new ChangeEventListenter() {
            @Override
            public void onChangeEvent(StringeeChange stringeeChange) {
                if (stringeeChange.getObjectType() == Message.Type.MESSAGE){
                        Log.e("CHANGE ORCUR","STRINGEE");
                }
            }
        });
        stringeeClient.connect(token);
    }

}