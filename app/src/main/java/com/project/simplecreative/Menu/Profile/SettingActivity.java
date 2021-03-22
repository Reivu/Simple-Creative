package com.project.simplecreative.Menu.Profile;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.project.simplecreative.Model.RetrofitClient;
import com.project.simplecreative.Model.RetrofitInterface;
import com.project.simplecreative.Model.UserModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.ImageManager;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("Duplicates")
public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private static final int SELECT_PICTURE = 100;
    private double uploadProgress = 0;
    private String FIREBASE_IMAGE_STORAGE = "photos/user/";
    private Bitmap bitmap;

    private ImageButton close, done;
    private CircleImageView circleImageView;
    private TextView name, photoText;
    private EditText display_name, website, description, phone_number;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private UserModel userModel;

    private String imgUrl;
    private String UserID;
    private String TokenID = "ZYCBjVCRYx7GwrpvezPgmHzQyEgCnokxbEDVPOLn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        close = findViewById(R.id.settingClose);
        done = findViewById(R.id.settingDone);
        circleImageView = findViewById(R.id.settingProfilePhoto);
        name = findViewById(R.id.settingName);
        display_name = findViewById(R.id.settingDisplayName);
        website = findViewById(R.id.settingWebsite);
        description = findViewById(R.id.settingDescription);
        phone_number = findViewById(R.id.settingPhoneNumber);
        photoText =findViewById(R.id.settingPhoto);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        if(firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ShowData();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
                finish();
            }
        });

        photoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

    }

    private void ShowData() {
        Log.d(TAG, "ShowData: starting...");

        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitInterface retrofitAPI = retrofit.create(RetrofitInterface.class);
        Call call = retrofitAPI.listUser(UserID, TokenID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null){
                    UserModel userModel = (UserModel) response.body();

                    Glide.with(getApplicationContext()).load(userModel.getProfile_photo()).into(circleImageView);

                    name.setText(userModel.getName());
                    display_name.setText(userModel.getDisplay_name());
                    website.setText(userModel.getWebsite());
                    description.setText(userModel.getDescription());
                    phone_number.setText(String.valueOf(userModel.getPhone_number()));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(SettingActivity.this, "Failed to Retrieve Data!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveData() {
        Log.d(TAG, "SaveData: starting...");

        String sDisplayName = display_name.getText().toString();
        String sWebsite = website.getText().toString();
        String sDescription = description.getText().toString();
        long sPhoneNumber = Long.parseLong(phone_number.getText().toString());

        checkDataUnique(sDisplayName);

        if (imgUrl != null){
            addNewPhotoProfile(imgUrl);
        }

        if (sWebsite != null){
            databaseReference.child(getString(R.string.firebase_user))
                    .child(UserID)
                    .child(getString(R.string.firebase_website))
                    .setValue(sWebsite);
        }

        if (sDescription != null){
            databaseReference.child(getString(R.string.firebase_user))
                    .child(UserID)
                    .child(getString(R.string.firebase_description))
                    .setValue(sDescription);
        }

        if (sPhoneNumber != 0){
            databaseReference.child(getString(R.string.firebase_user))
                    .child(UserID)
                    .child(getString(R.string.firebase_phone_Number))
                    .setValue(sPhoneNumber);
        }

    }

    //Choose Photo Profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            .into(circleImageView);
                }
            }
        }
    }

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

    //Check Data Unique
    private void checkDataUnique(final String display_name){
        databaseReference.child(getString(R.string.firebase_user)).orderByChild(getString(R.string.firebase_display_name)).equalTo(display_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange display_name: Success");
                if (dataSnapshot.exists()){
                    Toast.makeText(SettingActivity.this, "Choose Another Display Name", Toast.LENGTH_SHORT).show();
                }else {
                    databaseReference.child(getString(R.string.firebase_user))
                            .child(UserID)
                            .child(getString(R.string.firebase_display_name))
                            .setValue(display_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });
    }

    //Upload Photo
    private void addNewPhotoProfile(String imgUrl){
        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference sr = storageReference.child(FIREBASE_IMAGE_STORAGE + "/" + UserID + "/profile_photo/");

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
                    Toast.makeText(SettingActivity.this, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SettingActivity.this, "photo upload success", Toast.LENGTH_SHORT).show();
                    setProfilePhoto(downloadUri.toString());
                } else {
                    Log.e(TAG, "onFailure: Photo upload failed. " + task.getException().getMessage());
                    Toast.makeText(SettingActivity.this, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Save User Photo
    private void setProfilePhoto(String downloadUri) {
        Log.d(TAG, "setProfilePhoto: " + downloadUri);

        databaseReference.child(getString(R.string.firebase_user))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_profile_photo))
                .setValue(downloadUri);
    }
}
