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
import com.example.fludde.adapters.MovieChildAdapter;
import com.example.fludde.model.MovieContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.MockData;
import com.example.fludde.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Movies tab carousel with TMDB trending content.
 */
public class MovieChildFragment extends Fragment {

    private static final String TAG = "MovieChildFragment";

    private static final String TMDB_URL = 
            "https://api.themoviedb.org/3/trending/movie/day?language=en-US";

    private RecyclerView rv;
    private ProgressBar progress;
    private MovieChildAdapter adapter;
    private final List<MovieContent> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rvMovieHorizontalView);
        progress = v.findViewById(R.id.progressBar);

        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new MovieChildAdapter(requireContext(), items, new MovieChildAdapter.OnMovieContentListener() {
            @Override public void onMovieContentClick(int position) {}
            @Override public void onMovieContentLongClick(int position) {}
        });
        rv.setAdapter(adapter);

        // Carousel affordance: peeking edges + per-item snap
        final int itemSpace = getResources().getDimensionPixelSize(R.dimen.space_12);
        final int edgePeek = getResources().getDimensionPixelSize(R.dimen.space_24);
        rv.setClipToPadding(false);
        rv.setPadding(edgePeek, 0, edgePeek, 0);
        rv.addItemDecoration(new SpacesItemDecoration(itemSpace));
        new LinearSnapHelper().attachToRecyclerView(rv);

        fetchMovies();
    }

    private void setLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rv != null) rv.setAlpha(show ? 0.3f : 1f);
    }

    private void fetchMovies() {
        setLoading(true);

        // Mock mode: use canned data
        if (BuildConfig.MOCK_MODE) {
            try {
                JSONObject mockData = MockData.tmdbTrendingJson();
                JSONArray results = mockData.optJSONArray("results");
                items.clear();
                if (results != null) {
                    items.addAll(MovieContent.fromJsonArray(results));
                }
                adapter.notifyDataSetChanged();
                
                Log.d(TAG, "Mock movies loaded ✓ count=" + items.size());
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.empty_movies_message), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Mock data error", e);
                Toast.makeText(requireContext(), getString(R.string.error_load_content), Toast.LENGTH_SHORT).show();
            } finally {
                setLoading(false);
            }
            return;
        }

        // Real API call
        Log.d(TAG, "Fetching movies: " + TMDB_URL);

        ApiUtils.get(TMDB_URL, new ApiUtils.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject json = new JSONObject(body);
                    JSONArray results = json.optJSONArray("results");
                    items.clear();
                    if (results != null) {
                        items.addAll(MovieContent.fromJsonArray(results));
                    }
                    
                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Movies loaded ✓ count=" + items.size());
                            
                            if (items.isEmpty()) {
                                Toast.makeText(requireContext(), getString(R.string.empty_movies_message), Toast.LENGTH_SHORT).show();
                            }
                            setLoading(false);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse error (movies)", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), getString(R.string.error_load_content), Toast.LENGTH_SHORT).show();
                            setLoading(false);
                        });
                    }
                }
            }

            @Override
            public void onError(IOException e) {
                Log.e(TAG, "Network error (movies)", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoading(false);
                        Toast.makeText(requireContext(), getString(R.string.error_load_content), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}