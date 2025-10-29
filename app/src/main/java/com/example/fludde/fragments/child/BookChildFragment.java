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

import com.example.fludde.BuildConfig;
import com.example.fludde.R;
import com.example.fludde.adapters.BookChildAdapter;
import com.example.fludde.model.BooksContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.ErrorHandler;
import com.example.fludde.utils.MockData;
import com.example.fludde.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Horizontal books carousel (Google Books). */
public class BookChildFragment extends Fragment {

    private static final String TAG = "BookChildFragment";
    private static final String BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=miami&maxResults=25";

    private RecyclerView rv;
    private ProgressBar progress;

    private final List<BooksContent> items = new ArrayList<>();
    private BookChildAdapter adapter;

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

        // carousel affordance
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

        if (BuildConfig.MOCK_MODE) {
            try {
                JSONObject mock = MockData.googleBooksJson();
                JSONArray arr = mock.optJSONArray("items");
                items.clear();
                if (arr != null) {
                    items.addAll(BooksContent.fromGoogleBooks(arr));
                }
                adapter.notifyDataSetChanged();
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.empty_books_message), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Mock books parse error", e);
                ErrorHandler.showToast(requireContext(), R.string.error_load_content);
            } finally {
                setLoading(false);
            }
            return;
        }

        Log.d(TAG, "Fetching books: " + BOOKS_URL);

        ApiUtils.get(BOOKS_URL, new ApiUtils.Callback() {
            @Override public void onSuccess(String body) {
                try {
                    JSONObject response = new JSONObject(body);
                    JSONArray arr = response.optJSONArray("items");
                    items.clear();
                    if (arr != null) {
                        items.addAll(BooksContent.fromGoogleBooks(arr));
                    }
                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (items.isEmpty()) {
                            ErrorHandler.showToast(requireContext(), R.string.empty_books_message);
                        }
                        setLoading(false);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Parse error (books)", e);
                    requireActivity().runOnUiThread(() -> {
                        ErrorHandler.showToast(requireContext(), R.string.error_load_content);
                        setLoading(false);
                    });
                }
            }

            @Override public void onError(IOException e) {
                Log.e(TAG, "Network error (books)", e);
                requireActivity().runOnUiThread(() -> {
                    ErrorHandler.showNetworkError(requireContext(), R.string.error_load_content);
                    setLoading(false);
                });
            }
        });
    }
}
