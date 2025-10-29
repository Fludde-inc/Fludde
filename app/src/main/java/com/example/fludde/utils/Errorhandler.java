package com.example.fludde.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.fludde.R;

/**
 * Centralized error handling utility for the Fludde app.
 * Provides methods for displaying toasts, inline errors, and logging.
 */
public final class ErrorHandler {
    private static final String TAG = "ErrorHandler";

    private ErrorHandler() {}

    /* ---------------------- Toast helpers ---------------------- */

    public static void showToast(@NonNull Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(@NonNull Context context, @NonNull String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(@NonNull Context context, @NonNull String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static void showToastLong(@NonNull Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(@NonNull Context context, @NonNull String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /* -------------------- Inline error helpers -------------------- */

    public static void showInlineError(@Nullable View cardView, @StringRes int messageResId) {
        if (cardView == null) return;
        TextView tv = cardView.findViewById(R.id.tvErrorMessage);
        if (tv != null) tv.setText(messageResId);
        cardView.setVisibility(View.VISIBLE);
    }

    public static void hideInlineError(@Nullable View cardView) {
        if (cardView != null) cardView.setVisibility(View.GONE);
    }

    /* -------------------- Domain-specific helpers -------------------- */

    public static void showAuthError(@NonNull Context context, @StringRes int resId) {
        showToast(context, resId);
    }

    public static void showAuthError(@NonNull Context context, @NonNull String message) {
        showToast(context, message);
    }

    public static void showValidationError(@NonNull Context context, @StringRes int resId) {
        showToast(context, resId);
    }

    public static void showValidationError(@NonNull Context context, @NonNull String message) {
        showToast(context, message);
    }

    public static void showNetworkError(@NonNull Context context, @StringRes int resId) {
        showToast(context, resId);
    }

    public static void showNetworkError(@NonNull Context context, @NonNull String message) {
        showToast(context, message);
    }

    /* -------------------- API error logging -------------------- */

    public static void handleApiError(int statusCode, @Nullable Throwable t) {
        if (t != null) {
            Log.e(TAG, "HTTP " + statusCode, t);
        } else {
            Log.e(TAG, "HTTP " + statusCode + " (no throwable)");
        }
    }

    /* -------------------- NEW: Logging helpers -------------------- */

    /**
     * Log a warning message
     * @param tag The log tag
     * @param message The warning message
     */
    public static void logWarning(@NonNull String tag, @NonNull String message) {
        Log.w(tag, message);
    }

    /**
     * Log an error with exception
     * @param tag The log tag
     * @param message The error message
     * @param throwable The exception that occurred
     */
    public static void logError(@NonNull String tag, @NonNull String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            Log.e(tag, message, throwable);
        } else {
            Log.e(tag, message);
        }
    }

    /**
     * Log an error without exception
     * @param tag The log tag
     * @param message The error message
     */
    public static void logError(@NonNull String tag, @NonNull String message) {
        Log.e(tag, message);
    }
}