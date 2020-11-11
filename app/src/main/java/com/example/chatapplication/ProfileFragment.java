package com.example.chatapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private ImageView _coverImage, _editImage;
    private CircleImageView _image;
    private TextView _editCoverImage, _username, _address, _phone, _email;
    private FloatingActionButton _editProfile;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        //init view
        _coverImage = v.findViewById(R.id.cover_image);
        _image = v.findViewById(R.id.image);
        _username = v.findViewById(R.id.username);
        _address = v.findViewById(R.id.address_profile);
        _phone = v.findViewById(R.id.phone_profile);
        _email = v.findViewById(R.id.email_profile);
        //btn edit
        _editImage = v.findViewById(R.id.edit_image);
        _editCoverImage = v.findViewById(R.id.btn_edit_cover_image);
        _editProfile = v.findViewById(R.id.edit_profile);
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.orderByChild("Email").equalTo(firebaseUser.getEmail());
        //get data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    _username.setText(ds.child("Username").getValue().toString());
                    //_coverImage.setImageResource(R.drawable.);
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
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //click edit
        _editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EditProfileFragment editFragment = new EditProfileFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content, editFragment, "");
                ft.commit();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

}