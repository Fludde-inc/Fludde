package com.example.fludde.fragments.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Books tab for Compose:
 * - Horizontal carousel list with stable item sizing from dimens.
 * - Only data fetching + adapter hookup; card look from layout/styles XML.
 */
public class BookChildFragment extends Fragment {

    // Public Google Books query with friendly defaults (no key required).
    private static final String BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=subject:fiction&maxResults=20";

    private RecyclerView rv;
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
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new BookChildAdapter(requireContext(), items, new BookChildAdapter.OnBookContentListener() {
            @Override public void onBookContentClick(int position) { /* no-op for now */ }
            @Override public void onBookContentLongClick(int position) { /* no-op for now */ }
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

    private void fetchBooks() {
        ApiUtils.get(BOOKS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.optJSONArray("items");
                    if (results != null) {
                        items.clear();
                        items.addAll(BooksContent.fromJsonArray(results));
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception ignore) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ApiUtils.handleFailure(statusCode, throwable);
            }
        });
    }
}
