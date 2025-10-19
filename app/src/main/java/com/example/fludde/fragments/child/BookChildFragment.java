package com.example.fludde.fragments.child;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.adapters.BookChildAdapter;
import com.example.fludde.model.BooksContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.SpacesItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Books tab carousel with defensive parsing + noisy logs.
 */
public class BookChildFragment extends Fragment {

    private static final String TAG = "BookChildFragment";

    // Public Google Books query with friendly defaults (no key required).
    private static final String BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=subject:fiction&maxResults=20";

    private RecyclerView rv;
    private ProgressBar progress;
    private BookChildAdapter adapter;
    private final List<BooksContent> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rvBookHorizontalView);
        progress = v.findViewById(R.id.progressBar);

        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new BookChildAdapter(requireContext(), items, new BookChildAdapter.OnBookContentListener() {
            @Override public void onBookContentClick(int position) {}
            @Override public void onBookContentLongClick(int position) {}
        });
        rv.setAdapter(adapter);

        // Carousel affordance: peeking edges + per-item snap
        final int itemSpace = getResources().getDimensionPixelSize(R.dimen.space_12);
        final int edgePeek = getResources().getDimensionPixelSize(R.dimen.space_24);
        rv.setClipToPadding(false);
        rv.setPadding(edgePeek, 0, edgePeek, 0);
        rv.addItemDecoration(new SpacesItemDecoration(itemSpace));
        new LinearSnapHelper().attachToRecyclerView(rv);

        fetchBooks();
    }

    private void setLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rv != null) rv.setAlpha(show ? 0.3f : 1f);
    }

    private void fetchBooks() {
        setLoading(true);
        Log.d(TAG, "Fetching books: " + BOOKS_URL);

        ApiUtils.get(BOOKS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray arr = response.optJSONArray("items");
                    items.clear();
                    items.addAll(BooksContent.fromGoogleBooks(arr));
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Books loaded ✓ count=" + items.size());
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.empty_books_message), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse error (books)", e);
                    Toast.makeText(requireContext(), getString(R.string.error_load_content), Toast.LENGTH_SHORT).show();
                } finally {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                setLoading(false);
                ApiUtils.handleFailure(statusCode, throwable);
                Toast.makeText(requireContext(), getString(R.string.error_load_content), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
