package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapplication.Model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileUserActivity extends AppCompatActivity {
    private ImageView _coverImage, btn_back;
    private CircleImageView _image;
    private TextView _username, _address, _phone, _email;
    private Button btn_add;

    String current_state;

    FirebaseAuth firebaseAuth;
    DatabaseReference mUserDatabase;
    DatabaseReference mFriendReqDatabase;
    DatabaseReference mFriendDatabase;
    FirebaseUser mCurrent_user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_user);
        _coverImage = findViewById(R.id.cover_image);
        _image = findViewById(R.id.image);
        _username = findViewById(R.id.username);
        _address = findViewById(R.id.address_profile);
        _phone = findViewById(R.id.phone_profile);
        _email = findViewById(R.id.email_profile);
        btn_add = findViewById(R.id.btn_sendrequest);
        btn_back = findViewById(R.id.back_btn);
        //get data inten
        Intent i = getIntent();
        Account account = (Account) i.getSerializableExtra("account");
        current_state = "not_friends";

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(account.getUid());
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                _username.setText(ds.child("Username").getValue().toString());
                _email.setText(ds.child("Email").getValue().toString());
                if (TextUtils.isEmpty(ds.child("Address").getValue().toString())) {
                    _address.setText("Cập nhật");
                } else {
                    _address.setText(ds.child("Address").getValue().toString());
                }

                if (TextUtils.isEmpty(ds.child("Phone").getValue().toString())) {
                    _phone.setText("Cập nhật");
                } else {
                    _phone.setText(ds.child("Phone").getValue().toString());
                }
                try {
                    Picasso.get().load(ds.child("Image").getValue().toString()).into(_image);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.image_default).into(_image);
                }
                try {
                    Picasso.get().load(ds.child("CoverImage").getValue().toString()).into(_coverImage);
                } catch (Exception e) {
                    Picasso.get().load(R.color.bgCover).into(_coverImage);
                }

                //accept friend
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(account.getUid())) {
                            String req_type = snapshot.child(account.getUid()).child("Request_type").getValue().toString();
                            if (req_type.equals("Received")) {
                                current_state = "req_received";
                                Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_btn_color);
                                btn_add.setBackground(image);
                                btn_add.setText("Accept Friend Request");
                            } else if (req_type.equals("Sent")) {
                                current_state = "req_sent";
                                Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_btn_color);
                                btn_add.setBackground(image);
                                btn_add.setText("Cancel Friend Request");
                            } else if (req_type.equals("Matches")) {
                                current_state = "friends";
                                Drawable image = (Drawable) getResources().getDrawable(R.drawable.border);
                                btn_add.setBackground(image);
                                btn_add.setText("Unfriend");
                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //send
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_add.setEnabled(false);

                if (current_state.equals("not_friends")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(account.getUid()).child("Request_type").setValue("Sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mFriendReqDatabase.child(account.getUid()).child(mCurrent_user.getUid()).child("Request_type").setValue("Received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        current_state = "req_sent";
                                                        btn_add.setEnabled(true);
                                                        btn_add.setText("Cancel Friend Request");
                                                        Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_btn_color);
                                                        btn_add.setBackground(image);
                                                        Toast.makeText(ViewProfileUserActivity.this, " Sent request successfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(ViewProfileUserActivity.this, "Failed sending request!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                if (current_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(account.getUid()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(account.getUid()).child(mCurrent_user.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    btn_add.setEnabled(true);
                                                    current_state = "not_friends";
                                                    Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_btn);
                                                    btn_add.setBackground(image);
                                                    btn_add.setText("Send Friend Request");
                                                    Toast.makeText(ViewProfileUserActivity.this, "Cancel request successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                }
                if (current_state.equals("req_received")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(account.getUid()).child("Request_type").setValue("Matches")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(account.getUid()).child(mCurrent_user.getUid()).child("Request_type").setValue("Matches")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                                    mFriendDatabase.child(mCurrent_user.getUid()).child(account.getUid()).child(account.getUid()).setValue(currentDate)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    mFriendDatabase.child(account.getUid()).child(mCurrent_user.getUid()).child(mCurrent_user.getUid()).setValue(currentDate)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    btn_add.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    Drawable image = (Drawable) getResources().getDrawable(R.drawable.border);
                                                                                    btn_add.setBackground(image);
                                                                                    btn_add.setText("Unfriend");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                }
                if (current_state.equals("friends")) {
                    mFriendDatabase.child(mCurrent_user.getUid()).child(account.getUid()).child(account.getUid()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(account.getUid()).child(mCurrent_user.getUid()).child(mCurrent_user.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(account.getUid()).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendReqDatabase.child(account.getUid()).child(mCurrent_user.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    btn_add.setEnabled(true);
                                                                                    current_state = "not_friends";
                                                                                    Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_btn);
                                                                                    btn_add.setBackground(image);
                                                                                    btn_add.setText("Send Friend Request");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                }

            }
        });
    }
}