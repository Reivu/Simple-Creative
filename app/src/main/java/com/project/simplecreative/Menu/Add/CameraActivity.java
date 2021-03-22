package com.project.simplecreative.Menu.Add;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.simplecreative.MainActivity;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.ImageManager;
import com.project.simplecreative.Utils.StringManipulation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("Duplicates")
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";

    private static final int CAMERA_REQUEST = 1;
    private String FIREBASE_IMAGE_STORAGE = "photos/user/";
    private String imgUrl;
    private String UserID;
    private int image_count = 0;
    private double uploadProgress = 0;

    private ImageView photo, close;
    private TextView done;
    private EditText title, caption;
    private FloatingActionButton add;
    private Bitmap bitmap;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        photo = findViewById(R.id.addShow);
        title = findViewById(R.id.addTitle);
        caption = findViewById(R.id.addDescription);
        add = findViewById(R.id.addImage);
        close = findViewById(R.id.addClose);
        done = findViewById(R.id.addShare);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Check User Login
        if (firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        imageCount();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = title.getText().toString();
                String c = caption.getText().toString();
                addNewPhoto(t, c, image_count, imgUrl);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: Get File");

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Uri selectedImageUri = data.getData();

            File imageFile = new File(getPathFromURI(selectedImageUri));
            imgUrl = imageFile.toString();

            //imageView.setImageBitmap(photo);
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap)
                    .into(photo);
        }
    }

    //Get the real path from the URI
    private String getPathFromURI(Uri contentUri) {

        String result;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }

    //Photo Numbering
    private void imageCount(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image_count = getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: Image Count " + image_count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot dataSnapshot1: dataSnapshot.child(getString(R.string.firebase_user_photos)).child(UserID).getChildren()){
            count++;
        }
        return count;
    }

    //Upload
    private void addNewPhoto(final String t, final String caption, int count, final String imgUrl){

        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference sr = storageReference.child(FIREBASE_IMAGE_STORAGE + "/" + UserID + "/photo" + (count + 1));

        if(bitmap == null){
            bitmap = ImageManager.getBitmap(imgUrl);
        }

        //Compress
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask;
        uploadTask = sr.putBytes(bytes);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if(progress - 15 > uploadProgress){
                    Toast.makeText(CameraActivity.this, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    uploadProgress = progress;
                }

                Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
            }
        });

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return sr.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addPhotoToDatabase(t, caption, downloadUri.toString());
                    Toast.makeText(CameraActivity.this, "photo upload success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CameraActivity.this, MainActivity.class));
                } else {
                    Log.e(TAG, "onFailure: Photo upload failed. " + task.getException().getMessage());
                    Toast.makeText(CameraActivity.this, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPhotoToDatabase(String t, String caption, String downloadUri) {

        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = databaseReference.child(getString(R.string.firebase_photos)).push().getKey();
        PhotoModel photo = new PhotoModel();

        photo.setTitle(t);
        photo.setCaption(caption);
        photo.setDate(getTimestamp());
        photo.setPath(downloadUri);
        photo.setTags(tags);
        photo.setUser_id(UserID);
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        databaseReference.child(getString(R.string.firebase_user_photos)).child(UserID).child(newPhotoKey).setValue(photo);
        databaseReference.child(getString(R.string.firebase_photos)).child(newPhotoKey).setValue(photo);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        return sdf.format(new Date());
    }

}
