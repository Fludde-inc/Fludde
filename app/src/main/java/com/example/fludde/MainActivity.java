package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fludde.fragments.ComposeParentFragment;
import com.example.fludde.fragments.HomeFragment;
import com.example.fludde.fragments.PostFragment;
import com.example.fludde.fragments.ProfileFragment;
import com.example.fludde.fragments.SearchFragment;
import com.example.fludde.utils.InsetsUtils;
import com.example.fludde.utils.MockSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

/**
 * Main Activity with session verification.
 * 
 * Features:
 * - Session check on startup
 * - Redirects to login if not authenticated
 * - Bottom navigation for app sections
 * - Supports both mock and real Parse authentication
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 🔒 SESSION CHECK: Verify user is logged in
        if (!isUserLoggedIn()) {
            Log.w(TAG, "No valid session found, redirecting to login");
            redirectToLogin();
            return;
        }
        
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

        // Set default fragment (Home)
        bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    /**
     * 🔒 Check if user is logged in.
     * Handles both mock mode and real Parse authentication.
     * 
     * @return true if user is logged in, false otherwise
     */
    private boolean isUserLoggedIn() {
        if (BuildConfig.MOCK_MODE) {
            // Check mock session
            boolean isLoggedIn = MockSessionManager.isLoggedIn(this);
            if (isLoggedIn) {
                Log.d(TAG, "Valid mock session found for user: " + MockSessionManager.getCurrentUsername(this));
            }
            return isLoggedIn;
        } else {
            // Check Parse session
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "Valid Parse session found for user: " + currentUser.getUsername());
                return true;
            }
            return false;
        }
    }

    /**
     * Redirects to login activity when no valid session is found.
     * Clears the back stack to prevent returning to MainActivity.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}