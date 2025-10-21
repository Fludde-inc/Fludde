package com.example.fludde;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.fludde.fragments.ComposeParentFragment;
import com.example.fludde.fragments.HomeFragment;
import com.example.fludde.fragments.PostFragment;
import com.example.fludde.fragments.ProfileFragment;
import com.example.fludde.fragments.SearchFragment;
import com.example.fludde.utils.InsetsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        InsetsUtils.applyEdgeToEdge(this, findViewById(R.id.flContainer), bottomNavigation);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();
            if (id == R.id.action_home) {
                f = new HomeFragment();                         // Home
            } else if (id == R.id.action_feed) {
                f = new PostFragment();                          // Timeline
            } else if (id == R.id.action_compose) {
                f = new ComposeParentFragment();                 // Compose
            } else if (id == R.id.action_search) {
                f = new SearchFragment();                        // Search
            } else { // R.id.action_profile
                f = new ProfileFragment();                       // Profile
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, f)
                    .commit();
            return true;
        });

        // Default tab
        bottomNavigation.setSelectedItemId(R.id.action_home);
    }
}
