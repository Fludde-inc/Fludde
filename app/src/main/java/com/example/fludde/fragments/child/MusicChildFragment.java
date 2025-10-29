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
import com.example.fludde.adapters.MusicChildAdapter;
import com.example.fludde.model.MusicContent;
import com.example.fludde.utils.ApiUtils;
import com.example.fludde.utils.ErrorHandler;
import com.example.fludde.utils.MockData;
import com.example.fludde.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Horizontal music carousel (iTunes). */
public class MusicChildFragment extends Fragment {

    private static final String TAG = "MusicChildFragment";
    private static final String ITUNES_URL =
            "https://itunes.apple.com/search?term=miami&entity=song&limit=25";

    private RecyclerView rv;
    private ProgressBar progress;

    private final List<MusicContent> items = new ArrayList<>();
    private MusicChildAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        // carousel affordance
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

        if (BuildConfig.MOCK_MODE) {
            try {
                JSONObject mock = MockData.itunesSearchJson();
                JSONArray results = mock.optJSONArray("results");
                items.clear();
                if (results != null) {
                    items.addAll(MusicContent.fromJsonArray(results));
                }
                adapter.notifyDataSetChanged();
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.empty_music_message), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Mock music parse error", e);
                ErrorHandler.showToast(requireContext(), R.string.error_load_content);
            } finally {
                setLoading(false);
            }
            return;
        }

        Log.d(TAG, "Fetching music: " + ITUNES_URL);

        ApiUtils.get(ITUNES_URL, new ApiUtils.Callback() {
            @Override public void onSuccess(String body) {
                try {
                    JSONObject response = new JSONObject(body);
                    JSONArray results = response.optJSONArray("results");
                    items.clear();
                    if (results != null) {
                        items.addAll(MusicContent.fromJsonArray(results));
                    }
                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (items.isEmpty()) {
                            ErrorHandler.showToast(requireContext(), R.string.empty_music_message);
                        }
                        setLoading(false);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Parse error (music)", e);
                    requireActivity().runOnUiThread(() -> {
                        ErrorHandler.showToast(requireContext(), R.string.error_load_content);
                        setLoading(false);
                    });
                }
            }

            @Override public void onError(IOException e) {
                Log.e(TAG, "Network error (music)", e);
                requireActivity().runOnUiThread(() -> {
                    ErrorHandler.showNetworkError(requireContext(), R.string.error_load_content);
                    setLoading(false);
                });
            }
        });
    }
}
