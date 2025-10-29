package com.example.fludde.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.fludde.BuildConfig;
import com.example.fludde.LoginActivity;
import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;
import com.example.fludde.model.PostUi;
import com.example.fludde.utils.MockData;
import com.example.fludde.utils.MockSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** 
 * Profile with enhanced mock-mode support.
 * Shows user profile data and their posts.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvEmail;
    private MaterialButton btnEditProfile;
    private MaterialButton btnLogout;

    private LinearLayout emptyState;
    private MaterialButton btnCreateFirstPost;

    private RecyclerView rvPosts;
    private final List<PostUi> posts = new ArrayList<>();
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        ivAvatar = v.findViewById(R.id.ivAvatar);
        tvUsername = v.findViewById(R.id.tvUsername);
        tvEmail = v.findViewById(R.id.tvEmail);
        btnEditProfile = v.findViewById(R.id.btnEditProfile);
        btnLogout = v.findViewById(R.id.btnLogout);
        emptyState = v.findViewById(R.id.emptyState);
        btnCreateFirstPost = v.findViewById(R.id.btnCreateFirstPost);
        rvPosts = v.findViewById(R.id.rvProfilePosts);

        adapter = new PostAdapter(requireContext(), posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setAdapter(adapter);

        loadUserProfile();
        wireActions();
    }

    /**
     * Load user profile - checks mock mode first.
     */
    private void loadUserProfile() {
        // ✅ NEW: Enhanced mock mode with MockSessionManager
        if (BuildConfig.MOCK_MODE) {
            displayMockProfile();
            return;
        }
        
        // EXISTING: Real Parse query (unchanged)
        bindRealUserProfile();
    }

    /**
     * ✅ NEW METHOD: Display mock profile from session.
     */
    private void displayMockProfile() {
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(requireContext());
        
        if (profile == null) {
            Log.w(TAG, "No mock profile found - user may not be logged in");
            // Show generic fallback
            tvUsername.setText("demo");
            tvEmail.setText("demo@example.com");
            loadDefaultAvatar();
            return;
        }
        
        // Display profile data
        tvUsername.setText(profile.username);
        tvEmail.setText(profile.email);
        
        // Load profile picture
        Glide.with(this)
                .load(profile.profilePictureUrl)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(ivAvatar);
        
        // Display user's posts
        posts.clear();
        if (profile.userPosts != null && !profile.userPosts.isEmpty()) {
            posts.addAll(profile.userPosts);
            rvPosts.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        } else {
            rvPosts.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        
        Log.d(TAG, "Mock profile displayed for: " + profile.username + " with " + posts.size() + " posts");
    }

    /**
     * Bind real user profile from Parse.
     */
    private void bindRealUserProfile() {
        try {
            ParseUser current = ParseUser.getCurrentUser();
            String username = current != null && current.getUsername() != null ? current.getUsername() : "";
            String email = current != null && current.getEmail() != null ? current.getEmail() : "";

            tvUsername.setText(username);
            tvEmail.setText(email);

            ParseFile avatar = current != null ? current.getParseFile("image") : null;
            Glide.with(this)
                    .load(avatar != null ? avatar.getUrl() : null)
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(ivAvatar);

            Log.d(TAG, "Real user profile bound");
            
            // In real mode, show empty state until posts are implemented
            rvPosts.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to bind user profile", e);
            Toast.makeText(getContext(), getString(R.string.error_load_profile), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load default avatar when no profile found.
     */
    private void loadDefaultAvatar() {
        Glide.with(this)
                .load(R.drawable.placeholder_avatar)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(ivAvatar);
    }

    /**
     * Wire up button actions.
     */
    private void wireActions() {
        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(requireContext(), getString(R.string.action_edit_profile), Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            // ✅ UPDATED: Handle mock mode logout with MockSessionManager
            if (BuildConfig.MOCK_MODE) {
                MockSessionManager.logout(requireContext());
                Log.d(TAG, "Mock user logged out");
                navigateToLogin();
                return;
            }
            
            // EXISTING: Real Parse logout
            try {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Logout failed", e);
                            Toast.makeText(requireContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        navigateToLogin();
                    }
                });
            } catch (Exception ex) {
                Log.e(TAG, "Logout error", ex);
                Toast.makeText(requireContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateFirstPost.setOnClickListener(v ->
                Toast.makeText(requireContext(), getString(R.string.nav_compose), Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigate to login screen.
     */
    private void navigateToLogin() {
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}