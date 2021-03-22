package com.project.simplecreative.Menu.Search;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.google.firebase.database.*;
import com.project.simplecreative.Model.UserModel;
import com.project.simplecreative.R;
import com.project.simplecreative.Utils.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private SearchView searchField;
//    private ImageView button;
    private RecyclerView resultList;

    private SearchAdapter searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        searchField = view.findViewById(R.id.searchField);
        resultList = view.findViewById(R.id.searchList);

        setRecyclerView();

        setSearchFilter();

        return view;
    }

    private void setSearchFilter() {
        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setRecyclerView() {
        Log.d(TAG, "setRecyclerView: ");

//        final ArrayList<UserModel> user = new ArrayList<>();
        final List<UserModel> user = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child(getString(R.string.firebase_user));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //user.add(ds.getValue(UserModel.class));
                    UserModel userModel = ds.getValue(UserModel.class);
                    user.add(userModel);
                }

//                ArrayList<String> img = new ArrayList<>();
//                for (UserModel userModel : user) {
//                    img.add(userModel.getProfil_photo());
//                }
//
//                ArrayList<String> name = new ArrayList<>();
//                for (UserModel userModel : user) {
//                    name.add(userModel.getDisplay_name());
//                }
//
//                ArrayList<String> desc = new ArrayList<>();
//                for (UserModel userModel : user) {
//                    desc.add(userModel.getDescription());
//                }

                resultList.setLayoutManager(new LinearLayoutManager(getActivity()));
                resultList.setHasFixedSize(true);
//                searchAdapter = new SearchAdapter(getActivity(), img, name, desc);
                searchAdapter = new SearchAdapter(getActivity(), user);
                resultList.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });

    }


}
