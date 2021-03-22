package com.project.simplecreative.Menu.Profile;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.simplecreative.LoginActivity;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.Model.RetrofitClient;
import com.project.simplecreative.Model.RetrofitInterface;
import com.project.simplecreative.Model.UserModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.RecyclerViewAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private Button setting, signout;
    private TextView display_name, email, description;
    private CircleImageView circleImageView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String UserID;
    private String TokenID = "ZYCBjVCRYx7GwrpvezPgmHzQyEgCnokxbEDVPOLn";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreateView: starting...");

        setting = v.findViewById(R.id.profileSetting);
        signout = v.findViewById(R.id.profileSignout);
        display_name = v.findViewById(R.id.profileDisplayName);
        email = v.findViewById(R.id.profileEmail);
        description = v.findViewById(R.id.profileDescription);
        circleImageView = v.findViewById(R.id.profileImage);
        recyclerView = v.findViewById(R.id.profileRecyclerView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if(firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        setRecyclerView();
        userData();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();

                Toast.makeText(getActivity(), "Log Out!!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    //Get User Data
    private void userData(){

        Log.d(TAG, "userData: starting...");

        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitInterface retrofitAPI = retrofit.create(RetrofitInterface.class);
        Call call = retrofitAPI.listUser(UserID, TokenID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                Log.d(TAG, "onResponse: Success to Retrive Data!!!");

                if (response.body() != null){
                    UserModel userModel = (UserModel) response.body();

                    if (getContext() != null){
                        Glide.with(getContext()).load(userModel.getProfile_photo()).into(circleImageView);
                    }

                    email.setText(userModel.getEmail());
                    display_name.setText(userModel.getDisplay_name());
                    description.setText(userModel.getDescription());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), "Failed to Retrieve Data!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        Log.d(TAG, "setRecyclerView: Setting Up RecyclerView");

        final ArrayList<PhotoModel> photos = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child(getString(R.string.firebase_user_photos)).child(UserID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot snapshot :  dataSnapshot.getChildren()){
                    //photos.add(ds.getValue(PhotoModel.class));
                    PhotoModel photoModel = snapshot.getValue(PhotoModel.class);
                    photos.add(photoModel);
                }

//                ArrayList<String> imgUrls = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    imgUrls.add(photo.getPath());
//                }
//
//                ArrayList<String> caption = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    caption.add(photo.getCaption());
//                }
//
//                ArrayList<String> title = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    title.add(photo.getTitle());
//                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), photos);
                recyclerView.setAdapter(recyclerViewAdapter);

                recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra("photo", photos.get(position));
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }
}
