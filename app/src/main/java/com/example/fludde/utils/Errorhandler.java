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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Centralized error handling utility for consistent error messages and logging.
 * 
 * Features:
 * - Toast error messages
 * - Inline error views
 * - Automatic error type detection
 * - Consistent logging
 * - String resource usage
 * 
 * Usage:
 * - ErrorHandler.showToast(context, R.string.error_network);
 * - ErrorHandler.handleApiError(context, exception);
 * - ErrorHandler.showInlineError(errorView, R.string.error_load_feed);
 */
public final class ErrorHandler {

    private static final String TAG = "ErrorHandler";

    private ErrorHandler() {
        // Prevent instantiation
    }

    // ========== TOAST METHODS ==========

    /**
     * Show a short toast with an error message from string resources.
     * 
     * @param context Context for showing toast
     * @param messageResId String resource ID for the error message
     */
    public static void showToast(@NonNull Context context, @StringRes int messageResId) {
        showToast(context, context.getString(messageResId), Toast.LENGTH_SHORT);
    }

    /**
     * Show a toast with custom message and duration.
     * 
     * @param context Context for showing toast
     * @param message Error message to display
     * @param duration Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public static void showToast(@NonNull Context context, @NonNull String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * Show a long toast with an error message from string resources.
     * 
     * @param context Context for showing toast
     * @param messageResId String resource ID for the error message
     */
    public static void showToastLong(@NonNull Context context, @StringRes int messageResId) {
        showToast(context, context.getString(messageResId), Toast.LENGTH_LONG);
    }

    // ========== INLINE ERROR METHODS ==========

    /**
     * Show an inline error view with a message from string resources.
     * 
     * @param errorView The error container view (usually includes error message TextView)
     * @param messageResId String resource ID for the error message
     */
    public static void showInlineError(@NonNull View errorView, @StringRes int messageResId) {
        showInlineError(errorView, errorView.getContext().getString(messageResId));
    }

    /**
     * Show an inline error view with a custom message.
     * 
     * @param errorView The error container view
     * @param message Error message to display
     */
    public static void showInlineError(@NonNull View errorView, @NonNull String message) {
        errorView.setVisibility(View.VISIBLE);
        
        // Try to find TextView for error message (common pattern)
        TextView tvError = errorView.findViewById(R.id.tvErrorMessage);
        if (tvError != null) {
            tvError.setText(message);
        }
    }

    /**
     * Hide an inline error view.
     * 
     * @param errorView The error container view to hide
     */
    public static void hideInlineError(@NonNull View errorView) {
        errorView.setVisibility(View.GONE);
    }

    // ========== API ERROR HANDLING ==========

    /**
     * Handle API errors intelligently by detecting error type and showing appropriate message.
     * Combines logging and user-facing error display.
     * 
     * @param context Context for showing error
     * @param throwable The exception that occurred
     */
    public static void handleApiError(@NonNull Context context, @Nullable Throwable throwable) {
        handleApiError(context, throwable, null);
    }

    /**
     * Handle API errors with optional inline error view.
     * 
     * @param context Context for showing error
     * @param throwable The exception that occurred
     * @param inlineErrorView Optional inline error view (null to show toast instead)
     */
    public static void handleApiError(@NonNull Context context, @Nullable Throwable throwable, 
                                      @Nullable View inlineErrorView) {
        int errorMessageId = determineErrorMessage(throwable);
        String errorMessage = context.getString(errorMessageId);
        
        // Log the error
        if (throwable != null) {
            Log.e(TAG, "API Error: " + errorMessage, throwable);
        } else {
            Log.e(TAG, "API Error: " + errorMessage);
        }

        // Show error to user
        if (inlineErrorView != null) {
            showInlineError(inlineErrorView, errorMessageId);
        } else {
            showToast(context, errorMessageId);
        }
    }

