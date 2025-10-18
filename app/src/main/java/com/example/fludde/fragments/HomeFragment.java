package com.example.fludde.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvHomeFeed;
    private PostAdapter adapter;
    private final List<Post> posts = new ArrayList<>();

    private ShimmerFrameLayout shimmer;
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
        shimmer = view.findViewById(R.id.shimmerContainer);

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
        if (show) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
            rvHomeFeed.setAlpha(0f);
        } else {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            rvHomeFeed.setAlpha(1f);
        }
    }

    private void showError(boolean show, @Nullable String message) {
        errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
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
                posts.addAll(result);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
