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
import com.example.fludde.adapters.MovieChildAdapter;
import com.example.fludde.utils.ApiUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieChildFragment extends Fragment {
    private static final String TAG = "MovieChildFragment";
    private RecyclerView rvMovieHorizontalView;
    private MovieChildAdapter adapter;

    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_movie, container, false);

        rvMovieHorizontalView = view.findViewById(R.id.rvMovieHorizontalView);
        adapter = new MovieChildAdapter(getContext());
        rvMovieHorizontalView.setAdapter(adapter);

        fetchPopularMovies();

        return view;
    }

    private void fetchPopularMovies() {
        ApiUtils.get(MOVIE_DB_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    adapter.setMovies(results);
                    Log.d(TAG, "Movies fetched successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse movie data", e);
                    Toast.makeText(getContext(), "Failed to load movies. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to fetch movies from API", throwable);
                ApiUtils.handleFailure(statusCode, throwable);
                Toast.makeText(getContext(), "Failed to fetch movies. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
