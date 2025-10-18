package com.example.fludde.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Simple horizontal spacing between carousel cards.
 * Pair with RecyclerView edge padding + clipToPadding(false) to allow peeking items.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration(int space) { this.space = space; }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (pos == RecyclerView.NO_POSITION) return;
        // Add space to the end of each item; edge padding on the RecyclerView handles left/right peeks.
        outRect.right = space;
    }
}
