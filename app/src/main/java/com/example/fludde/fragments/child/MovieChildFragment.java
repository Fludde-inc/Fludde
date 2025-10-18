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

import com.example.fludde.BuildConfig;
import com.example.fludde.R;
import com.example.fludde.adapters.MovieChildAdapter;
import com.example.fludde.model.MovieContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.SpacesItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MovieChildFragment extends Fragment {

    private RecyclerView rv;
    private MovieChildAdapter adapter;
    private final List<MovieContent> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rvMovieHorizontalView);
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

    private void fetchMovies() {
        String url = "https://api.themoviedb.org/3/trending/movie/day?api_key=" + BuildConfig.TMDB_API_KEY;
        ApiUtils.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.optJSONArray("results");
                    if (results != null) {
                        items.clear();
                        items.addAll(MovieContent.fromJsonArray(results));
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
