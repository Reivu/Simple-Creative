package com.project.simplecreative.Menu.Home;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.simplecreative.Menu.Profile.PreviewActivity;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.Heart;
import com.project.simplecreative.Utils.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private GestureDetector gestureDetector;
    private Heart heart;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String UserID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.homeList);
        searchView = v.findViewById(R.id.searchField);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //Check User Login
        if (firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        setRecyclerView();

        setSearchFilter();

        return v;
    }

    private void setSearchFilter() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setRecyclerView() {

        final List<PhotoModel> photos = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child(getString(R.string.firebase_photos));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //photos.add(ds.getValue(PhotoModel.class));
                    PhotoModel photoModel = ds.getValue(PhotoModel.class);
                    photos.add(photoModel);
                }

//                ArrayList<String> img = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    img.add(photo.getPath());
//                }
//
//                ArrayList<String> title = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    title.add(photo.getTitle());
//                }
//
//                ArrayList<String> caption = new ArrayList<>();
//                for (PhotoModel photo : photos) {
//                    caption.add(photo.getCaption());
//                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                homeAdapter = new HomeAdapter(getActivity(), photos);
                recyclerView.setAdapter(homeAdapter);

                homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra("photo", photos.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }
}
