package com.example.fludde.fragments.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.example.fludde.R;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MusicChildFragment extends Fragment {

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Need to define the child fragment layout
        return inflater.inflate(R.layout.fragment_child_music, container, false);
    }


}
