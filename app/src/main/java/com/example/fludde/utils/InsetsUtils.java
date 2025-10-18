package com.example.fludde.utils;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Centralized, duplicate-free system bar inset handling.
 * - Enables edge-to-edge on the window.
 * - Pads the fragment container by status bar + nav bar insets.
 * - Adds bottom inset to BottomNavigationView so it sits above the gesture area.
 * - Accounts for the BottomNavigationView's measured height so content isn't hidden.
 */
public final class InsetsUtils {

    private InsetsUtils() {}

    private static final class Padding {
        final int l, t, r, b;
        Padding(View v) { l = v.getPaddingLeft(); t = v.getPaddingTop(); r = v.getPaddingRight(); b = v.getPaddingBottom(); }
    }

    public static void applyEdgeToEdge(Activity activity, View container, BottomNavigationView bottomNav) {
        // Tell the system we're handling insets ourselves.
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        final Padding containerBase = new Padding(container);
        final Padding navBase = new Padding(bottomNav);

        ViewCompat.setOnApplyWindowInsetsListener(container, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Add status bar/top insets and left/right for cutouts.
            int top = containerBase.t + sysBars.top;
            int left = containerBase.l + sysBars.left;
            int right = containerBase.r + sysBars.right;
            // Reserve space for both the system navigation area and the bottom nav view height.
            int bottom = containerBase.b + sysBars.bottom + bottomNav.getHeight();
            v.setPadding(left, top, right, bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int left = navBase.l + sysBars.left;
            int right = navBase.r + sysBars.right;
            int bottom = navBase.b + sysBars.bottom;
            v.setPadding(left, navBase.t, right, bottom);
            return insets;
        });

        // If the bottom nav's height changes (first layout, configuration change), re-dispatch insets.
        bottomNav.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                ViewCompat.requestApplyInsets(container));

        // Kick the first dispatch.
        activity.getWindow().getDecorView().post(() -> {
            ViewCompat.requestApplyInsets(container);
            ViewCompat.requestApplyInsets(bottomNav);
        });
    }
}
