package com.example.fludde.fragments.child;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.adapters.BookChildAdapter;
import com.example.fludde.utils.ApiUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BookChildFragment extends Fragment {
    private static final String TAG = "BookChildFragment";
    private RecyclerView rvBookHorizontalView;
    private BookChildAdapter adapter;

    private static final String NY_TIMES_API_KEY = BuildConfig.NY_TIMES_API_KEY;
    private static final String NYTIMES_URL = "https://api.nytimes.com/svc/books/v3/lists/best-sellers/history.json?api-key=" + NY_TIMES_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_book, container, false);

        rvBookHorizontalView = view.findViewById(R.id.rvBookHorizontalView);
        adapter = new BookChildAdapter(getContext());
        rvBookHorizontalView.setAdapter(adapter);

        fetchPopularBooks();

        return view;
    }

    private void fetchPopularBooks() {
        ApiUtils.get(NYTIMES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    adapter.setBooks(results);
                    Log.d(TAG, "Books fetched successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse book data", e);
                    Toast.makeText(getContext(), "Failed to load books. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to fetch books from API", throwable);
                ApiUtils.handleFailure(statusCode, throwable);
                Toast.makeText(getContext(), "Failed to fetch books. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
