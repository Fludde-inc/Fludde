package com.example.fludde.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.fludde.R;
import com.example.fludde.adapters.SearchFragmentAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Search screen:
 * - Prominent search field + primary button (consistent placement)
 * - Empty state guidance when no results
 * - Keyboard action (IME_ACTION_SEARCH / Enter) equals tapping the button
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private RecyclerView rvUserQuery;
    private SearchFragmentAdapter searchFragmentAdapter;
    private final List<ParseUser> allUsers = new ArrayList<>();

    private TextInputEditText etSearchFieldUser;
    private Button btSearch;

    // Empty state views
    private View emptyState;

    public SearchFragment() { }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUserQuery = view.findViewById(R.id.rvUserQuery);
        etSearchFieldUser = view.findViewById(R.id.etSearchFieldUser);
        btSearch = view.findViewById(R.id.btSearch);
        emptyState = view.findViewById(R.id.emptyState);

        searchFragmentAdapter = new SearchFragmentAdapter(requireContext(), allUsers);
        rvUserQuery.setAdapter(searchFragmentAdapter);
        rvUserQuery.setLayoutManager(new LinearLayoutManager(getContext()));

        // Primary button tap
        btSearch.setOnClickListener(v -> performSearch(getQueryText()));

        // Keyboard action (Search / Enter) == button tap
        etSearchFieldUser.setOnEditorActionListener((tv, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(getQueryText());
                handled = true;
            } else if (event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                performSearch(getQueryText());
                handled = true;
            }
            return handled;
        });

        // Show initial guidance
        showEmptyState(true);
    }

    private String getQueryText() {
        String q = etSearchFieldUser.getText() != null ? etSearchFieldUser.getText().toString().trim() : "";
        return q;
    }

    private void performSearch(String userName) {
        hideKeyboard();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getContext(), getString(R.string.hint_search_user), Toast.LENGTH_SHORT).show();
            allUsers.clear();
            searchFragmentAdapter.notifyDataSetChanged();
            showEmptyState(true);
            return;
        }
        lookForUser(userName);
    }

    private void showEmptyState(boolean show) {
        if (emptyState != null) emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View v = requireActivity().getCurrentFocus();
            if (imm != null && v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception ignore) { }
    }

    private void lookForUser(String userName) {
        // Make the same action work for button and keyboard
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // Use a contains match for a friendlier search experience
        query.whereContains("username", userName);

        // Optional: small limit to keep UI snappy; adjust as needed
        query.setLimit(50);

        query.findInBackground((users, e) -> {
            if (e != null) {
                Log.e(TAG, "Search failed", e);
                Toast.makeText(getContext(), getString(R.string.error_search), Toast.LENGTH_SHORT).show();
                allUsers.clear();
                searchFragmentAdapter.notifyDataSetChanged();
                showEmptyState(true);
                return;
            }

            allUsers.clear();
            if (users != null) {
                allUsers.addAll(users);
            }
            searchFragmentAdapter.notifyDataSetChanged();

            // Empty state guidance if nothing found
            showEmptyState(allUsers.isEmpty());
        });
    }
}
