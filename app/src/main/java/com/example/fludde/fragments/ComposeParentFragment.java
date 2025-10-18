package com.example.fludde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fludde.R;
import com.example.fludde.fragments.child.BookChildFragment;
import com.example.fludde.fragments.child.MovieChildFragment;
import com.example.fludde.fragments.child.MusicChildFragment;
import com.example.fludde.utils.FragmentTransitions;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

/**
 * Parent "Compose" fragment:
 * - Subtle fast transitions between child tabs.
 * - Exposed dropdown selection for Movies / Books / Music.
 * - Child lists sized with stable item dimensions (see dimens).
 */
public class ComposeParentFragment extends Fragment {
    private MaterialAutoCompleteTextView actCategory;
    private final String[] dropDownItems = new String[]{"Movies", "Books", "Music"};

    public ComposeParentFragment() {}

    public static ComposeParentFragment newInstance(String p1, String p2) {
        ComposeParentFragment fragment = new ComposeParentFragment();
        Bundle args = new Bundle();
        args.putString("param1", p1);
        args.putString("param2", p2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_parent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        actCategory = view.findViewById(R.id.actCategory);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, dropDownItems);
        actCategory.setAdapter(adapter);

        actCategory.setOnItemClickListener((parent, v, pos, id) -> {
            switch (pos) {
                case 0:
                default:
                    insertChild(new MovieChildFragment());
                    Toast.makeText(getContext(), "You selected Movies", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    insertChild(new BookChildFragment());
                    Toast.makeText(getContext(), "You selected Books", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    insertChild(new MusicChildFragment());
                    Toast.makeText(getContext(), "You selected Music", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Default
        actCategory.setText(dropDownItems[0], false);
        insertChild(new MovieChildFragment());
    }

    private void insertChild(Fragment child) {
        FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        FragmentTransitions.applyFastFade(tx);
        tx.replace(R.id.child_fragment_container, child).commit();
    }
}