    /**
     * Handle API errors with HTTP status code information.
     * 
     * @param context Context for showing error
     * @param statusCode HTTP status code
     * @param throwable The exception that occurred
     */
    public static void handleApiError(@NonNull Context context, int statusCode, 
                                      @Nullable Throwable throwable) {
        handleApiError(context, statusCode, throwable, null);
    }

    /**
     * Handle API errors with HTTP status code and optional inline error view.
     * 
     * @param context Context for showing error
     * @param statusCode HTTP status code
     * @param throwable The exception that occurred
     * @param inlineErrorView Optional inline error view
     */
    public static void handleApiError(@NonNull Context context, int statusCode, 
                                      @Nullable Throwable throwable, @Nullable View inlineErrorView) {
        // Log with status code
        ApiUtils.handleFailure(statusCode, throwable);
        
        // Determine appropriate error message based on status code
        int errorMessageId;
        if (statusCode == 401 || statusCode == 403) {
            errorMessageId = R.string.error_auth_required;
        } else if (statusCode == 404) {
            errorMessageId = R.string.error_load_content;
        } else if (statusCode == 429) {
            errorMessageId = R.string.error_api_limit;
        } else if (statusCode >= 500) {
            errorMessageId = R.string.error_generic;
        } else {
            errorMessageId = determineErrorMessage(throwable);
        }

        // Show error to user
        if (inlineErrorView != null) {
            showInlineError(inlineErrorView, errorMessageId);
        } else {
            showToast(context, errorMessageId);
        }
    }

    // ========== AUTHENTICATION ERRORS ==========

    /**
     * Show authentication-related error (login/signup).
     * 
     * @param context Context for showing error
     * @param messageResId String resource ID for the auth error
     */
    public static void showAuthError(@NonNull Context context, @StringRes int messageResId) {
        showToast(context, messageResId);
        Log.w(TAG, "Authentication error: " + context.getString(messageResId));
    }

    // ========== VALIDATION ERRORS ==========

    /**
     * Show validation error (e.g., empty fields).
     * 
     * @param context Context for showing error
     * @param messageResId String resource ID for the validation error
     */
    public static void showValidationError(@NonNull Context context, @StringRes int messageResId) {
        showToast(context, messageResId);
        Log.w(TAG, "Validation error: " + context.getString(messageResId));
    }

    // ========== HELPER METHODS ==========

    /**
     * Determine the appropriate error message based on exception type.
     * 
     * @param throwable The exception to analyze
     * @return String resource ID for the error message
     */
    @StringRes
    private static int determineErrorMessage(@Nullable Throwable throwable) {
        if (throwable == null) {
            return R.string.error_generic;
        }

        // Network connectivity issues
        if (throwable instanceof UnknownHostException) {
            return R.string.error_network;
        }

        // Timeout issues
        if (throwable instanceof SocketTimeoutException) {
            return R.string.error_timeout;
        }

        // Generic IOException (could be various network issues)
        if (throwable instanceof IOException) {
            String message = throwable.getMessage();
            if (message != null) {
                if (message.contains("401") || message.contains("403")) {
                    return R.string.error_auth_required;
                }
                if (message.contains("429")) {
                    return R.string.error_api_limit;
                }
                if (message.contains("timeout")) {
                    return R.string.error_timeout;
                }
            }
            return R.string.error_network;
        }

        // Default to generic error
        return R.string.error_generic;
    }

    /**
     * Log error for debugging purposes.
     * 
     * @param tag Log tag (usually class name)
     * @param message Error message
     * @param throwable Optional exception
     */
    public static void logError(@NonNull String tag, @NonNull String message, 
                                @Nullable Throwable throwable) {
        if (throwable != null) {
            Log.e(tag, message, throwable);
        } else {
            Log.e(tag, message);
        }
    }

    /**
     * Log warning for debugging purposes.
     * 
     * @param tag Log tag (usually class name)
     * @param message Warning message
     */
    public static void logWarning(@NonNull String tag, @NonNull String message) {
        Log.w(tag, message);
    }
}