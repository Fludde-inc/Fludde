package com.example.fludde.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;

import org.json.JSONArray;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView rvHomeFeed;
    private PostAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvHomeFeed = view.findViewById(R.id.rvHomeFeed);
        progressBar = view.findViewById(R.id.progressBar);
        adapter = new PostAdapter(getContext());
        rvHomeFeed.setAdapter(adapter);

        loadHomeFeed();

        return view;
    }

    private void loadHomeFeed() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONArray dummyData = new JSONArray(); 
            adapter.setPosts(dummyData);

            Log.d(TAG, "Home feed loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load home feed", e);
            Toast.makeText(getContext(), "Failed to load feed. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            progressBar.setVisibility(View.GONE);
        }
    }
}
