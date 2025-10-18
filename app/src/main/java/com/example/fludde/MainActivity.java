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
import com.example.fludde.utils.FragmentTransitions;
import com.example.fludde.utils.InsetsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Edge-to-edge with proper insets so content doesn't jump or hide
        InsetsUtils.applyEdgeToEdge(this, findViewById(R.id.flContainer), bottomNavigation);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment frag;
            int id = item.getItemId();
            if (id == R.id.action_home) {
                frag = new HomeFragment();
            } else if (id == R.id.action_feed) {
                frag = new PostFragment();
            } else if (id == R.id.action_compose) {
                frag = new ComposeParentFragment();
            } else if (id == R.id.action_search) {
                frag = new SearchFragment();
            } else {
                frag = new ProfileFragment();
            }
            // Subtle fast transition between tabs
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                            R.anim.fade_in_fast, R.anim.fade_out_fast,
                            R.anim.fade_in_fast, R.anim.fade_out_fast)
                    .replace(R.id.flContainer, frag)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.action_home);
        }
    }
}
