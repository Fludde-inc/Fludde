package com.example.fludde.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fludde.Post;
import com.example.fludde.adapters.PostAdapter;
import com.example.fludde.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {
    private RecyclerView rvPost;
    private static final String TAG = "Post";
    protected PostAdapter adapter;
    protected List<Post> allPosts;

    private ShimmerFrameLayout shimmer;
    private View errorCard;
    private TextView tvErrorMessage;
    private Button btnRetry;

    public PostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPost = view.findViewById(R.id.rvPost);
        shimmer = view.findViewById(R.id.shimmerContainer);

        errorCard = view.findViewById(R.id.inlineError);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);

        rvPost.setAdapter(adapter);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPost.setHasFixedSize(true);

        btnRetry.setOnClickListener(v -> queryPosts());

        showError(false, null);
        showLoading(true);
        queryPosts();
    }

    private void showLoading(boolean show) {
        if (show) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
            rvPost.setAlpha(0f);
        } else {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            rvPost.setAlpha(1f);
        }
    }

    private void showError(boolean show, @Nullable String message) {
        errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvErrorMessage.setText(message != null ? message : getString(R.string.error_load_feed));
        }
    }

    protected void queryPosts() {
        showError(false, null);
        showLoading(true);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addAscendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void done(List<Post> posts, ParseException e) {
                showLoading(false);
                if (e != null) {
                    Log.e(TAG, "post error", e);
                    showError(true, getString(R.string.error_load_feed));
                    return;
                }
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
