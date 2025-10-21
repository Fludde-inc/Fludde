package com.example.fludde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.BuildConfig;
import com.example.fludde.R;
import com.example.fludde.adapters.PostAdapter;
import com.example.fludde.model.PostUi;
import com.example.fludde.utils.MockData;

import java.util.ArrayList;
import java.util.List;

/** Home shows a friendly feed. In MOCK_MODE we always show canned posts. */
public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private View errorCard;
    private TextView tvError;
    private Button btnRetry;

    private final List<PostUi> items = new ArrayList<>();
    private PostAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rvHomeFeed);
        errorCard = v.findViewById(R.id.inlineError);
        tvError = errorCard != null ? errorCard.findViewById(R.id.tvErrorMessage) : null;
        btnRetry = errorCard != null ? errorCard.findViewById(R.id.btnRetry) : null;

        adapter = new PostAdapter(requireContext(), items);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        if (btnRetry != null) btnRetry.setOnClickListener(view -> load());

        load();
    }

    private void showError(boolean show, @Nullable String msg) {
        if (errorCard != null) errorCard.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show && tvError != null) tvError.setText(msg == null ? getString(R.string.generic_error_message) : msg);
    }

    private void load() {
        showError(false, null);
        items.clear();

        // Always show something in debug: mock posts
        if (BuildConfig.MOCK_MODE) {
            items.addAll(MockData.mockPosts());
            adapter.notifyDataSetChanged();
            return;
        }

        // (Non-mock path could query a backend; for now just surface empty state)
        showError(true, getString(R.string.empty_profile_message));
        adapter.notifyDataSetChanged();
    }
}
