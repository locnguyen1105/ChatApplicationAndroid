package com.example.chatapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    private static final int IMAGE_COVER_CAMERA_REQUEST = 3;
    private static final int IMAGE_COVER_GALLERY_REQUEST = 4;
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
                    try {
                        Picasso.get().load(ds.child("CoverImage").getValue().toString()).into(_coverImage);
                    } catch (Exception e) {
                        Picasso.get().load(R.color.bgCover).into(_coverImage);
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
                selectImage("image");
            }
        });
        //edit_cover
        _editCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("coverimage");
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

    /*private void Upload(String type) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading..");
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
                        hashMap.put(type, mUri);
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
    }*/

    private void selectImage(String type) {
        final CharSequence[] options = {"Camera", "Gallery", "Delete Image", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Temp");
                    imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if (type.equals("image")) {
                        startActivityForResult(takePictureIntent, IMAGE_CAMERA_REQUEST);
                    } else {
                        startActivityForResult(takePictureIntent, IMAGE_COVER_CAMERA_REQUEST);
                    }

                } else if (options[item].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (type.equals("image")) {
                        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                    } else {
                        startActivityForResult(intent, IMAGE_COVER_GALLERY_REQUEST);
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                } else if (options[item].equals("Delete Image")) {

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
                imageUri = data.getData();
                if (requestCode == IMAGE_GALLERY_REQUEST) {
                    UploadImage("Image");
                } else if (requestCode == IMAGE_COVER_GALLERY_REQUEST) {
                    UploadImage("CoverImage");
                } else if (requestCode == IMAGE_CAMERA_REQUEST) {
                    UploadImage("Image");
                } else if (requestCode == IMAGE_COVER_CAMERA_REQUEST) {
                    UploadImage("CoverImage");
                }
            }

        }
    }

    //demo
    private void UploadImage(String type) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image");
        pd.show();
        if (imageUri != null) {
            if (!storageReference.child(firebaseAuth.getUid()).equals(firebaseAuth.getUid())) {
                storageReference = storageReference.child(firebaseAuth.getUid());
            }
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downLoad = uriTask.getResult();
                    if (uriTask.isSuccessful()) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(type, downLoad.toString());
                        databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });

                    } else {
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Progress: " + (int) progressPercent + "%");
                }
            });

        }
    }

}