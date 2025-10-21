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

import com.example.fludde.BuildConfig;
import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;
import com.example.fludde.model.PostUi;
import com.example.fludde.utils.MockData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvHomeFeed;
    private PostAdapter adapter;
    private final List<PostUi> posts = new ArrayList<>();

    private ProgressBar progress;
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
        rvHomeFeed.setHasFixedSize(true);

        btnRetry.setOnClickListener(v -> loadFeed());

        showError(false, null);
        showLoading(true);
        loadFeed();
    }

    private void showLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rvHomeFeed != null) rvHomeFeed.setAlpha(show ? 0.3f : 1f);
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

        if (BuildConfig.MOCK_MODE) {
            posts.clear();
            posts.addAll(MockData.mockPosts());
            adapter.notifyDataSetChanged();
            showLoading(false);
            return;
        }

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
                if (result != null) {
                    for (Post p : result) {
                        posts.add(mapToUi(p));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private PostUi mapToUi(Post p) {
        String cat = safe(p.getCategory());
        String desc = safe(p.getDescription());
        String title = safe(p.getContentTitle());
        String rev = safe(p.getReview());

        String contentUrl = "";
        try {
            ParseFile f = p.getContentImage();
            if (f != null && f.getUrl() != null) contentUrl = f.getUrl();
        } catch (Exception ignore) {}

        String username = "";
        String userUrl = "";
        try {
            ParseUser u = p.getUser();
            if (u != null) {
                if (u.getUsername() != null) username = u.getUsername();
                ParseFile avatar = u.getParseFile("image");
                if (avatar != null && avatar.getUrl() != null) userUrl = avatar.getUrl();
            }
        } catch (Exception ignore) {}

        return new PostUi(cat, desc, title, rev, contentUrl, username, userUrl);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
