package com.project.simplecreative.Menu.Profile;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.simplecreative.Model.LikeModel;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.Model.UserModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.Heart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = "PreviewActivity";

    private ImageView imageView, close, red_heart, white_heart;
    private TextView textView, time, likes;
    private GestureDetector gestureDetector;
    private Heart heart;
    private PhotoModel photoModel;
    private UserModel currentUser;
    private StringBuilder user;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private Boolean likedByUser;
    private String likesString = "";
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        close = findViewById(R.id.back);
        red_heart = findViewById(R.id.image_heart_red);
        white_heart = findViewById(R.id.image_heart);
        likes = findViewById(R.id.image_likes);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //Check User Login
        if (firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        gestureDetector = new GestureDetector(PreviewActivity.this, new GestureListener());
        heart = new Heart(red_heart, white_heart);

        try{
            photoModel = getIncomingIntent();
            getCurrentUser();
            //setLikes
            getLikeString();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private PhotoModel getIncomingIntent(){
        Log.d(TAG, "getPhotoFromBundle: arguments: ");

        //IntentData
        PhotoModel photoModel = getIntent().getExtras().getParcelable("photo");

        String image = photoModel.getPath();
        String caption = photoModel.getCaption();
        String date = photoModel.getDate();

        //setPhoto
        imageView = findViewById(R.id.post_image);
        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(imageView);

        //setCaption
        textView = findViewById(R.id.image_caption);
        textView.setText(caption);

        //setTime
        time = findViewById(R.id.image_time_posted);
        String timestampDiff = getTimestampDifference(date);
        if(!timestampDiff.equals("0")){
            time.setText(timestampDiff + " DAYS AGO");
        }else{
            time.setText("TODAY");
        }

        return photoModel;
    }

    private String getTimestampDifference(String date){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;

        try{
            timestamp = sdf.parse(date);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    private void getLikeString(){

        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.firebase_photos))
                .child(photoModel.getPhoto_id())
                .child(getString(R.string.firebase_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = new StringBuilder();

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.firebase_user))
                            .orderByChild(getString(R.string.firebase_user_id))
                            .equalTo(singleSnapshot.getValue(LikeModel.class).getUserID());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: " + singleSnapshot.getValue(UserModel.class).getDisplay_name());

                                user.append(singleSnapshot.getValue(UserModel.class).getDisplay_name());
                                user.append(",");
                            }

                            String[] splitUsers = user.toString().split(",");
                            likes.setVisibility(View.VISIBLE);

                            if(user.toString().contains(currentUser.getDisplay_name())){
                                likedByUser = true;
                            }else{
                                likedByUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1){
                                likesString = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                likesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            }
                            else if(length == 4){
                                likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            }
                            else if(length > 4){
                                likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + likesString);

                            setLikesString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    likesString = "";
                    likedByUser = false;
                    setLikesString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });

    }

    private void setLikesString(){

        likes.setText(likesString);

        if(likedByUser){
            white_heart.setVisibility(View.GONE);
            red_heart.setVisibility(View.VISIBLE);
            red_heart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            white_heart.setVisibility(View.VISIBLE);
            red_heart.setVisibility(View.GONE);
            white_heart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            Log.d(TAG, "onDoubleTap: double tap detected.");

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.firebase_photos))
                    .child(photoModel.getPhoto_id())
                    .child(getString(R.string.firebase_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(likedByUser && singleSnapshot.getValue(LikeModel.class).getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            databaseReference.child(getString(R.string.firebase_photos))
                                    .child(photoModel.getPhoto_id())
                                    .child(getString(R.string.firebase_likes))
                                    .child(keyID)
                                    .removeValue();

                            databaseReference.child(getString(R.string.firebase_user_photos))
                                    .child(photoModel.getUser_id())
                                    .child(photoModel.getPhoto_id())
                                    .child(getString(R.string.firebase_likes))
                                    .child(keyID)
                                    .removeValue();

                            heart.toggle();
                            getLikeString();
                        }
                        //case2: The user has not liked the photo
                        else if(!likedByUser){
                            //add new like
                            addNewLike();
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });

            return true;
        }
    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = databaseReference.push().getKey();
        LikeModel like = new LikeModel();
        like.setUserID(UserID);

        databaseReference.child(getString(R.string.firebase_photos))
                .child(photoModel.getPhoto_id())
                .child(getString(R.string.firebase_likes))
                .child(newLikeID)
                .setValue(like);

        databaseReference.child(getString(R.string.firebase_user_photos))
                .child(photoModel.getUser_id())
                .child(photoModel.getPhoto_id())
                .child(getString(R.string.firebase_likes))
                .child(newLikeID)
                .setValue(like);

        heart.toggle();
        getLikeString();
    }

    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.firebase_user))
                .orderByChild(getString(R.string.firebase_user_id))
                .equalTo(UserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    currentUser = singleSnapshot.getValue(UserModel.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }
}
