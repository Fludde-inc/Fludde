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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.fludde.BuildConfig;
import com.example.fludde.LoginActivity;
import com.example.fludde.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Profile screen with mock-mode support.
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

        bindUser();
        wireActions();
    }

    private void bindUser() {
        if (BuildConfig.MOCK_MODE) {
            tvUsername.setText("johndoe2016");
            tvEmail.setText("johndoe@youknow.com");

            Glide.with(this)
                    .load("https://i.pravatar.cc/150?img=12")
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(ivAvatar);
            Log.d(TAG, "Mock profile bound");
            return;
        }

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
            Toast.makeText(requireContext(), getString(R.string.action_edit_profile), Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            if (BuildConfig.MOCK_MODE) {
                // In mock mode, just go to login.
                Intent i = new Intent(requireContext(), LoginActivity.class);
                startActivity(i);
                requireActivity().finish();
                return;
            }

            try {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Logout failed", e);
                            Toast.makeText(requireContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                            return;
                        }
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
            Toast.makeText(requireContext(), getString(R.string.nav_compose), Toast.LENGTH_SHORT).show();
        });
    }
}
