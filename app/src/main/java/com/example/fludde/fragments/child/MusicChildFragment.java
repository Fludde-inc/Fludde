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
import com.example.fludde.adapters.MusicChildAdapter;
import com.example.fludde.model.MusicContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.SpacesItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MusicChildFragment extends Fragment {

    private static final String TAG = "MusicChildFragment";

    private static final String ITUNES_URL =
            "https://itunes.apple.com/search?term=pop&entity=song&limit=20";

    private RecyclerView rv;
    private ProgressBar progress;
    private MusicChildAdapter adapter;
    private final List<MusicContent> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rvMusicHorizontalView);
        progress = v.findViewById(R.id.progressBar);

        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new MusicChildAdapter(requireContext(), items, new MusicChildAdapter.OnMusicContentListener() {
            @Override public void onMusicContentClick(int position) {}
            @Override public void onMusicContentLongClick(int position) {}
        });
        rv.setAdapter(adapter);

        final int itemSpace = getResources().getDimensionPixelSize(R.dimen.space_12);
        final int edgePeek = getResources().getDimensionPixelSize(R.dimen.space_24);
        rv.setClipToPadding(false);
        rv.setPadding(edgePeek, 0, edgePeek, 0);
        rv.addItemDecoration(new SpacesItemDecoration(itemSpace));
        new LinearSnapHelper().attachToRecyclerView(rv);

        fetchMusic();
    }

    private void setLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rv != null) rv.setAlpha(show ? 0.3f : 1f);
    }

    private void fetchMusic() {
        setLoading(true);
        Log.d(TAG, "Fetching music: " + ITUNES_URL);

        ApiUtils.get(ITUNES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.optJSONArray("results");
                    items.clear();
                    if (results != null) {
                        items.addAll(MusicContent.fromJsonArray(results));
                    }
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Music loaded ✓ count=" + items.size());
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.empty_music_message), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse error (music)", e);
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
