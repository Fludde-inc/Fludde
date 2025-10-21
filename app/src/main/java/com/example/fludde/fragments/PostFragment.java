package com.example.fludde.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fludde.BuildConfig;
import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;
import com.example.fludde.model.PostUi;
import com.example.fludde.utils.MockData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/** Timeline / feed (always shows data in MOCK_MODE). */
public class PostFragment extends Fragment {

    private RecyclerView rvPost;
    private PostAdapter adapter;
    private final List<PostUi> allPosts = new ArrayList<>();

    private ShimmerFrameLayout shimmer;
    private View errorCard;
    private TextView tvErrorMessage;
    private Button btnRetry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPost = view.findViewById(R.id.rvPost);
        shimmer = view.findViewById(R.id.shimmerContainer);

        errorCard = view.findViewById(R.id.inlineError);
        tvErrorMessage = errorCard != null ? errorCard.findViewById(R.id.tvErrorMessage) : null;
        btnRetry = errorCard != null ? errorCard.findViewById(R.id.btnRetry) : null;

        adapter = new PostAdapter(requireContext(), allPosts);
        rvPost.setAdapter(adapter);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPost.setHasFixedSize(true);

        if (btnRetry != null) btnRetry.setOnClickListener(v -> queryPosts());

        showError(false, null);
        showLoading(true);
        queryPosts();
    }

    private void showLoading(boolean show) {
        if (shimmer == null || rvPost == null) return;
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

    private void showError(boolean show, @Nullable String msg) {
        if (errorCard != null) errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show && tvErrorMessage != null) {
            tvErrorMessage.setText(msg == null ? getString(R.string.error_generic) : msg);
        }
    }

    private void bindMock() {
        allPosts.clear();
        allPosts.addAll(MockData.mockPosts());
        adapter.notifyDataSetChanged();
    }

    private void queryPosts() {
        // In mock mode, just bind canned data.
        if (BuildConfig.MOCK_MODE) {
            bindMock();
            showLoading(false);
            showError(false, null);
            return;
        }

        // Real backend path (Parse). If your backend isn't ready, you'll still see mock in debug.
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(50);
        query.orderByDescending(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                showLoading(false);
                if (e != null) {
                    showError(true, getString(R.string.error_generic));
                    return;
                }
                allPosts.clear();
                if (posts != null) {
                    for (Post p : posts) {
                        String category = nz(p.getString(Post.KEY_CATEGORY));
                        String desc = nz(p.getString(Post.KEY_DESCRIPTION));
                        String title = nz(p.getString(Post.KEY_CONTENT_TITLE));
                        String review = nz(p.getString(Post.KEY_REVIEW));

                        String contentUrl = "";
                        try {
                            ParseFile f = p.getParseFile(Post.KEY_CONTENT_IMAGE);
                            if (f != null) contentUrl = nz(f.getUrl());
                        } catch (Exception ignore) {}

                        String userName = "";
                        String userImageUrl = "";
                        try {
                            ParseUser u = p.getParseUser(Post.KEY_USER);
                            if (u != null) {
                                userName = nz(u.getUsername());
                                ParseFile uf = u.getParseFile("image");
                                if (uf != null) userImageUrl = nz(uf.getUrl());
                            }
                        } catch (Exception ignore) {}

                        allPosts.add(new PostUi(category, desc, title, review, contentUrl, userName, userImageUrl));
                    }
                }
                if (allPosts.isEmpty()) {
                    // graceful empty state
                    showError(true, getString(R.string.empty_profile_message));
                } else {
                    showError(false, null);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
