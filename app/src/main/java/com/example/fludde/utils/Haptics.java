package com.example.fludde.utils;

import android.content.Context;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;

/** Small helpers to provide discoverable, consistent haptics. */
public final class Haptics {
    private Haptics() {}

    /** Light haptic for long-press (discoverability). */
    public static void longPress(View v) {
        if (v == null) return;
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    /** Subtle confirmation haptic (e.g., successful toggle). */
    public static void tick(View v) {
        if (v == null) return;
        if (Build.VERSION.SDK_INT >= 21) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    /** Error/denied haptic. */
    public static void error(View v) {
        if (v == null) return;
        v.performHapticFeedback(HapticFeedbackConstants.REJECT);
    }
}
