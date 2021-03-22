package com.project.simplecreative.Menu.Add;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("ALL")
public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    private static final int SELECT_PICTURE = 100;
    private String FIREBASE_IMAGE_STORAGE = "photos/user/";
    private String UserID;
    private double cUploadProgress = 0;
    private int image_count = 0;
    private String imgUrl;

    private FirebaseAuth cFirebaseAuth;
    private DatabaseReference cDatabaseReference;
    private FirebaseStorage cFirebaseStorage;
    private FirebaseDatabase cFirebaseDatabase;
    private StorageReference cStorageReference;

    private FloatingActionButton button;
    private ImageView imageView, close;
    private TextView done;
    private EditText title, description;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        button = findViewById(R.id.addImage);
        close = findViewById(R.id.addClose);
        done = findViewById(R.id.addShare);
        imageView = findViewById(R.id.addShow);
        description = findViewById(R.id.addDescription);
        title = findViewById(R.id.addTitle);

        cFirebaseAuth = FirebaseAuth.getInstance();
        cFirebaseDatabase = FirebaseDatabase.getInstance();
        cDatabaseReference = FirebaseDatabase.getInstance().getReference();
        cFirebaseStorage = FirebaseStorage.getInstance();
        cStorageReference = FirebaseStorage.getInstance().getReference();
        Log.d(TAG, "onDataChange: " + image_count);

        //Check User Login
        if (cFirebaseAuth.getCurrentUser() != null){
            UserID = cFirebaseAuth.getCurrentUser().getUid();
        }

        imageCount();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String head = title.getText().toString();
                String caption = description.getText().toString();
                addNewPhoto(head, caption, image_count, imgUrl);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /* Choose an image from Gallery */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    imgUrl = path;
                    Log.d(TAG, "Image Path : " + path);

                    // Set the image in ImageView
                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImageUri)
                            .into(imageView);
                }
            }
        }
    }

    //Get the real path from the URI
    private String getPathFromURI(Uri contentUri) {

        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    //Photo Numbering
    private void imageCount(){
        cDatabaseReference.addValueEventListener(new ValueEventListener() {
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
    private void addNewPhoto(final String head, final String caption, int count, final String imgUrl){

        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference storageReference = cStorageReference.child(FIREBASE_IMAGE_STORAGE + "/" + UserID + "/photo" + (count + 1));

        if(bitmap == null){
            bitmap = ImageManager.getBitmap(imgUrl);
        }

        //Compress
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if(progress - 15 > cUploadProgress){
                    Toast.makeText(GalleryActivity.this, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    cUploadProgress = progress;
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
                return storageReference.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addPhotoToDatabase(head, caption, downloadUri.toString());
                    Toast.makeText(GalleryActivity.this, "photo upload success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GalleryActivity.this, MainActivity.class));
                } else {
                    Log.e(TAG, "onFailure: Photo upload failed. " + task.getException().getMessage());
                    Toast.makeText(GalleryActivity.this, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addPhotoToDatabase(String head, String caption, String downloadUri) {

        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = cDatabaseReference.child(getString(R.string.firebase_photos)).push().getKey();
        PhotoModel photo = new PhotoModel();

        photo.setTitle(head);
        photo.setCaption(caption);
        photo.setDate(getTimestamp());
        photo.setPath(downloadUri);
        photo.setTags(tags);
        photo.setUser_id(UserID);
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        cDatabaseReference.child(getString(R.string.firebase_user_photos)).child(UserID).child(newPhotoKey).setValue(photo);
        cDatabaseReference.child(getString(R.string.firebase_photos)).child(newPhotoKey).setValue(photo);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        return sdf.format(new Date());
    }
}
