package com.example.fludde;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    loadFragment(new HomeFragment());
                    Log.d(TAG, "Home fragment selected");
                    return true;
                case R.id.nav_profile:
                    loadFragment(new ProfileFragment());
                    Log.d(TAG, "Profile fragment selected");
                    return true;
                default:
                    Log.e(TAG, "Unhandled navigation item selected: " + item.getItemId());
                    return false;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Log.d(TAG, "Fragment loaded successfully: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Failed to load fragment: " + fragment.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
                Log.d(TAG, "Back pressed: navigating to previous fragment");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during back navigation", e);
        }
    }
}
