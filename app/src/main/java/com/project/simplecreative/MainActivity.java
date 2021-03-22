package com.project.simplecreative;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.project.simplecreative.Menu.Add.CameraActivity;
import com.project.simplecreative.Menu.Add.GalleryActivity;
import com.project.simplecreative.Menu.Home.HomeFragment;
import com.project.simplecreative.Menu.Profile.ProfileFragment;
import com.project.simplecreative.Menu.Search.SearchFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private ImageView add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        add = findViewById(R.id.toolbarAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });

        //Bottom Navigation
        loadFragment(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }

    //Switch Fragment
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.menuHome:
                    fragment = new HomeFragment();
                    break;
                case R.id.menuSearch:
                    fragment = new SearchFragment();
                    break;
                case R.id.menuProfile:
                    fragment = new ProfileFragment();
                    break;

            }
            return loadFragment(fragment);
        }
    };

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    //Request Permission For CAMERA and GALLERY
    private void requestPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {

                    Toast.makeText(getApplicationContext(), "Access Granted!!", Toast.LENGTH_SHORT).show();

                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), add);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_add, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menuCamera:
                                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                                return true;
                            case R.id.menuGalerry:
                                startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
                                return true;
                            default:
                                return false;
                        }
                        }
                    });
                    popupMenu.show();
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(getApplicationContext(), "Need Access!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }

        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Log.e(TAG, "onError: " + error );
                Toast.makeText(getApplicationContext(), "Error, Please Contact Admin", Toast.LENGTH_SHORT).show();
            }
        })
            .onSameThread()
            .check();
    }


}
