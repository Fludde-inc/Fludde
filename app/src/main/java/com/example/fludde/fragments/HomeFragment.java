package com.example.fludde.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Log;

import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvHomeFeed;
    private PostAdapter adapter;
    private final List<Post> posts = new ArrayList<>();

    // Use the ProgressBar that already exists in fragment_home.xml
    private ProgressBar progress;

    // Inline error
    private View errorCard;
    private TextView tvErrorMessage;
    private Button btnRetry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvHomeFeed = view.findViewById(R.id.rvHomeFeed);
        progress = view.findViewById(R.id.progress);

        errorCard = view.findViewById(R.id.inlineError);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        adapter = new PostAdapter(requireContext(), posts);
        rvHomeFeed.setAdapter(adapter);
        rvHomeFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHomeFeed.setHasFixedSize(true); // stable item height prevents relayout jank

        btnRetry.setOnClickListener(v -> loadFeed());

        showError(false, null);
        showLoading(true);
        loadFeed();
    }

    private void showLoading(boolean show) {
        if (progress != null) {
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        // Fade list a touch while loading (optional)
        if (rvHomeFeed != null) {
            rvHomeFeed.setAlpha(show ? 0.3f : 1f);
        }
    }

    private void showError(boolean show, @Nullable String message) {
        if (errorCard != null) errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show && tvErrorMessage != null) {
            tvErrorMessage.setText(message != null ? message : getString(R.string.error_load_feed));
        }
    }

    private void loadFeed() {
        showError(false, null);
        showLoading(true);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> result, ParseException e) {
                showLoading(false);
                if (e != null) {
                    Log.e("HomeFragment", "Failed to load posts", e);
                    showError(true, getString(R.string.error_load_feed));
                    return;
                }
                posts.clear();
                if (result != null) posts.addAll(result);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
