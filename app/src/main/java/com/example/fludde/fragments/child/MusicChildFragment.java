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
import com.example.fludde.adapters.MusicChildAdapter;
import com.example.fludde.utils.ApiUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MusicChildFragment extends Fragment {
    private static final String TAG = "MusicChildFragment";
    private RecyclerView rvMusicHorizontalView;
    private MusicChildAdapter adapter;

    private static final String SPOTIFY_API_KEY = BuildConfig.SPOTIFY_KEY;
    private static final String SPOTIFY_URL = "https://api.spotify.com/v1/tracks?api_key=" + SPOTIFY_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_music, container, false);

        rvMusicHorizontalView = view.findViewById(R.id.rvMusicHorizontalView);
        adapter = new MusicChildAdapter(getContext());
        rvMusicHorizontalView.setAdapter(adapter);

        fetchPopularTracks();

        return view;
    }

    private void fetchPopularTracks() {
        ApiUtils.get(SPOTIFY_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("tracks");
                    adapter.setTracks(results);
                    Log.d(TAG, "Tracks fetched successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse track data", e);
                    Toast.makeText(getContext(), "Failed to load tracks. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to fetch tracks from API", throwable);
                ApiUtils.handleFailure(statusCode, throwable);
                Toast.makeText(getContext(), "Failed to fetch tracks. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
