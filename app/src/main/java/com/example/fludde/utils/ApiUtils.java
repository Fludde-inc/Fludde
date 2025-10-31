package com.example.fludde.utils;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.Nullable;

import com.example.fludde.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ApiUtils {

    private static final String TAG = "ApiUtils";

    public interface Callback {
        void onSuccess(String body);
        void onError(IOException e);
    }

    private static final OkHttpClient client = new OkHttpClient();

    private static @Nullable String apiKey = null;       // v3
    private static @Nullable String bearerToken = null;   // v4

    private ApiUtils() { }

    /** Prefer v4 bearer token; pass null to clear. */
    public static void setBearer(@Nullable String token) {
        bearerToken = token;
    }

    /** Legacy v3 api_key; pass null to clear. */
    public static void setApiKey(@Nullable String key) {
        apiKey = key;
    }

    /**
     * Handle API request failures by logging the status code and error details.
     * This method is called from various fragments when API requests fail.
     *
     * @param statusCode HTTP status code from the failed request
     * @param throwable The exception/error that occurred
     */
    public static void handleFailure(int statusCode, Throwable throwable) {
        if (throwable != null) {
            Log.e(TAG, "API request failed with status code: " + statusCode, throwable);
            Log.e(TAG, "Error message: " + throwable.getMessage());
        } else {
            Log.e(TAG, "API request failed with status code: " + statusCode + " (no throwable provided)");
        }
    }

    public static void get(String url, final Callback cb) {
        // ✅ ADD THIS - Validate URL first
        if (url == null || url.trim().isEmpty()) {
            if (cb != null) {
                cb.onError(new IOException("URL cannot be null or empty"));
            }
            return;
        }
        
        // Existing code continues below...
        if (BuildConfig.MOCK_MODE) {
            handleMockRequest(url, cb);
            return;
        }
        // ===== END MOCK MODE CHECK =====
        
        // ===== EXISTING: Real API call (unchanged) =====
        try {
            Request.Builder rb = new Request.Builder();

            // If no bearer, append v3 api_key as query parameter (legacy fallback).
            if (bearerToken == null || bearerToken.isEmpty()) {
                if (apiKey != null && !apiKey.isEmpty()) {
                    Uri uri = Uri.parse(url).buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    url = uri.toString();
                }
            }

            rb.url(url);

            // If bearer available, use Authorization header (preferred).
            if (bearerToken != null && !bearerToken.isEmpty()) {
                rb.addHeader("Authorization", "Bearer " + bearerToken);
            }

            Request request = rb.build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    if (cb != null) cb.onError(e);
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (Response res = response) {
                        if (!res.isSuccessful()) {
                            if (cb != null) cb.onError(new IOException("HTTP " + res.code() + " " + res.message()));
                            return;
                        }
                        String body = res.body() != null ? res.body().string() : "";
                        if (cb != null) cb.onSuccess(body);
                    }
                }
            });

        } catch (Exception e) {
            if (cb != null) cb.onError(new IOException("Request build error", e));
        }
    }

    // ===== NEW METHOD: Handle mock requests =====
    /**
     * Handle mock requests by routing to appropriate mock data based on URL.
     * Simulates network delay for realistic behavior.
     * 
     * @param url The requested URL
     * @param cb Callback to return mock data
     */
    private static void handleMockRequest(String url, final Callback cb) {
        // Simulate network delay (100-500ms)
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try {
                String mockResponse = null;
                
                // Route based on URL to appropriate mock data
                if (url.contains("themoviedb.org")) {
                    mockResponse = MockData.tmdbTrendingJson().toString();
                    Log.d(TAG, "Mock: Returning TMDB data");
                } else if (url.contains("itunes.apple.com")) {
                    mockResponse = MockData.itunesSearchJson().toString();
                    Log.d(TAG, "Mock: Returning iTunes data");
                } else if (url.contains("googleapis.com/books")) {
                    mockResponse = MockData.googleBooksJson().toString();
                    Log.d(TAG, "Mock: Returning Google Books data");
                }
                
                if (mockResponse != null && cb != null) {
                    cb.onSuccess(mockResponse);
                } else {
                    if (cb != null) {
                        cb.onError(new IOException("Mock: Unknown endpoint - " + url));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Mock error", e);
                if (cb != null) {
                    cb.onError(new IOException("Mock error", e));
                }
            }
        }, 100 + (long)(Math.random() * 400)); // Random delay between 100-500ms
    }
    // ===== END NEW METHOD =====
}