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

/**
 * Timeline / feed fragment with very defensive view lookups and detailed logging.
 * We scope child lookups under the included error card to avoid nulls from <include>.
 */
public class PostFragment extends Fragment {

    private static final String TAG = "PostFragment";

    private RecyclerView rvPost;
    protected PostAdapter adapter;
    protected final List<PostUi> allPosts = new ArrayList<>();

    private ShimmerFrameLayout shimmer;
    private View errorCard;          // root of the included error layout
    private TextView tvErrorMessage; // found under errorCard
    private Button btnRetry;         // found under errorCard

    public PostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() inflating fragment_post");
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        Log.d(TAG, "onCreateView() inflate OK, view=" + v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated() start");
        super.onViewCreated(view, savedInstanceState);

        // --- find views ---
        rvPost = view.findViewById(R.id.rvPost);
        shimmer = view.findViewById(R.id.shimmerContainer);

        // Included error card root (android:id set on <include> tag)
        errorCard = view.findViewById(R.id.inlineError);
        if (errorCard == null) {
            Log.e(TAG, "onViewCreated(): errorCard is NULL (inlineError not found). " +
                    "Check fragment_post.xml <include> has android:id='@id/inlineError'.");
        } else {
            // Scope lookups to the included root to avoid accidental nulls
            tvErrorMessage = errorCard.findViewById(R.id.tvErrorMessage);
            btnRetry = errorCard.findViewById(R.id.btnRetry);
            Log.d(TAG, "onViewCreated(): errorCard=" + idName(errorCard) +
                    " tvErrorMessage=" + idName(tvErrorMessage) +
                    " btnRetry=" + idName(btnRetry));
        }

        // --- recycler setup ---
        adapter = new PostAdapter(requireContext(), allPosts);
        rvPost.setAdapter(adapter);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPost.setHasFixedSize(true);
        Log.d(TAG, "onViewCreated(): RecyclerView + adapter wired, items=" + allPosts.size());

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                Log.d(TAG, "Retry tapped");
                queryPosts();
            });
        } else {
            Log.w(TAG, "onViewCreated(): btnRetry null; retry won’t work");
        }

        // Initial state
        showError(false, null);
        showLoading(true);
        queryPosts();
        Log.d(TAG, "onViewCreated() end (query issued)");
    }

    private void showLoading(boolean show) {
        Log.d(TAG, "showLoading(" + show + ")");
        if (shimmer == null || rvPost == null) {
            Log.w(TAG, "showLoading(): shimmer or rvPost null; shimmer=" + (shimmer != null) + " rv=" + (rvPost != null));
            return;
        }
        try {
            if (show) {
                shimmer.setVisibility(View.VISIBLE);
                shimmer.startShimmer();
                rvPost.setAlpha(0f);
                Log.d(TAG, "showLoading(): shimmer started");
            } else {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                rvPost.setAlpha(1f);
                Log.d(TAG, "showLoading(): shimmer stopped and hidden");
            }
        } catch (Throwable t) {
            Log.e(TAG, "showLoading(): error toggling shimmer", t);
        }
    }

    private void showError(boolean show, @Nullable String message) {
        Log.d(TAG, "showError(show=" + show + ", msg=" + (message == null ? "<null>" : message) + ")");
        try {
            if (errorCard != null) {
                errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
                Log.d(TAG, "showError(): errorCard visibility set to " + (show ? "VISIBLE" : "GONE"));
            } else {
                Log.w(TAG, "showError(): errorCard null (cannot change visibility)");
            }

            if (show) {
                if (tvErrorMessage != null) {
                    tvErrorMessage.setText(message != null ? message : getString(R.string.error_load_feed));
                    Log.d(TAG, "showError(): tvErrorMessage updated");
                } else {
                    Log.e(TAG, "showError(): tvErrorMessage is NULL — cannot set text");
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "showError(): unexpected error", t);
        }
    }

    protected void queryPosts() {
        Log.d(TAG, "queryPosts(): begin; MOCK_MODE=" + BuildConfig.MOCK_MODE);
        showError(false, null);
        showLoading(true);

        if (BuildConfig.MOCK_MODE) {
            Log.d(TAG, "queryPosts(): using MockData");
            allPosts.clear();
            allPosts.addAll(MockData.mockPosts());
            Log.d(TAG, "queryPosts(): mock items loaded count=" + allPosts.size());
            adapter.notifyDataSetChanged();
            showLoading(false);
            return;
        }

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addAscendingOrder(Post.KEY_CREATED_AT);
        Log.d(TAG, "queryPosts(): dispatching Parse query; limit=20 sort=createdAt ASC");

        query.findInBackground(new FindCallback<Post>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void done(List<Post> posts, ParseException e) {
                Log.d(TAG, "queryPosts(): callback; e=" + e + " postsCount=" + (posts == null ? -1 : posts.size()));
                showLoading(false);
                if (e != null) {
                    Log.e(TAG, "queryPosts(): Parse error", e);
                    showError(true, getString(R.string.error_load_feed));
                    return;
                }
                allPosts.clear();
                if (posts != null) {
                    for (Post p : posts) {
                        PostUi ui = mapToUi(p);
                        allPosts.add(ui);
                    }
                }
                Log.d(TAG, "queryPosts(): mapped UI items=" + allPosts.size());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private PostUi mapToUi(Post p) {
        if (p == null) {
            Log.w(TAG, "mapToUi(): post null; returning empty");
            return new PostUi("", "", "", "", "", "", "");
        }
        String cat = safe(p.getCategory());
        String desc = safe(p.getDescription());
        String title = safe(p.getContentTitle());
        String rev = safe(p.getReview());

        String contentUrl = "";
        try {
            ParseFile f = p.getContentImage();
            if (f != null && f.getUrl() != null) contentUrl = f.getUrl();
        } catch (Exception ex) {
            Log.w(TAG, "mapToUi(): content image error", ex);
        }

        String username = "";
        String userUrl = "";
        try {
            ParseUser u = p.getUser();
            if (u != null) {
                if (u.getUsername() != null) username = u.getUsername();
                ParseFile avatar = u.getParseFile("image");
                if (avatar != null && avatar.getUrl() != null) userUrl = avatar.getUrl();
            }
        } catch (Exception ex) {
            Log.w(TAG, "mapToUi(): user/avatar error", ex);
        }

        Log.d(TAG, "mapToUi(): title=\"" + title + "\" user=\"" + username + "\" img?=" + (!contentUrl.isEmpty()));
        return new PostUi(cat, desc, title, rev, contentUrl, username, userUrl);
    }

    private static String safe(String s) { return s == null ? "" : s; }

    /** Pretty id name for logging (null-safe). */
    private String idName(View v) {
        if (v == null) return "null";
        try {
            int id = v.getId();
            if (id == View.NO_ID) return v.getClass().getSimpleName() + "(no-id)";
            return v.getClass().getSimpleName() + "(@" + getResources().getResourceEntryName(id) + ")";
        } catch (Exception ignore) {
            return v.getClass().getSimpleName() + "(id?)";
        }
    }
}
