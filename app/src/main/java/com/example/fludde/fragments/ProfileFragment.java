package com.example.fludde.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fludde.R;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private TextView tvUsername;
    private TextView tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        try {
            String username = ""; 
            String email = ""; 

            tvUsername.setText(username);
            tvEmail.setText(email);

            Log.d(TAG, "User profile loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load user profile", e);
            Toast.makeText(getContext(), "Failed to load profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
