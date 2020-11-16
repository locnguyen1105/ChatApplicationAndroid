package com.example.chatapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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

    StorageReference storageReference;
    private static final int IMAGE_CAMERA_REQUEST = 1;
    private static final int IMAGE_GALLERY_REQUEST = 2;
    private Uri imageUri;
    private StorageTask storageTask;
    String currentPath;
    Bitmap thumbnail;

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
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

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
        //change image
        _editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //click edit
        _editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);

            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void Upload() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            if (!storageReference.child(firebaseAuth.getUid()).equals(firebaseAuth.getUid())) {
                storageReference = storageReference.child(firebaseAuth.getUid());
            }
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Image", mUri);
                        databaseReference.updateChildren(hashMap);
                        pd.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No Image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, IMAGE_CAMERA_REQUEST);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                if (requestCode == IMAGE_GALLERY_REQUEST) {
                    imageUri = data.getData();
                    if (storageTask != null && storageTask.isInProgress()) {
                        Toast.makeText(getActivity(), "Upload in process!", Toast.LENGTH_SHORT).show();
                    } else {
                        Upload();
                    }
                } else if (requestCode == IMAGE_CAMERA_REQUEST) {
                    thumbnail = (Bitmap) data.getExtras().get("data");
                    handleUpload(thumbnail);

                  /*  imageUri=data.getData();
                    Upload();*/


                }
            }

        }


    }

    private void handleUpload(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] s = bytes.toByteArray();

        if (!storageReference.child(firebaseAuth.getUid()).equals(firebaseAuth.getUid())) {
            storageReference = storageReference.child(firebaseAuth.getUid());
        }

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageTask = fileReference.putBytes(s);
        storageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}