package com.example.fludde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fludde.R;
import com.example.fludde.fragments.child.BookChildFragment;
import com.example.fludde.fragments.child.MovieChildFragment;
import com.example.fludde.fragments.child.MusicChildFragment;
import com.example.fludde.utils.ErrorHandler;

/**
 * ComposeParentFragment - Refactored to use ErrorHandler utility and string resources.
 * 
 * Changes:
 * - All hardcoded Toast messages moved to strings.xml
 * - All Toast.makeText() replaced with ErrorHandler methods
 * - Consistent error handling
 */
public class ComposeParentFragment extends Fragment {

    private static final String TAG = "ComposeParentFragment";

    private AutoCompleteTextView actCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_parent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actCategory = view.findViewById(R.id.actCategory);

        // Setup category dropdown
        String[] categories = {"Movies", "Books", "Music"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        );
        actCategory.setAdapter(adapter);

        // Handle category selection
        actCategory.setOnItemClickListener((parent, v, pos, id) -> {
            switch (pos) {
                case 0:
                default:
                    insertChild(new MovieChildFragment());
                    // ✅ Using ErrorHandler with string resource
                    ErrorHandler.showToast(requireContext(), R.string.content_selected_movies);
                    break;
                case 1:
                    insertChild(new BookChildFragment());
                    // ✅ Using ErrorHandler with string resource
                    ErrorHandler.showToast(requireContext(), R.string.content_selected_books);
                    break;
                case 2:
                    insertChild(new MusicChildFragment());
                    // ✅ Using ErrorHandler with string resource
                    ErrorHandler.showToast(requireContext(), R.string.content_selected_music);
                    break;
            }
        });

        // Default
        insertChild(new MovieChildFragment());
    }

    private void insertChild(Fragment child) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.child_fragment_container, child);
        ft.commit();
    }
}