package com.example.fludde.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// (Posts list is a future enhancement; keeping imports minimal)
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.fludde.LoginActivity;
import com.example.fludde.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Profile screen:
 * - Large header (avatar, username, email)
 * - Secondary actions: Edit Profile (tonal), Logout (outlined)
 * - Empty state prompting the first post
 * - Posts list placeholder (future enhancement; no logic changes now)
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvEmail;
    private MaterialButton btnEditProfile;
    private MaterialButton btnLogout;

    // Empty state
    private LinearLayout emptyState;
    private MaterialButton btnCreateFirstPost;

    // Future list
    // private RecyclerView rvProfilePosts;

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
        // rvProfilePosts = v.findViewById(R.id.rvProfilePosts);

        // Future: set up the posts list
        // rvProfilePosts.setLayoutManager(new LinearLayoutManager(requireContext()));

        bindUser();
        wireActions();
    }

    private void bindUser() {
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

            Log.d(TAG, "User profile bound");
        } catch (Exception e) {
            Log.e(TAG, "Failed to bind user profile", e);
            Toast.makeText(getContext(), getString(R.string.error_load_profile), Toast.LENGTH_SHORT).show();
        }
    }

    private void wireActions() {
        btnEditProfile.setOnClickListener(v -> {
            // Placeholder: future edit UI
            Toast.makeText(requireContext(), getString(R.string.action_edit_profile), Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            try {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Logout failed", e);
                            Toast.makeText(requireContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Navigate to login
                        Intent i = new Intent(requireContext(), LoginActivity.class);
                        startActivity(i);
                        requireActivity().finish();
                    }
                });
            } catch (Exception ex) {
                Log.e(TAG, "Logout error", ex);
                Toast.makeText(requireContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateFirstPost.setOnClickListener(v -> {
            // For now, just switch to the Compose tab if your MainActivity honors bottom nav IDs.
            Toast.makeText(requireContext(), getString(R.string.nav_compose), Toast.LENGTH_SHORT).show();
        });
    }
}
